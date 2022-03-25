import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "SurviveJS",
  about: "SurviveJS will take you from a JavaScript apprentice to master",
  image: {
    url: "sponsors/survivejs.svg",
  },
  social: {
    homepage: "https://survivejs.com/",
    twitter: "survivejs",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Vienna",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
