import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Jan Peer Stöcklmair",
  about: "Open-source contributor. JS fanatic. I love optimizing workflows.",
  image: {
    url: "people/jan.png",
  },
  social: {
    homepage: "https://jpeer.at/",
    twitter: "jpeer264",
    github: "jpeer264",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "jpeer",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
