import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Brent Jackson",
  about: `Brent Jackson lives in Brooklyn, NY with his partner and calico roommate. Originally from Huntington, West Virginia, he studied Sociology, Graphic Design, and Japanese Language at Marshall University.

His career in tech has led him from Web and interaction design to user experience design to front-end development. Jackson currently works at Gatsby, previously at Kickstarter, Etsy, LivingSocial, Stitch Fix, and Priceline. He has worked on several open source projects, including Basscss, CSS Stats, Colorable, Rebass, Styled System, Theme UI, and MDX Deck.`,
  image: {
    url: "people/brent.jpg",
  },
  social: {
    homepage: "https://jxnblk.com",
    twitter: "jxnblk",
    github: "jxnblk",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "New York City",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
