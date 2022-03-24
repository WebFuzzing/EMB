import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Diego Haz",
  about:
    "Software architect, designer, indie maker, cosmopolitan, astronomy lover.",
  image: {
    url: "people/diego.jpg",
  },
  social: {
    homepage: "",
    twitter: "diegohaz",
    github: "diegohaz",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Brazil",
      code: "BR",
    },
    city: "Rio Janeiro", // Not accurate
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
