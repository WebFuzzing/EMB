import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Dragos Bilaniuc",
  about:
    "Freelancer • Web and Mobile Tech Lead/PM • Occasional public speaker | I will help you articulate your idea, develop your product and pitch your business.",
  image: {
    url: "people/dragos.jpg",
  },
  social: {
    homepage: "https://dbln.me/",
    twitter: "dragosbln",
    github: "dragosbln",
    facebook: "dragos.blnc",
    medium: "dragosbln",
    instagram: "dragosbilaniuc",
    linkedin: "dragosbln",
    youtube: "UCqxf5BjmjorefWE3NGEAE_w",
    devto: "",
  },
  location: {
    country: {
      name: "Romania",
      code: "RO",
    },
    city: "Cluj-Napoca",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
