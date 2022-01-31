# EMB
[EvoMaster](http://evomaster.org) Benchmark (EMB): 
a set of web/enterprise applications for scientific research in Software Engineering.

We collected several different systems, in different programming languages, like
Java, Kotlin, JavaScript and C#.
In this documentation, we will refer to these projects as System Under Test (SUT).
Currently, the SUTs are either _REST_ or _GraphQL_ APIs.

For each SUT, we implemented _driver_ classes, which can programmatically _start_, _stop_ and _reset_ the state of SUT (e.g., data in SQL databases).
As well as enable setting up different properties in a _uniform_ way, like choosing TCP port numbers for the HTTP servers. 
If a SUT uses any external services (e.g., a SQL database), these will be automatically started via Docker in these driver classes. 


This collection of SUTs was originally assembled for easing experimentation with the fuzzer called [EvoMaster](http://evomaster.org).
However, finding this type of applications is not trivial among open-source projects. 
Furthermore, it is not simple to sort out all the technical details on how to set these applications up and start them in a simple, uniform approach. 
Therefore, this repository provides the important contribution of providing all these necessary scripts for researchers that need this kind of case study.   


## License
All the code that is new for this repository (e.g., the driver classes) is released under Apache 2.0 license. 
However, this repository contains as well sources from different open-source 
projects, each one with its own license, as clarified in more details beneath.


## Current Case Studies

The projects were selected based on searches using keywords on GitHub APIs, using convenience sampling.
Several SUTs were looked at, in which we discarded the ones that would not compile, would crash at startup, would use obscure/unpopular libraries with no documentation to get them started, are too trivial, student projects, etc.
Where possible, we tried to prioritize/sort based on number of _stars_ on GitHub.


Note that some of these open-source projects might be no longer supported, whereas others are still developed and updated.
Once a system is added to EMB, we do not modify nor keep it updated with its current version under development.
The reason is that we want to keep an easy to use, constant set of case studies for experimentation that can be reliably used throughout the years.

The SUTs called _NCS_ (Numerical Case Study) and _SCS_ (String Case study) are artificial, developed by us.
They are based on numerical and string-based functions previously used in the literature of unit test generation.
We just re-implemented in different languages, and put them behind a web service. 



### REST: Java/Kotlin

* Features-Service (Apache), from [https://github.com/JavierMF/features-service](https://github.com/JavierMF/features-service)  

* Scout-API (MIT), from [https://github.com/mikaelsvensson/scout-api](https://github.com/mikaelsvensson/scout-api)

* ProxyPrint (Apache), from [https://github.com/ProxyPrint/proxyprint-kitchen](https://github.com/ProxyPrint/proxyprint-kitchen)

* CatWatch (Apache), from [https://github.com/zalando-incubator/catwatch](https://github.com/zalando-incubator/catwatch)

* OCVN (MIT), from [https://github.com/devgateway/ocvn](https://github.com/devgateway/ocvn)

* News (LGPL), from [https://github.com/arcuri82/testing_security_development_enterprise_systems](https://github.com/arcuri82/testing_security_development_enterprise_systems) 

* Restcountries (MPL), from [https://github.com/apilayer/restcountries](https://github.com/apilayer/restcountries)

* Languagetool (LGPL), from [https://github.com/languagetool-org/languagetool](https://github.com/languagetool-org/languagetool) 

* CWA-Verification-Server (Apache), from [https://github.com/corona-warn-app/cwa-verification-server](https://github.com/corona-warn-app/cwa-verification-server)

* NCS (not-known license, artificial numerical examples coming from different sources)

* SCS (not-known license, artificial string examples coming from different sources)


### REST: JavaScript/TypeScript

* Disease-sh-API (GPL), from [https://github.com/disease-sh/API](https://github.com/disease-sh/API)

* Cyclotron (MIT), from [https://github.com/ExpediaInceCommercePlatform/cyclotron](https://github.com/ExpediaInceCommercePlatform/cyclotron)

* SpaceX-API (Apache-2.0 License), from [https://github.com/r-spacex/SpaceX-API](https://github.com/r-spacex/SpaceX-API)

* Realworld-App (ISC), from [https://github.com/lujakob/nestjs-realworld-example-app](https://github.com/lujakob/nestjs-realworld-example-app)

* NCS (not-known license, artificial numerical examples coming from different sources)

* SCS (not-known license, artificial string examples coming from different sources)


### REST: .Net/C# 

* Menu.API (not-known license), from [https://github.com/chayxana/Restaurant-App](https://github.com/chayxana/Restaurant-App)

* SampleProject (MIT), from [https://github.com/kgrzybek/sample-dotnet-core-cqrs-api](https://github.com/kgrzybek/sample-dotnet-core-cqrs-api)

* NCS (not-known license, artificial numerical examples coming from different sources)

* SCS (not-known license, artificial string examples coming from different sources)


### GraphQL: Java/Kotlin

* Spring-Pet-Clinic (Apache ), from [https://github.com/spring-petclinic/spring-petclinic-graphql]()

* Patio-Api (GPL), from [https://github.com/patio-team/patio-api]()

* Timbuctoo (GPL), from [https://github.com/HuygensING/timbuctoo]()

* NCS (not-known license, artificial numerical examples coming from different sources)

* SCS (not-known license, artificial string examples coming from different sources)


## Using This Repository

Due to several reasons, the software in this repository is not published as a library (e.g., on Maven and NPM).
To use EMB, you need to clone this repository:

```
git clone https://github.com/EMResearch/EMB.git
```

There are 2 use cases for EMB:

* Run experiments with _EvoMaster_

* Run experiments with other tools

To run experiments with _EvoMaster_, everything can be setup by running the script `scripts/dist.py`.
Note that you will need installed at least JDK 8, JDK 11, NPM and .Net 3.x, as well as Docker.
Also, you will need to setup environment variables like `JAVA_HOME_8` and `JAVA_HOME_11`.
The script will issue error messages if any prerequisite is missing.
Once the script is completed, all the SUTs will be available under the `dist` folder, and a `dist.zip` will be created as well.
Note that here the drivers will be built as well besides the SUTs, and the SUT themselves will be instrumented by code manipulations (for white-box testing heuristics) of _EvoMaster_ (this is for JavaScript and .Net, whereas instrumentation for JVM is done at runtime, via an attached JavaAgent). 

TODO

## Build The Systems

### Build JDK_8_MAVEN

The folder `cs` (*case study*) contains the source code of the different 
system under tests (SUT) in this benchmark, for JDK 8 and Maven.

The folder `em` (*EvoMaster*) contains the classes needed to be written to enable
the use of EvoMaster on the SUTs. 
In particular, there are `EmbeddedEvoMasterController` and
`ExternalEvoMasterController` class implementations for each SUT.
Note: usually you would write a EvoMaster controller class in the same module
of the SUTs. 
Here, they are in different modules just to make clear what is needed to implement
to enable the use of EvoMaster.


To compile and generate all the jar files, use the command:

``mvn clean package -DskipTests`` 

Currently, all the case studies do require JDK __8__.
They will not compile with a different version. 

_Note_: the case studies do import EvoMaster as a library. Current SNAPSHOT
versions of the case studies do use the most recent SNAPSHOT version of EvoMaster
(the two versioning numbers are aligned).
We do __NOT__ publish the SNAPSHOT dependencies online.
This means that, if you try to build the project directly, it will fail due to 
missing SNAPSHOT dependencies. 

To use such SNAPSHOT versions, you need first a `mvn install` of EvoMaster on your 
machine (so that the SNAPSHOT jars are created, and put under your `~/.m2` folder).
However, in the Git repository of EMB, we did tag the versions of EMB that are
using the published versions of EvoMaster.
See the [releases](https://github.com/EMResearch/EMB/releases) page.
For example, to use version `X` of EvoMaster, you can check out the Git commit
of EMB tagged with version `X`. 
To see the current available tags, from a command-line you can execute:

`git tag`

Then, to switch to a specific tag X (e.g., `v1.0.0`), you can run:

`git checkout tags/v1.0.0`

Finally, if for any reason you need to switch back to the latest snapshot version, you can run:

`git checkout master` 

There is an issue if you try to checkout an old version. 
Not only Java broke backward compatibility with JDK 9, but also Maven...
If you try to build with Maven and get an error regarding
`maven-processor-plugin`, you might have to add manually
the following plugin dependency version:
```
<plugin>
    <groupId>org.bsc.maven</groupId>
    <artifactId>maven-processor-plugin</artifactId>
    <version>3.3.3</version>
</plugin>
```

Besides JDK 8, to build from Maven you will also need NPM and NodeJS installed
on your machine (as some  projects have GUIs built with JS).


### Build DOTNET_3

*Documentation under construction*


### Build JS_NPM

*Documentation under construction*

### Build *develop* Branch

Branch *develop* is using the most recent SNAPSHOT version of _EvoMaster_.
As that is not published online, you need to clone its repository, and build
it locally (see its documentation on how to do it).

To handle JavaScript, unfortunately there is the need for some manual settings.
However, it needs to be done just once. 

You need to create _symbolic_ link inside `EMB\js_npm` that points to the `evomaster-client-js` folder in _EvoMaster_.
How to do this, depends on the Operating System.
Note that in the following, `<some-path>` should be replaced with the actual real paths of where you cloned the _EvoMaster_ and _EMB_ repositories. 

Windows: `mklink /D  C:\<some-path>\EMB\js_npm\evomaster-client-js  C:\<some-path>\EvoMaster\client-js\evomaster-client-js`

Mac: `ln -s /<some-path>/EvoMaster/client-js/evomaster-client-js  /<some-path>/EMB/js_npm/evomaster-client-js`