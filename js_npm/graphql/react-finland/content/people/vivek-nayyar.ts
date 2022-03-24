import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Vivek Nayyar",
  about:
    "An avid reader who is always trying to learn concepts and best practices which should be followed while programming.",
  image: {
    url: "people/vivek.jpg",
  },
  social: {
    homepage: "https://www.viveknayyar.in",
    twitter: "VivekNayyar09",
    github: "vivek12345",
    facebook: "",
    medium: "@VivekNayyar",
    instagram: "",
    linkedin: "viveknayyar",
    youtube: "",
  },
  location: {
    country: {
      name: "India",
      code: "IN",
    },
    city: "Thane",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
