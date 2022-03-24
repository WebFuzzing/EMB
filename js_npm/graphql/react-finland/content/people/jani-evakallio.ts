import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Jani Eväkallio",
  about:
    "Jani is a small-town Finnish boy far from home. Today he leads a team building cross-stack React mobile and web apps at Formidable London and, in his spare time, performs improvised comedy and speaks at conferences advocating for the React ecosystem, particularly React Native and GraphQL.",
  image: {
    url: "people/jani.jpg",
  },
  social: {
    homepage: "",
    twitter: "jevakallio",
    github: "jevakallio",
    linkedin: "jevakallio",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "London",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
