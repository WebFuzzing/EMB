// MODIFIED (WFD-added, not part of the original SUT): the graph half of the seed the drivers
// create. Admins have no profile, so they get no :User node. No relationship between users is
// seeded - likes, dislikes and matches are left for the search to create.
MERGE (s:State {name: 'tuscany'})
WITH s
UNWIND [
  {id: 'wfd_user1', age: 28, sex: 'm', orientation: 'straight', city: 'pisa', interests: ['music', 'travel']},
  {id: 'wfd_user2', age: 27, sex: 'f', orientation: 'straight', city: 'pisa', interests: ['music', 'art']}
] AS row
MERGE (c:City {name: row.city})
MERGE (c)-[:LOCATED_IN]->(s)
CREATE (u:User {_id: row.id, age: row.age, sex: row.sex, orientation: row.orientation})
CREATE (u)-[:LIVES_IN]->(c)
WITH u, row
UNWIND row.interests AS name
MERGE (i:Interest {name: name})
CREATE (u)-[:HAS_INTEREST]->(i);
