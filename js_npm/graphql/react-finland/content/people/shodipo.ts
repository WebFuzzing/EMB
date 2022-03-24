import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Shodipo Ayomide",
  about: "",
  image: {
    url: "people/shodipo.jpg",
  },
  social: {
    homepage: "",
    twitter: "developerayo",
    github: "developerayo",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Nigeria",
      code: "NG",
    },
    city: "Lagos",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
