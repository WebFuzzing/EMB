import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Ville Immonen",
  about:
    "Software developer who works on developer tools at Expo. Formerly co-founded Reindex, a hosted GraphQL service. Likes good coffee, snowboarding and surfing. Lives in Finland with his family.",
  image: {
    url: "people/ville.jpg",
  },
  social: {
    twitter: "VilleImmonen",
    github: "fson",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Espoo",
  },
  keywords: [
    Keyword.REACT,
    Keyword.REACT_NATIVE,
    Keyword.TOOLING,
    Keyword.UNIVERSAL_REACT,
  ],
  type: [ContactType.SPEAKER],
};

export default person;
