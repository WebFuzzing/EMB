import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Nitor",
  about: "Digital engineering",
  image: {
    url: "sponsors/nitor.svg",
  },
  social: {
    homepage: "https://www.nitor.com/en",
    facebook: "NitorCreations",
    github: "NitorCreations",
    linkedin: "company/nitor-creations-ltd",
    instagram: "nitorcreations",
    twitter: "nitorcreations",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
