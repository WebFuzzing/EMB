import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Esa-Matti Suuronen",
  about: "",
  image: {
    url: "people/esa-matti.jpg",
  },
  social: {
    homepage: "",
    twitter: "esamatti",
    github: "esamattis",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Jyväskylä",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
