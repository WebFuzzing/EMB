import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Relex",
  about: "Optimize retail for every future",
  image: {
    url: "sponsors/relex.svg",
  },
  social: {
    homepage: "https://www.relexsolutions.com/",
    facebook: "RELEXSolutions",
    twitter: "RelexSolutions",
    linkedin: "relexsolutions",
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
