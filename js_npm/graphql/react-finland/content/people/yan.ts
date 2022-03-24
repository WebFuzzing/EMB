import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Yan Lavryushev",
  about: "",
  image: {
    url: "people/yan.jpg",
  },
  social: {
    homepage: "",
    twitter: "lobatik",
    github: "YanLobat",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Russia",
      code: "RU",
    },
    city: "St. Petersburg",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
