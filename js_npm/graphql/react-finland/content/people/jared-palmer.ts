import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Jared Palmer",
  about:
    "I am passionate about making open source software. My libraries and tools are used in production by thousands of developers including teams at Microsoft, Amazon, Twitter, Airbnb, Walmart, Verizon, Lyft, Stripe, Samsung, US Army, NASA, NOAA, 18F, Canadian Design Service, Morgan Stanley, Goldman Sachs, NFL, MLB, and Coinbase.",
  image: {
    url: "people/jared.jpg",
  },
  social: {
    homepage: "https://jaredpalmer.com",
    twitter: "jaredpalmer",
    github: "jaredpalmer",
    facebook: "",
    medium: "",
    instagram: "jaredpalmer",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "New York",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
