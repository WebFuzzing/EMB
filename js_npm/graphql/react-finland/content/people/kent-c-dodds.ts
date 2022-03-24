import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Kent C. Dodds",
  about: "Making people's lives better with software.",
  image: {
    url: "people/kent.jpg",
  },
  social: {
    homepage: "https://kentcdodds.com",
    twitter: "kentcdodds",
    github: "kentcdodds",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "Salt Lake City",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
