import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import { paasitorni } from "../../locations";
import marc from "../../people/marc-andre-giroux";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [marc],
  title: "GraphQL Schema Design",
  description: `Building a brand new GraphQL API? Migrating from a REST API? If you are interested in designing a GraphQL schema that is great to use by clients and stands the test of time, this workshop is for you.

Alternating between presentations and hands on exercices, this workshop will cover:

* Good development workflows to build GraphQL APIs
* Common anti-patterns
* How to build an evolvable schema
* How to approach a migration from another API
* Domain driven design & GraphQL
* How to design errors
* Must-have tooling for building GraphQL APIs at scale
* Many other great practices used in the largest GraphQL APIs out there!

For the exercices, we will only use the GraphQL SDL (Schema Definition Language), meaning you'll get to learn no matter what language you use to build your schema.`,
  keywords: [Keyword.GRAPHQL, Keyword.REACT],
  location: paasitorni,
};

export default workshop;
