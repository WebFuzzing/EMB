import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Naman Goel",
  about:
    "Maintainer of Stylex, Facebook's not-yet-open-source CSS-in-JS framework. Working on Web Designs System Components at Facebook.",
  image: {
    url: "people/naman.jpg",
  },
  social: {
    homepage: "http://blog.namangoel.com",
    twitter: "naman34",
    github: "nmn",
    facebook: "naman34",
    medium: "naman34",
    instagram: "naman34h",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "San Francisco",
  },
  keywords: [
    Keyword.REACT,
    Keyword.LIBRARIES,
    Keyword.STYLING,
    Keyword.TOOLING,
    Keyword.TYPESCRIPT,
    Keyword.WEBPACK,
    Keyword.BABEL,
    Keyword.NEXT_JS,
  ],
  type: [ContactType.SPEAKER],
};

export default person;
