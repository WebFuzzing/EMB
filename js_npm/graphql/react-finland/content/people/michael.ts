import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Michael Shilman",
  about: "Building Storybook and Chroma.",
  image: {
    url: "people/michael.jpg",
  },
  social: {
    homepage: "",
    twitter: "mshilman",
    github: "shilman",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Taiwan",
      code: "TW",
    },
    city: "Taipei",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
