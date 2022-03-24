import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Patrick Ecker",
  about:
    "Patrick is a frontend engineer at Rohea Oy, building the next big sales enablement platform built on Flow, ReScript, React and PHP. He is also core team member of the ReScript programming language, working on the language's tooling infrastructure and documentation website.",
  image: {
    url: "people/patrick.jpg",
  },
  social: {
    homepage: "",
    twitter: "ryyppy",
    github: "ryyppy",
    linkedin: "patrick-stapfer-5ba205a4",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  keywords: [Keyword.REACT, Keyword.RESCRIPT],
  type: [ContactType.SPEAKER],
};

export default person;
