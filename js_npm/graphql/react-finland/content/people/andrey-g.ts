import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Andrey Goncharov",
  about:
    "Andrey is a full-stack TypeScript and JavaScript developer. He came from working in two software consultancies on a variety of projects: from small startups to well-established enterprises. Now he is happy to be a part of a family at Hazelcast. Big lover of open-source. Author of a small technical blog. Runs BeerJS Voronezh.",
  image: {
    url: "people/andrey-g.jpg",
  },
  social: {
    homepage: "https://goncharov.page",
    twitter: "ai_goncharov",
    github: "aigoncharov",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "aigoncharov",
    youtube: "",
    devto: "aigoncharov",
  },
  location: {
    country: {
      name: "Russia",
      code: "RU",
    },
    city: "Voronezh",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
