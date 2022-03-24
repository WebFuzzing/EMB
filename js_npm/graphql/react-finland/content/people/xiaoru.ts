import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Xiaoru Li",
  about:
    "Xiaoru is a computer science student at Karlsruhe Institute of Technology, Germany. He loves to teach himself cool stuff like machine learning and web technologies and write about them.",
  image: {
    url: "people/xiaoru.jpg",
  },
  social: {
    homepage: "https://www.xiaoru.li",
    twitter: "hexrcs",
    github: "hexrcs",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "hexrcs",
  },
  location: {
    country: {
      name: "China",
      code: "CN",
    },
    city: "",
  },
  keywords: [Keyword.REACT], // TODO: check keywords for more
  type: [ContactType.SPEAKER],
};

export default person;
