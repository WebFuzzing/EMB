import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "React Week",
  about: "A week of React in New York",
  image: {
    url: "sponsors/reactweek.svg",
  },
  social: {
    homepage: "https://reactweek.nyc/",
    facebook: "",
    instagram: "",
    twitter: "reactweek",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "New York",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
