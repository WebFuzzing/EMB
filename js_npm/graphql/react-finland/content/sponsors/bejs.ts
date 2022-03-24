import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "BeJS",
  about: "The JavaScript conference in Brussels",
  image: {
    url: "sponsors/bejs.svg",
  },
  social: {
    homepage:
      "https://www.bejs.io/conf?utm_source=Partners&utm_campaign=LaunchCampaign&utm_medium=reactfinland",
    twitter: "BeJS_",
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
