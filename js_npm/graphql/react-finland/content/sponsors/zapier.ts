import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Zapier",
  about: "Makes you happier :)",
  image: {
    url: "sponsors/zapier.png",
  },
  social: {
    homepage: "https://www.zapier.com/",
    facebook: "ZapierApp",
    linkedin: "company/zapier",
    twitter: "zapier",
    youtube: "ZapierApp",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "San Francisco",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
