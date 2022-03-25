import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import { paasitorni } from "../../locations";
import nik from "../../people/nik-graf";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [nik],
  title: "Advanced GraphQL Concepts",
  description: `You understood the basics of GraphQL or even worked with it in a production environment? Then this workshop is for you. While GraphQL comes with many benefits it also requires some rethinking. For example how to secure your server from malicious queries or how to give different users access to different parts of the Schema.

We will answer these questions and many more by covering the following topics:

- Authentication
- Authorization
- Rate limiting
- Persistent Queries
- Schema stitching
- API Design

Every topic is covered by a 20 minute presentation followed by a hands on exercise. After every exercise we will briefly glance at a solution and move on to the next topic. For the exercises you can choose the programming language and tools of your choice. Just come prepared with a working GraphQL setup.

To goal is that by the end of the day you are way more comfortable making design decisions for your GraphQL environment.
`,
  keywords: [Keyword.GRAPHQL],
  location: paasitorni,
};

export default workshop;
