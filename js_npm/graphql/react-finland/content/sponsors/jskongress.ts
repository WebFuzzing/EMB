import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "JSKongress",
  about: "(r)evolution of Architectures",
  image: {
    url: "sponsors/jskongress.png",
  },
  social: {
    homepage:
      "https://js-kongress.com/?utm_source=reactfi&utm_medium=koop&utm_campaign=20190118&utm_nooverride=1",
    facebook: "",
    instagram: "",
    twitter: "JSKongress",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Munich",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
