import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Jamon Holmgren",
  about:
    "Founder & CTO @infinitered (http://infinite.red). Software engineer (JS, React/React Native, Node, Ruby, Elixir), Lutheran, husband, dad to 4.",
  image: {
    url: "people/jamon.jpg",
  },
  social: {
    homepage: "https://jamonholmgren.com/",
    twitter: "jamonholmgren",
    github: "jamonholmgren",
    facebook: "",
    medium: "jamonholmgren",
    instagram: "",
    linkedin: "jamonholmgren",
    youtube: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "Vancouver",
  },
  keywords: [Keyword.REACT, Keyword.REACT_NATIVE],
  type: [ContactType.SPEAKER],
};

export default person;
