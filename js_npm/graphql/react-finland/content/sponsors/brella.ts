import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Brella",
  about: "The world's leading virtual event platform",
  image: {
    url: "sponsors/brella.svg",
  },
  social: {
    homepage: "https://www.brella.io/",
    facebook: "brellanetwork",
    twitter: "brellanetwork",
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
