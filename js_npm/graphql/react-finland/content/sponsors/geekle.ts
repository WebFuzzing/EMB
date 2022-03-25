import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Geekle",
  about: "The international event for coding inspiration",
  image: {
    url: "sponsors/geekle.svg",
  },
  social: {
    homepage: "https://react.geekle.us/",
    twitter: "GeekleOfficial",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "Wilmington",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
