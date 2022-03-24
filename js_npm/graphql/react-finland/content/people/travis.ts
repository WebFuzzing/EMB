import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Travis Arnold",
  about: "Designer 🎨 Engineer 📐 Systems 🎛 React ⚛️",
  image: {
    url: "people/travis.jpg",
  },
  social: {
    homepage: "https://souporserious.com/",
    twitter: "souporserious",
    github: "souporserious",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "San Diego",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
