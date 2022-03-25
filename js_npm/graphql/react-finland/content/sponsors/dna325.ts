import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "DNA325",
  about: "Top rated IT staffing and recruitment agency",
  image: {
    url: "sponsors/dna325.svg",
  },
  social: {
    homepage: "https://dna325.com/",
    facebook: "dna325",
    linkedin: "dna325",
  },
  location: {
    country: {
      name: "Ukraine",
      code: "UA",
    },
    city: "Kiev",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
