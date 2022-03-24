import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Cybercom",
  about: `Cybercom is an IT consulting company enabling companies and organisations to benefit from the opportunities of the connected world and to enhance their competitiveness. Combining technical edge and strong business insight we provide innovative and sustainable solutions.`,
  image: {
    url: "sponsors/cybercom.svg",
  },
  social: {
    homepage: "https://www.cybercom.com",
    facebook: "cybercomfinland",
    instagram: "cybercomfinland",
    linkedin: "company/cybercom",
    twitter: "CybercomFinland",
  },
  location: {
    country: {
      name: "Sweden",
      code: "SE",
    },
    city: "Stockholm",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
