import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Adam Miskiewicz",
  about:
    "Adam Miskiewicz is a software engineer at Airbnb, lover of his two dogs, JavaScript, and GraphQL.",
  image: {
    url: "people/adam.jpg",
  },
  social: {
    homepage: "http://www.adammiskiewicz.com/",
    twitter: "skevy",
    github: "skevy",
  },
  location: {
    country: {
      name: "USA",
      code: "US",
    },
    city: "San Francisco",
  },
  keywords: [Keyword.GRAPHQL, Keyword.GRAPHQLJS],
  type: [ContactType.SPEAKER, ContactType.TALK],
};

export default person;
