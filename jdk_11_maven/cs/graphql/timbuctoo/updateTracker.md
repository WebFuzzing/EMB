 - how to deal with schema updates? Can we make our own schema updates always backwards compatible?
   - never remove a property (but place empty properties in deprecated)
   - never switch between list en single entity
      - Never go from list to non-list. Put a proper suffix behind list properties (List) deprecate the non-list variant
      and have it return the first item.
   - always make all normal properties nullable (lists can still be non-nullable)

- [ ] rename tripleStore quadstore and store the graph
- [ ] extract graphProcessor as separate object instead of inheritance
- [ ] make BdbDatastoreFactory that creates DataSets and get's a dataproviderFactory injected from the config.
      pull addLog, addFile etc. up to dataprovider



----------------------------------------
# Subscribing to changes

The core technical features of timbuctoo that underpin everything that it does is: 
 - Accepting arbitrary data. It's really hard to come up with data that our database can't handle, as opposed to a 
   relational database that can't handle multiple values in columns or an object store that can't contain pointers.
 - Generating a coherent, ever backwards compatible schema out of that data. So you can see at a glance what properties
   are used. How types interact etc.
 - Subscribing to changes on the data in the database. So you can fill a data-store optimized for certain queries.

[...maybe leave this out]

## Overview

Timbuctoo should allow you to subscribe to updates to the dataset. This is used both within the java program to
correctly update the various derived datasets and from an external service using the HTTP api.

*Internally* we use a push model. You implement the interface RawPatchListener, or OptimizedPatchListener and it will be
called whenever a new changeset is applied. RawPatchListener is called with the raw, unsorted, stream of RDF triple
assertions and retractions. It might receive two identical additions in a row, or an addition immediately followed by a
retraction. OptimizedPatchListener is called with the collected changes per subject without duplicates and assertions
that are immediately retracted.

*Externally* we use a pull model. You send an http query and you will get a list with what changed. Since this list can
be rather big you only get the first x items. You also get a token that allows you to request the next x items. Once you
get an empty list you're up to date. You then keep polling with the same token until you get new changes.

## Recap: Querying the current state

Before diving into an explanation of what data we need to store to support the push and pull model, here's a quick recap
of how we currently store the triples. 

One approach of making quad stores queryable is to index the Subjects, Predicates, Objects and Graphs in different
indexes each sorted in a different way. This is the approach taken by the database datomic, and also explained in []().
In short:

 - to easily get the properties of a given subject (i.e. a row in a database) you query a S,P,O index because you can do
 a binary search that leads you to the correct subject and then you can iterate over the rows to find all the predicates
 belonging to that subject.
 - to get all the subjects with a given predicate (like finding all values for a given column) you index P,S,O
 - to do a join you simply look up the Object of an S,P,O triple in the S,P,O index (no need to create a new HashTable
 because you already have the index)
 - to index property contents indexing the value directly is usually too simple an approach because you want extra 
 search options like stemming or case insensitivity so you use a different index.
 - To walk a foreign key in reverse you use an O,P,S index. That you then only fill with triples whose object is a URI.

In timbuctoo we use this idea to power our graphql query engine. Timbuctoo's goal is not to implement an efficient 
generic query engine, but rather to implement a few specific query strategies that allow you to get all data out, 
unfiltered. This makes the query engine a lot simpler. We store the RDF data indexed in 2 different ways (and we also
store the raw source files of course).

The quadStore:: This store contains a sorted list of all the quads in S,P,O order. But also their inversions (so you can
query for incoming relations). It is thus a combination of an S,P,O store and an O,P,S store. Storing these together 
means we have to deal with only one index when listing the properties of a subject.

