import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Aapeli Haanpuu",
  about:
    "Aapeli is a frontend/full-stack developer with a long experience using React, Redux and other web technologies.",
  image: {
    url: "people/aapeli.jpg",
  },
  social: {
    homepage: "https://haanpuu.fi",
    twitter: "",
    github: "https://github.com/aapzu/",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "https://www.linkedin.com/in/aapeli-haanpuu/",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [Keyword.REACT, Keyword.REDUX, Keyword.TYPESCRIPT],
  type: [ContactType.SPEAKER],
};

export default person;
