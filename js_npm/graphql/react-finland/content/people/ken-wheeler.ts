import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Ken Wheeler",
  about:
    "Technology Artist - Synthwave & Hip Hop Producer - Dad - Grilling Enthusiast",
  image: {
    url: "people/ken.jpg",
  },
  social: {
    homepage: "",
    twitter: "ken_wheeler",
    github: "kenwheeler",
    linkedin: "kennywheeler",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "Allenwood",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
