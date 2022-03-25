import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Marianna Österlund",
  about:
    "Marianna is an accessibility specialist at Selko Digital Oy in Finland. She is a young – yet enthusiastic envoy for a better and more inclusive Internet. For her, accessibility is equality and good service for everyone. She is a many-sided accessibility expert who aims to understand the different types of users, their needs and how to address them in web content.",
  image: {
    url: "people/marianna.jpg",
  },
  social: {
    homepage: "",
    twitter: "",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "marianna-%C3%B6sterlund",
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
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
