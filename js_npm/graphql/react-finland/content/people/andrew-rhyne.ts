import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Andrew Rhyne",
  about:
    "Originally hailing from Austin, Andrew learned to code at the wee age of 8 first with BASIC and then C shortly thereafter.  He cut his teeth on Slackware while in grade school, and discovered his love for JavaScript and dynamic programming languages while at University. Andrew is very involved in the NodeJS OSS community, contributing to multiple Visionmedia projects such as debug and node-progress as well as the Apollostack ecosystem.  He is also the creator of multiple popular GraphQL server-side libraries, including apollo-errors and apollo-resolvers.",
  aboutShort:
    "Andrew is very involved in the NodeJS OSS community, contributing to multiple Visionmedia projects such as debug and node-progress as well as the Apollostack ecosystem.  He is also the creator of multiple popular GraphQL server-side libraries, including apollo-errors and apollo-resolvers.",
  image: {
    url: "people/andrew.jpg",
  },
  social: {
    twitter: "thebigredgeek",
    github: "thebigredgeek",
  },
  location: {
    country: {
      name: "USA",
      code: "US",
    },
    city: "San Francisco",
  },
  keywords: [Keyword.GRAPHQL],
  type: [ContactType.SPEAKER, ContactType.LIGHTNING_TALK],
};

export default person;
