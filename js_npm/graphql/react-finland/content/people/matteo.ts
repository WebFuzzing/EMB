import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Matteo Frana",
  about: "Founder of @ReactBricks, the Visual editing CMS.",
  image: {
    url: "people/matteo.jpg",
  },
  social: {
    homepage: "https://reactbricks.com/",
    twitter: "matfrana",
    github: "matteofrana",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Italy",
      code: "IT",
    },
    city: "Bergamo",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
