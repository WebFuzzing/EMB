import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Manjula Dube",
  about: "",
  image: {
    url: "people/manjula.jpg",
  },
  social: {
    homepage: "https://www.manjuladube.dev/",
    twitter: "manjula_dube",
    github: "manjula-dube",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "India",
      code: "IN",
    },
    city: "Mumbai",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
