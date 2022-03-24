import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Kristopher Jackson",
  about:
    "Experienced Business Development Specialist with a demonstrated history of working in the information technology and services industry. Skilled in Sales, Customer Relationship Management (CRM), Event Management, Management, and Account Management. Strong business development professional with a Finance focused in Economics, and African American History from University of Illinois at Urbana-Champaign.",
  image: {
    url: "people/kristopher.jpg",
  },
  social: {
    homepage: "",
    twitter: "",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "jacksonkristopher",
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
