import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Margarita Obraztsova",
  about:
    "Margarita is a software engineer and international speaker on tools to improving software development processes. She is a consultant software engineer, who loves to find different ways to optimize project work.",
  image: {
    url: "people/margarita.jpg",
  },
  social: {
    homepage: "",
    twitter: "riittagirl",
    github: "",
    facebook: "",
    medium: "",
    instagram: "riittagirl",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
