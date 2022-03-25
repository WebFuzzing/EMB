import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Paul Henschel",
  about:
    "The author of react-spring, react-three-fiber, zustand, and many more libraries.",
  image: {
    url: "people/paul.png",
  },
  social: {
    homepage: "",
    twitter: "0xca0a",
    github: "drcmda",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
