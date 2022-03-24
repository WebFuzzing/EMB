import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Dan Schafer",
  about:
    "Dan Schafer is a software engineer at Facebook and a co-creator of GraphQL. He worked on the original GraphQL API for Facebook News Feed, developed Facebook’s GraphQL API Design principles, expanded GraphQL to support Mutations and Subscriptions, and contributed to the initial release of the GraphQL specification and reference implementation. Currently, he’s a tech lead on the Product Foundation team at Facebook, focused on building end-to-end client frameworks for development of the Facebook apps.",
  image: {
    url: "people/dan.jpg",
  },
  social: {
    twitter: "dlschafer",
    github: "dschafer",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "San Francisco",
  },
  keywords: [Keyword.GRAPHQL],
  type: [ContactType.SPEAKER, ContactType.KEYNOTE],
};

export default person;
