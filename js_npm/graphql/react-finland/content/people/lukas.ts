import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Lukas Holzer",
  about:
    "DesignOps — Frontend-Developer and Node.js enthusiast working @Dynatrace with a passion for enhancing design workflows",
  image: {
    url: "people/lukas.jpg",
  },
  social: {
    homepage: "",
    twitter: "luka5c0m",
    github: "lukasholzer",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "lukas-holzer",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Linz",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
