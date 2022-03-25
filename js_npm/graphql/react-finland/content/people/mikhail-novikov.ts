import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Mikhail Novikov",
  about:
    "Mikhail is a GraphQL hipster. He started GraphQL startup before GraphQL was ever public. The startup failed, but Mikhail has been active in GraphQL community ever since, working on tools like Schema Stitching and Apollo Launchpad. He is one of the organizers of GraphQL Finland.",
  aboutShort:
    "Mikhail is a GraphQL hipster. He started GraphQL startup before GraphQL was ever public. After that he worked on tools like Schema Stitching and Apollo Launchpad and taught GraphQL at multiple workshops.",
  image: {
    url: "people/mikhail.jpg",
  },
  social: {
    homepage: "https://freiksenet.com",
    twitter: "freiksenet",
    github: "freiksenet",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [Keyword.GRAPHQL, Keyword.CMS],
  type: [ContactType.ORGANIZER],
};

export default person;
