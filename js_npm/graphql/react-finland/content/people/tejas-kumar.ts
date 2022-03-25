import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Tejas Kumar",
  about: "I write code and love life.",
  image: {
    url: "people/tejas.jpg",
  },
  social: {
    homepage: "",
    twitter: "TejasKumar_",
    github: "tejasq",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Berlin",
  },
  keywords: [Keyword.REACT, Keyword.TOOLING],
  type: [ContactType.SPEAKER],
};

export default person;
