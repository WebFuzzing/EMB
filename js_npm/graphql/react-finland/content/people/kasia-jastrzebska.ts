import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Kasia Jastrzębska",
  about:
    "Frontend developer with over 6 years of experience around various frameworks and libs knowledge in my basket (such as React+Redux, Polymer, Ember, Backbone). Always eager to go deeper ;D",
  image: {
    url: "people/kasia.jpg",
  },
  social: {
    homepage: "",
    twitter: "kejt_bw",
    github: "kajas90",
    linkedin: "kasia-jastrzębska-8369a664",
  },
  location: {
    country: {
      name: "Poland",
      code: "PL",
    },
    city: "Gdansk",
  },
  keywords: [Keyword.REACT, Keyword.SSR, Keyword.STYLING],
  type: [ContactType.SPEAKER],
};

export default person;
