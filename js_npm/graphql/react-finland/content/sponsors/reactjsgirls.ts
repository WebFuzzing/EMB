import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "ReactJS Girls",
  about: "A conference for every React developer where women take the stage",
  image: {
    url: "sponsors/reactjsgirls.svg",
  },
  social: {
    homepage: "https://reactjsgirls.com/",
    twitter: "reactjsgirls",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "London",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
