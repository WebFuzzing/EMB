import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "David Leitner",
  about:
    "David is Coding Architect at SQUER, a Viennese Software Company, which is working with different stacks and environments, but always an overarching mission: connect ideas and provide impact — with technology. He spends much of his time on the frontlines tackling the challenges of scaling software and complex domains, with a strong focus on reactive systems, cloud-native architectures and state of the art web technologies. David enjoys sharing his knowledge as a speaker at conferences and as a lecturer for his post-diploma courses at the University of Applied Sciences Technikum Vienna.",
  image: {
    url: "people/david-l.jpg",
  },
  social: {
    homepage: "",
    twitter: "duffleit",
    github: "duffleit",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "leitner-david",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
