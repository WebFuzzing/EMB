import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Marc-André Giroux",
  about:
    "A Jazz guitarist turned developer, Marc-André is currently working at GitHub, where he is trying to help building better APIs. He is currently writing a book, [The Little Book of GraphQL Design](https://book.graphqlschemadesign.com/). When he's not hacking on or thinking about GraphQL, you might find him throwing heavy weights above his head.",
  aboutShort:
    "A Jazz guitarist turned developer, Marc-André is currently working at GitHub, where he is trying to help building better APIs. He is currently writing a book, The Little Book of GraphQL Design.",
  image: {
    url: "people/marc.jpg",
  },
  social: {
    homepage: "http://mgiroux.me/",
    twitter: "__xuorig__",
    github: "xuorig",
  },
  location: {
    country: {
      name: "Canada",
      code: "CA",
    },
    city: "Montreal",
  },
  keywords: [Keyword.GRAPHQL, Keyword.GRAPHQLJS],
  type: [ContactType.SPEAKER, ContactType.WORKSHOP_HOST],
};

export default person;
