import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Rebecca Hill",
  about:
    "Full stack JavaScript engineer at WeTransfer, building feedback software. Not-so-secretly helping JavaScript take over the world.",
  image: {
    url: "people/rebecca.jpg",
  },
  social: {
    homepage: "http://rebeccahill.co.nz",
    twitter: "rebekaka",
    github: "beerecca",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Netherlands",
      code: "NL",
    },
    city: "Amsterdam",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
