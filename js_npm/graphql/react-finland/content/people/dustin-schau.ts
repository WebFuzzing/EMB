import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Dustin Schau",
  about:
    "A front-end developer specializing in all things JavaScript. I don't totally suck 👋 Software Engineer @gatsbyjs",
  image: {
    url: "people/dustin.jpg",
  },
  social: {
    homepage: "https://dustinschau.com/",
    twitter: "schaudustin",
    github: "DSchau",
    facebook: "Dustin-Schau/100004599014794",
    medium: "",
    instagram: "",
    linkedin: "dustin-schau-928907132",
    youtube: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "Omaha",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
