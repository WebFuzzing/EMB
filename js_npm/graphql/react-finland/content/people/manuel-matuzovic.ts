import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Manuel Matuzović",
  about:
    "I'm a frontend developer from Vienna. I'm specialized in HTML, CSS, accessibility, and performance.",
  image: {
    url: "people/manuel.jpg",
  },
  social: {
    homepage: "https://www.matuzo.at/",
    twitter: "mmatuzo",
    github: "matuzo",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
