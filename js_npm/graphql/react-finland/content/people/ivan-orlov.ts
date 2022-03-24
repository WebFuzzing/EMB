import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Ivan Orlov",
  about: "",
  image: {
    url: "people/ivan-orlov.jpg",
  },
  social: {
    homepage: "",
    twitter: "nchanged",
    github: "nchanged",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Malta",
      code: "MT",
    },
    city: "Valletta",
  },
  keywords: [Keyword.REACT], // TODO: check keywords for more
  type: [ContactType.SPEAKER],
};

export default person;
