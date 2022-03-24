import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Laura Kalbag",
  about: `I’m a friendly designer originally from the UK, now living in Ireland. You’ll usually find me talking about rights-respecting design, accessibility and inclusivity, privacy, web design and development.`,
  image: {
    url: "people/laurak.jpg",
  },
  social: {
    homepage: "https://laurakalbag.com/",
    twitter: "LauraKalbag",
    github: "LauraKalbag",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Ireland",
      code: "IE",
    },
    city: "Cork",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
