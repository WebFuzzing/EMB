import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Lauri Nevanperä",
  about:
    "Lauri is a Lead Frontend developer at Alma Media. He's currently interested in Web Workers and psychological safety in work teams. Always ready for a bicycle ride or for a cup of good coffee",
  image: {
    url: "people/lauri.jpg",
  },
  social: {
    homepage: "",
    twitter: "LauriNevanpera",
    github: "wombbu",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "laurinevanpera",
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
