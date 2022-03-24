import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Benjie Gillam",
  about:
    "Benjie loves using and talking about GraphQL, Node.js, PostgreSQL and React; he is the maintainer of PostGraphile, the original instant GraphQL API for your Postgres database.",
  image: {
    url: "people/benjie.jpg",
  },
  social: {
    homepage: "https://graphile.org/",
    twitter: "benjie",
    github: "benjie",
    linkedin: "benjiegillam",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "Southampton",
  },
  keywords: [Keyword.GRAPHQL],
  type: [ContactType.SPEAKER, ContactType.LIGHTNING_TALK],
};

export default person;
