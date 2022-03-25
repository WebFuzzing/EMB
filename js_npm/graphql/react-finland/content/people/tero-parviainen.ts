import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Tero Parviainen",
  about:
    "Software developer, music hacker, and writer. Building the design tools of the future at @CreativeDotAI. ",
  image: {
    url: "people/tero.jpg",
  },
  social: {
    homepage: "https://teropa.info",
    twitter: "teropa",
    github: "teropa",
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
