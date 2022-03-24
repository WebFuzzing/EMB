import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Michał Chudziak",
  about:
    'Lead software engineer in Callstack - company which provides the highest-level consultancy services for React and React Native. Lover of GraphQL\'s simplicity and a big supporter of "learn once, write anywhere" motto.',
  image: {
    url: "people/michal.jpg",
  },
  social: {
    homepage: "http://callstack.com",
    twitter: "michal_chudziak",
    github: "mike866",
    linkedin: "michalchudziak",
  },
  location: {
    country: {
      name: "Poland",
      code: "PL",
    },
    city: "Wrocław",
  },
  keywords: [Keyword.REACT, Keyword.REACT_NATIVE],
  type: [ContactType.SPEAKER],
};

export default person;
