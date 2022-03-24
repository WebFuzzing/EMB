import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Calin Bogdan",
  about: "JavaScript fellow, clean code maniac, minimalist.",
  image: {
    url: "people/calinb.jpg",
  },
  social: {
    homepage: "https://calinbogdan.com/",
    twitter: "calinesque",
    github: "calinbogdan",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Romania",
      code: "RO",
    },
    city: "",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
