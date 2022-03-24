import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Nidhi Kumari",
  about: "Frontend Engineer @shoptradeco | @gsoc 2019 & 2020",
  image: {
    url: "people/nidhi.jpg",
  },
  social: {
    homepage: "https://nimbus20bportfolio.netlify.app/",
    twitter: "NidhiJha98",
    github: "NidhiKJha",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "India",
      code: "IN",
    },
    city: "",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
