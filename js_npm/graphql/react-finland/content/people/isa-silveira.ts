import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Isa Silveira",
  about:
    "Devoted to the arduous task of making devs take real world problems as seriously as their tooling.",
  image: {
    url: "people/isa.jpg",
  },
  social: {
    homepage: "",
    twitter: "silveira_bells",
    github: "bella-silveira",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Sweden",
      code: "SE",
    },
    city: "Stockholm",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
