import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Varya Stepanova",
  about:
    "Varya Stepanova is a Design Systems Architect and Product Owner with extensive experience in management and development. During her tech career, Varya's primary focus was in component-focused UI design and development, creating helpful tooling for the subject and spreading the practices across organizations. Nowadays, she brings design systems to the next level of success through cooperative work in the company development community and bridging the gap for designers, developers, and business specialists.",
  image: {
    url: "people/varya.jpg",
  },
  social: {
    homepage: "http://varya.me/",
    twitter: "varya_en",
    github: "varya",
    linkedin: "varyastepanova",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [
    Keyword.REACT,
    Keyword.STYLING,
    Keyword.STYLE_GUIDES,
    Keyword.TOOLING,
  ],
  type: [ContactType.SPEAKER],
};

export default person;
