import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Norbert de Langen",
  about: "Maintainer of Storybook, love open source and donuts",
  image: {
    url: "people/norbert.jpg",
  },
  social: {
    homepage: "",
    twitter: "norbertdelangen",
    github: "ndelangen",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Netherlands",
      code: "NL",
    },
    city: "Zwolle",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
