import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Gant Laborde",
  about:
    "Gant is a New Orleans-based force of nature. Not satisfied to be “just” an experienced programmer in an array of disciplines, he’s also a prolific writer, an award-winning public speaker, and an adjunct professor.\n\nA self-improvement aficionado, he takes every opportunity to connect with people and share knowledge through education and outreach. In the zombie apocalypse, Gant will run our much-needed bar, offering up advice, encouragement, and libations in equal measure.",
  image: {
    url: "people/gant.jpg",
  },
  social: {
    homepage: "http://gantlaborde.com/",
    twitter: "GantLaborde",
    github: "GantMan",
    linkedin: "gant-laborde",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "New Orleans",
  },
  keywords: [Keyword.REACT, Keyword.REACT_NATIVE],
  type: [ContactType.SPEAKER],
};

export default person;
