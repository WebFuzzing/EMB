import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Doug Sillars",
  about: "Head of Developer Relations at api.video",
  image: {
    url: "people/doug.jpg",
  },
  social: {
    homepage: "",
    twitter: "dougsillars",
    github: "dougsillars",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "dougsillars",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
