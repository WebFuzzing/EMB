import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Matt Pocock",
  about:
    "Lead Developer at Stately. Trying to bring on the visual coding revolution.",
  image: {
    url: "people/matt.jpg",
  },
  social: {
    homepage: "",
    twitter: "mpocock1",
    github: "mattpocock",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
    twitch: "mpocock1",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "Oxfordshire",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
