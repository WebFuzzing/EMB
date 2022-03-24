import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Juan Picado",
  about:
    "Lead maintainer for 📦🔒 [Verdaccio](https://verdaccio.org) • 👨🏽‍💻 React and Node.js developer • Open Source believer",
  image: {
    url: "people/juan.jpg",
  },
  social: {
    homepage: "https://github.com/juanpicado",
    twitter: "jotadeveloper",
    github: "juanpicado",
    facebook: "",
    medium: "jotadeveloper",
    instagram: "",
    linkedin: "jotadeveloper",
    youtube: "",
    devto: "jotadeveloper",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Berlin",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
