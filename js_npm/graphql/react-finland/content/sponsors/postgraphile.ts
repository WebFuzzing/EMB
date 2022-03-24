import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Postgraphile",
  about: "Instant GraphQL API for PostgreSQL database",
  image: {
    url: "sponsors/postgraphile.svg",
  },
  social: {
    homepage: "https://www.graphile.org/postgraphile/",
    twitter: "Benjie",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "Southampton",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
