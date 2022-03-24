import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "React Brussels",
  about: "The first React conference in Brussels",
  image: {
    url: "sponsors/reactbrussels.svg",
  },
  social: {
    homepage:
      "https://www.react.brussels/?utm_source=reactfinland&utm_medium=referral&utm_campaign=partners-2021",
    facebook: "events/756038281737514",
    twitter: "BrusselsReact",
    youtube: "UCvES7lMpnx-t934qGxD4w4g",
  },
  location: {
    country: {
      name: "Belgium",
      code: "BE",
    },
    city: "Brussels",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
