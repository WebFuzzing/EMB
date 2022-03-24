import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Matias Huhta",
  about:
    "Matias is a passionate Web Components advocate. He's a active member of Web Component communities and a active open source contributor.",
  image: {
    url: "people/matias.jpeg",
  },
  social: {
    homepage: "https://matsu.fi/",
    twitter: "matsuuu_",
    github: "Matsuuu",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "matias-huhta-b0b159106",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Turku",
  },
  keywords: [Keyword.REACT, Keyword.TOOLING],
  type: [ContactType.SPEAKER],
};

export default person;
