import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Max Stoiber",
  about: `Creator of [Bedrock](https://bedrock.mxstbr.com), styled-components & react-boilerplate. Founder of [GraphCDN](https://graphcdn.io/).`,
  image: {
    url: "people/max.jpg",
  },
  social: {
    homepage: "https://mxstbr.com/",
    twitter: "mxstbr",
    github: "mxstbr",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
