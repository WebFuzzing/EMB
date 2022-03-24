import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Artem Sapegin",
  about:
    "Artem is a frontend developer living in Berlin, passionate photographer and owner of crazy dogs. Creator of React Styleguidist.",
  image: {
    url: "people/artem.jpg",
  },
  social: {
    homepage: "http://sapegin.me/",
    twitter: "iamsapegin",
    github: "sapegin",
    linkedin: "artemsapegin",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Berlin",
  },
  keywords: [
    Keyword.REACT,
    Keyword.STYLING,
    Keyword.STYLEGUIDIST,
    Keyword.STYLE_GUIDES,
    Keyword.TOOLING,
  ],
  type: [ContactType.SPEAKER],
};

export default person;
