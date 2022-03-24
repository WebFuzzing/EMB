import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Elisa",
  about: "Experiences and productivity",
  image: {
    url: "sponsors/elisa.svg",
  },
  social: {
    homepage: "https://elisa.fi/",
    facebook: "elisasuomi",
    github: "ElisaOyj",
    linkedin: "company/elisa",
    youtube: "user/elisa",
    instagram: "elisaoyj",
    twitter: "elisaoyj",
    pinterest: "elisaoyj",
    googleMaps: "https://goo.gl/maps/BrqBxpNc4ZN2",
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
