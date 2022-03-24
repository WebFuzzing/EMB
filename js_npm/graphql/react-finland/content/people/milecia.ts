import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Milecia McGregor",
  about: "",
  image: {
    url: "people/milecia.jpg",
  },
  social: {
    homepage: "https://www.flippedcoding.com/",
    twitter: "FlippedCoding",
    github: "flippedcoder",
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
    city: "",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
