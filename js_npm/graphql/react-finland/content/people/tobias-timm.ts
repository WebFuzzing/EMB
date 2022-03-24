import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Tobias Timm",
  about: "Software engineer, co-organizer of React Munich.",
  image: {
    url: "people/tobias-timm.jpg",
  },
  social: {
    homepage: "",
    twitter: "",
    github: "tobiastimm",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Augsburg",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
