import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Nasim Selmani",
  about:
    "Diversity, Equity & Inclusion | People Operations | Inclusive Leadership | Intersectionality | Producer",
  image: {
    url: "people/nasim.jpg",
  },
  social: {
    homepage: "https://www.deidei.co/",
    twitter: "nasimselmani",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "nasim-selmani-",
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
