import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Ante Tomić",
  about: "Software Engineer at Infobip and occasional presenter.",
  image: {
    url: "people/ante.jpg",
  },
  social: {
    homepage: "",
    twitter: "",
    github: "antetmc2",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "antetomic",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Croatia",
      code: "HR",
    },
    city: "Zagreb",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
