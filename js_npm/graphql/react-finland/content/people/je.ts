import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Je Xia",
  about: "Web developer and graphic designer. Creator of Aleph.js and esm.sh.",
  image: {
    url: "people/je.jpg",
  },
  social: {
    homepage: "http://jex.me/",
    twitter: "jexia_",
    github: "ije",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "China",
      code: "CN",
    },
    city: "Beijing",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
