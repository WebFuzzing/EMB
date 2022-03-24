import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Solita",
  about: "Solita is a digital business company",
  image: {
    url: "sponsors/solita.svg",
  },
  social: {
    homepage: "https://www.solita.fi/en/",
    facebook: "Solita",
    github: "solita",
    linkedin: "company/solita-oy",
    instagram: "solitaoy",
    twitter: "SolitaOy",
    youtube: "SolitaOy",
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
