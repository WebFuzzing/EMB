import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Luca Matteis",
  about: "Event Sourcing, Statecharts, Behavioral Programming.",
  image: {
    url: "people/luca.jpg",
  },
  social: {
    medium: "@lmatteis",
    twitter: "lmatteis",
    github: "lmatteis",
  },
  location: {
    country: {
      name: "Italy",
      code: "IT",
    },
    city: "Rome",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
