import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Lainey Feingold",
  about:
    "Lainey Feingold is a disability rights lawyer in the United States who has worked to make the digital world more inclusive since 1995 and negotiated the first web accessibility agreement in the United States in 2000. Lainey is an international speaker and the Digital Accessibility Resource for the global business to business non-profit Disability:IN.  Lainey believes in collaboration over conflict. She developed and practices Structured Negotiation, a problem-solving strategy that avoids lawsuits and focuses on lasting change and relationship-building in the digital accessibility space. She is the author of Structured Negotiation: A Winning Alternative to Lawsuits.",
  image: {
    url: "people/lainey.jpg",
  },
  social: {
    homepage: "https://www.lflegal.com/",
    twitter: "lflegal",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "laineyfeingold",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "San Francisco",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
