import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Thomas Allmer",
  about:
    "Maintainer Open Web Components, Modern Web, Rocket and works at ING doing Design Systems and Lion Web Components",
  image: {
    url: "people/thomas.jpg",
  },
  social: {
    homepage: "",
    twitter: "daKmoR",
    github: "daKmoR",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "thomas-allmer-1b264716",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
