import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Andrei Pfeiffer",
  about:
    "Eclectic Code Designer. UI Developer for web & mobile. Organizer of revo.js conference.",
  image: {
    url: "people/andrei.jpg",
  },
  social: {
    homepage: "https://andreipfeiffer.dev/",
    twitter: "pfeiffer_andrei",
    github: "andreipfeiffer",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Romania",
      code: "RO",
    },
    city: "Timisoara",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
