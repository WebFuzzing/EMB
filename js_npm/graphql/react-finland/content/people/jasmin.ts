import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Jasmin Assulin",
  about:
    "CEO & Co-founder at deidei | Diversity, Equity, Inclusion & Comms specialist | She/her",
  image: {
    url: "people/jasmin.jpg",
  },
  social: {
    homepage: "https://www.deidei.co/",
    twitter: "jasminassulin",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "jasmin-assulin",
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
