import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Creative.ai",
  about: "Artificial intelligence for the creative industries",
  image: {
    url: "sponsors/cai.svg",
  },
  social: {
    homepage: "https://creative.ai/",
    facebook: "",
    instagram: "",
    twitter: "CreativeDotAI",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Berlin",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
