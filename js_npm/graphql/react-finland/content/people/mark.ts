import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Mark Dalgleish",
  about:
    "CSS Modules co-creator, Playroom, Braid, MelbJS. OSS / UI / design / tooling at @seek-oss",
  image: {
    url: "people/mark.jpg",
  },
  social: {
    homepage: "",
    twitter: "markdalgleish",
    github: "markdalgleish",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Australia",
      code: "AU",
    },
    city: "Melbourne",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
