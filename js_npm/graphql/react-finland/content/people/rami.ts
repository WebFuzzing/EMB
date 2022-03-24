import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Rami Ertimo",
  about:
    "Rami is a Senior Developer working with Smartly.io's Design System. Designer by heart, developer by trait, bridging the gap between designers and developers.",
  image: {
    url: "people/rami.jpg",
  },
  social: {
    homepage: "",
    twitter: "",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "rami-ertimo",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
