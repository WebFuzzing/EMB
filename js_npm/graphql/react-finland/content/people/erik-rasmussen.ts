import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Erik Rasmussen",
  about: "American expat JavaScript developer.",
  image: {
    url: "people/erik.jpg",
  },
  social: {
    homepage: "",
    twitter: "erikras",
    github: "erikras",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Spain",
      code: "ES",
    },
    city: "",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
