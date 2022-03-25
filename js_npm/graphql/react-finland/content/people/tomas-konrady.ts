import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Tomáš Konrády",
  about:
    "Frontend developer at Lundegaard a.s. Lover of Ramda and creator of ramda-extension and react-union libraries.",
  image: {
    url: "people/tomas.jpg",
  },
  social: {
    twitter: "konnnyy",
    github: "tommmyy",
    linkedin: "tomaskonrady",
  },
  location: {
    country: {
      name: "Czech Republic",
      code: "CZ",
    },
    city: "Hradec Králové",
  },
  keywords: [Keyword.REACT, Keyword.TOOLING],
  type: [ContactType.SPEAKER],
};

export default person;