The CollectionIndex:: This store contains a sorted list of all subjects that are ever referenced as RDF type and the
subjects that reference them. It is thus an O,[P],S store for one specific predicate and that predicate is left out
(since it's implicit). *NOTE:* While writing this summary I realise that we could just keep track of all subjects that
are ever used as types and store those separately (that's a much shorter list) and then use the quadStore as the
collection index.

Together these stores allow us to list the available subjects of a specific type and their properties. And to walk from
these subjects to the subjects that they reference and are referenced by. This is exposed through the graphql API.

We also keep track of the _schema_ i.e. what subjects of what type use which predicates, and we might index specific rdf
data in a separate index. For example: we currently index the raw uploads in a separate index to make the rml mapper's
job easier and faster.

## What we need to build to make querying for changes possible

### The push model

#### requirements
The push model has the following requirements:

  1. The listener should be called once for each subject per change
  2. The listener should receive all additions and deletions per subject at once.
  3. The listener might also need the current state of the datastore (i.e. the state after applying the change). It can
     then easily calculate the state before applying the change.

Changes go into timbuctoo as an ordered list of documents that each are applied atomically. So a change might affect 1
triple, or 50 or a million. A change is a patch detailing what triples to insert and what triples to remove. Removing a
triple that doesn't exist is a no-op, just like inserting a triple that already exists. Since inserting and removing are
idempotent we talk about Asserting and Retracting statements (triples). Asserting a triple and retracting it later in
the same change is also effectively a no-op.

So to store the data related to changes we at least need to keep track of two other pieces of information: the Change 
Number (CN) and whether a change was an Assertion or a Retraction (AR).

#### implementation
We can fulfill the above requirements by adding two more stores. We change the quadStoreUpdater to note which changes to
the quadStore were actual changes (using !putResult.isUpdate() for puts and deleteResult == OperationStatus.SUCCESS for
deletes) and update these two stores with the actual changes:

An updatedPerPatch store:: A CN,S store. When we update the triple store we should store what subjects where affected by
a change. We can do this by storing changeNumber,subjectUri. This allows us to call the OptimizedPatchListeners with
only those subjects that were actually affected by a given change, and to make sure that we call the listener only once
for each subject.

A True-Patch store:: A S,CN,P,AR,O store. When we update the triple store we should take note if our updates are
actually updating the value and store those updates ordered by subject, then by changeNumber and then by predicate. This
allows us to provide the OptimizedPatchListeners with exactly those predicates that were changed.

For the push model we don't need to keep this information after the push has been completed, but we will need it for the 
pull model later on so we keep this information forever and store it in the same type of datastores that we use for the
QuadStore and CollectionIndex.

### The pull model

#### requirements
The pull model is a bit more complicated. While pushing we lock the dataset, meaning that there's no new data coming in
while a Listener is processing the data. Once all listeners are finished we start processing the next patch. When
pulling this is not a feasible approach because the external system might go down, their might be a network partition or
whatever. Also, the data can be too big to return to the puller in one http request. Together this means that a pull
will be spread out over multiple requests, but the dataset might have changed between the initial request and the "next
page" request. Timbuctoo should take care that the "next page" requests show the data as it was during the initial
request and ignore all further changes. Once the last page has been requested timbuctoo should show the initial page
again with all the changes that have arrived since the last "initial" request.


So the requirements are:

 - You should be able to do a query and get the current state of all the items in the dataset, without it changing 
   under your feet when you do pagination. (we might want to base the default collection listing on this, if it is fast
   enough)
 - once you've received all the items you should be able to query for all the items that changed since the last query.
 - While querying for updates you should be able to follow the graph and do nested queries (so you can easily update a
   denormalized datastore)
    - *Note:* When querying `{authorList { name, hasWritten { title } } }` You should not see an author first because its
      name was changed and then again because he wrote a book whose title was also changed. Because that would be an 
      exact duplicate. Timbuctoo should show the author only once. The book might appear twice if it was written by two 
      authors.
 - You should be able to query using the normal schema that you also use for current-time queries (this means that a 
   data change must not result in a backwards incompatible schema change because then your query might break during 
   pagination. We can do this, but that's a separate issue.)
 - You should also be able to get a list of what changed. Since this is a pull model that's a list of what changed since
   the last time you queried. This should also not show changes that arrived while you're paginating.

#### implementation

I propose to modify the graphql schema so that it looks like this:

```graphql
{
  # changeList lists all the subjects that have changed and per subject all the changes
  # resumeToken is used for pagination and after the last page has been reached it is used for getting the next update.
  # so the client can just keep pulling using resumeToken
  changeList(resumeToken: "")  {
    resumeToken #the next token
    uri
    changes(resumeToken: "", propertyNames: [""], predicateUris:[]) {
      propertyName # the graphql name of the changed property
      predicateUri # the rdf name of the changed property
      value # the new value
      changeType # whether it was an Assertion or Retraction
    }
  }
  changeList(uri: "")  {
    uri
    # also contains changes(...) { ... }
  }

  #The main change is that a personList is now listed in change order (oldest first) and that the resumeToken takes the
  #latest change into account so that the list does not change under your feet. We might add an extra token that does 
  #allow for getting the next page including all changes.
  das_PersonList(resumeToken: "") {
    # also contains changes(...) { ... }

    schema_personName { value }
    schema_birthPlace  { 
      schema_name  { value }
  
      # also contains changes(...) { ... }
    }
    # lists of course have their own pagination. This pagination should also not update under your feet
    foaf_friendsList(resumeToken: "") {
      foaf_name { value }
      # also contains changes(...) { ... }
    }
  }
}
```

To implement this we need to do the following on top of the above two stores: 

1. First I update a triple store using the rdf-patch
2. 
   (a) and add them to a "true-patch" store which contains the sorted by subject,patchNo,predicate,add/del,value
   (b) and add them to a "updatedPerPatch" store wich contains subjects sorted by patchNo,subject
3. Based on these I can have a method that gives all subjects updated per patch and their changes. This enables 
   subscriptions for:
   - the collectionIndex (because I know which collection changes happened). I sort the collection index by patchNo so 
     that we can easily get the most recently updated entities within a collection. (I can now also correctly update a 
     tim_unknown index)
   - the schema store
   - the rml datasource (because I know which entities are of the correct type) (this makes that datasource less 
     dependant on the order of the RDF)
(when changing the QuadStore data format I should also add the quad. Because it's kinda lame that we're not storing it 
and it's way easier to change it now then after september)

-------
4. I can then write a graphql datafetcher that you can use to poll for changes (above was push).
    - [ ] work out polling with tokens (see tricky case below)
    - [ ] work out the schema




The tricky case is when:

you're geting all changes since version 3. We're currently at version 12. We encounter an item that's updated at version
4. But there's a linked item updated at version 10 so we postpone displaying the item. The next page starts from version
6. But we need to remember that we want the changes since version 3 (and not 4)

At some point an item is updated. We then want to see all it's changes since the version that we've already observed. So
If we observed it while getting the changes of version 4-12 we want to see the changes since version 12.

So change listing should be done using a token containing the current page, the min version and the max version. when
we encounter an item we show all changes between min and max. When we reach max we generate a new token with min=oldMax
and max=currentMax and continue. When we reach the end of the changes we return an empty page with min=currentMax.
Requests using that token get parsed as min=min and max=currentMax so if we need to paginate again that's what the token
will contain.

If the item-chain contains an item that has a version > max we check for a version where 
rootItemversion < targetItemVersion < currentMax if it exists we postpone otherwise we continue. You can do a range 
lookup for targetSubject + rootItemversion in the diff list and then see if the next item is a higher version that's 
still lower then the currentMax

Other stuff we need to add:
  - work out how to make rdf properties never clash with "underscoreless" names (our special properties) and __* (graphql's special properties)
  - a performance budget test for the import of data (i.e. 10_000 triples are allowed to take 250ms or so)
  - debug why I now see an update happening multiple times
  - a webhook for update notifications where we allow the other side to return 200 OK for "thanks I received it" and a
    201 created with a polling uri if they want us to long-poll until they're done. We then store that URI and provide a
    uri to the client so it can poll us and we poll the rest to check when the change is applied everywhere.
    The new uri should at some point return 2xx to signal done. 404 signals pending, 301/302/303 are followed. 4xx and 5xx signals error.
  - ~A way to have an update break and be rejected.~
    - Only for internal clients anyway (because you have the general's problem)
    - We'd better check on the HTTP endpoint side and make sure that rdf put into the log is valid
    - when importing rdf we can't really change it anyway. And rejecting it wholesale is worse then importing it and 
      allowing the user to fix it.
    - we might have a separate process + separate store that validates properties on the rdf.
    - since there's an endless variance in value types we can never guarantee to validate them all. 
      So we might as well leave that to the client who is using it.

 - [x] a function that generates an RDF-patch based on the json-ld and the current state of the data
 - [ ] generate the "generated by timbuctoo" triples
 - [ ] make the transactioning block from the patch-generation onwards (to avoid race conditions)

Non-critical:

 - add provenance predicates to the archetypes
 - we should make sure blank nodes don't collide when importing normal rdf. (this should be done by having rdf-patch specify which blank node namespace to use and to skolemize / namespace all blank nodes inside timbuctoo)
 - make the json-ld as posted to the edit endpoint (a) not allow blank nodes (b) interpret a placeholder uri so the client can easily generate unique uri's (something like "@id": "NEW" or something)
 - be able to predefine http://predicate/name->graphql_name mappings so you can fixate parts of the graphql query and leave others open.
 - disallow empty @prefixes (replace them with an underscore) and make sure prefixes don't overlap
 - normalize and frame the edit json-ld so the frontender has more freedom in constructing his json
 - validate json against the schema




