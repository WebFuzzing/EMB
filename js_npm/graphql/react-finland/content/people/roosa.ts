import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Roosa Kallionpää",
  about:
    "Roosa, a UX and accessibility specialist at Eficode, started as a web developer and still maintains her React skills through varying projects. As a consultant, she wants to foster accessibility innovations by educating professionals in tech.",
  image: {
    url: "people/roosa.png",
  },
  social: {
    homepage: "",
    twitter: "",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "roosak",
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
