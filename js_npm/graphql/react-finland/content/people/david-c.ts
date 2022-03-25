import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "David Corbacho Roman",
  about:
    "Señor Developer at Wunder. Originally from Spain, I've been living in Finland for more than 10 years, focusing my career on web development and JavaScript",
  image: {
    url: "people/david-c.jpg",
  },
  social: {
    homepage: "",
    twitter: "dcorbacho",
    github: "dcorb",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "corbacho",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Nummela",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};
export default person;
