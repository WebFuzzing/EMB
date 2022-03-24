import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Artem Zakharchenko",
  about:
    "Full-stack JavaScript engineer at Oracle, medical doctor, musician, designer. A huge endorser of functional programming and clean project architecture.",
  image: {
    url: "people/artem-z.jpg",
  },
  social: {
    homepage: "https://redd.one",
    twitter: "kettanaito",
    github: "kettanaito",
    linkedin: "artem-zakharchenko",
  },
  location: {
    country: {
      name: "Ukraine",
      code: "UA",
    },
    city: "",
  },
  keywords: [Keyword.REACT, Keyword.STYLING, Keyword.TOOLING],
  type: [ContactType.SPEAKER],
};

export default person;
