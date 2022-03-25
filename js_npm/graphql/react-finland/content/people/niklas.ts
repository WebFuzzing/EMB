import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Niklas Lepistö",
  about: "Senior Frontend Developer at Brella",
  image: {
    url: "people/niklas.jpg",
  },
  social: {
    homepage: "",
    twitter: "laznic",
    github: "laznic",
    facebook: "",
    medium: "",
    instagram: "",
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
