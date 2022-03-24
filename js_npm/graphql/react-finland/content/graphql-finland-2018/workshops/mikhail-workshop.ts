import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import { paasitorni } from "../../locations";
import mikhail from "../../people/mikhail-novikov";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [mikhail],
  title: "Building a GraphQL API with node.js",
  description: `Learn how to build a GraphQL API from scratch using Node.js. This workshop is aimed at GraphQL beginners, but you probably have built an API server before. We'll go through the basics of building a GraphQL API server, wrap a REST API with it and then connect it to a database.

The following topics will be covered:

* Basics of GraphQL on a server
* Resolver model
* Ways to define a GraphQL schema
* Schema first design
* Evolving the backing data service, without changing the schema
* Wrapping a REST API with GraphQL gateway
* Connecting to a database

Workshop will have an interactive format, where I will livecode, while explaining what I'm doing. There will also be short presentations and non-coding parts, where I will explain some of the advanced concepts.

At the end of the workshop you should be comfortable building a GraphQL API with node.js.

This workshop is only offered as a bundle deal with a conference.
`,
  keywords: [Keyword.GRAPHQL],
  location: paasitorni,
};

export default workshop;
