import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Michel Weststrate",
  about:
    "Full-stack tech lead and open source evangelist at Mendix. Author of MobX, MobX-State-Tree and Immer. On a quest to make programming as natural as possible.",
  image: {
    url: "people/michel.jpg",
  },
  social: {
    homepage: "https://medium.com/@mweststrate",
    medium: "@mweststrate",
    twitter: "mweststrate",
    github: "mweststrate",
    linkedin: "michel-weststrate-3558463",
  },
  location: {
    country: {
      name: "Netherlands",
      code: "NL",
    },
    city: "Amsterdam",
  },
  keywords: [
    Keyword.IMMER,
    Keyword.MOBX,
    Keyword.MOBX_STATE_TREE,
    Keyword.REACT,
    Keyword.STATE_MANAGEMENT,
  ],
  type: [ContactType.SPEAKER],
};

export default person;
