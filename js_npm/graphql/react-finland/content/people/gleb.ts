import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Gleb Bahmutov",
  about:
    "JavaScript ninja, image processing expert, software quality fanatic. Sr Director of Engineering at Mercari US. MS MVP for OSS work.",
  image: {
    url: "people/gleb.jpg",
  },
  social: {
    homepage: "https://glebbahmutov.com/",
    twitter: "bahmutov",
    github: "bahmutov",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "https://www.linkedin.com/in/bahmutov/",
    youtube: "https://youtube.com/glebbahmutov",
    devto: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "Boston",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
