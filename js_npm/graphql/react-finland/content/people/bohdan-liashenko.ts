import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Bohdan Liashenko",
  about:
    "Bohdan is a frontend developer at 0+X, a consultancy firm based in Stockholm. He’s passionate about software delivery processes and believes there is still a room for improvements. Creator of codecrumbs, js2flowchart, Under the hood ReactJS.",
  image: {
    url: "people/bohdan.jpg",
  },
  social: {
    homepage: "",
    twitter: "bliashenko",
    github: "Bogdan-Lyashenko",
    facebook: "bliashenko",
    medium: "@bohdanliashenko",
    instagram: "",
    linkedin: "bohdan-liashenko-bb365854",
    youtube: "",
  },
  location: {
    country: {
      name: "Sweden",
      code: "SE",
    },
    city: "Stockholm",
  },
  keywords: [Keyword.REACT, Keyword.TOOLING],
  type: [ContactType.SPEAKER],
};

export default person;
