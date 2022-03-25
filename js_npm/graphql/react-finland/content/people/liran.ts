import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Liran Tal",
  about:
    "GitHub Star 🌟 · DevRel at Snyk 🥑 · Author of [Essential Node.js Security](http://bit.ly/securenodejs) · Node.js CLI Best Practices 🔥 · Docker security 🐳",
  image: {
    url: "people/liran.jpg",
  },
  social: {
    homepage: "",
    twitter: "liran_tal",
    github: "lirantal",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Israel",
      code: "IL",
    },
    city: "Tel Aviv",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
