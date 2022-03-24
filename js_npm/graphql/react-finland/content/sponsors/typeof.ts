import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "typeof",
  about: "typeof is a web craftsmanship conference",
  image: {
    url: "sponsors/typeof.svg",
  },
  social: {
    homepage: "https://typeofconf.com/",
    facebook: "typeofconf",
    twitter: "typeofconf",
    instagram: "typeofconf",
  },
  location: {
    country: {
      name: "Portugal",
      code: "PT",
    },
    city: "Porto",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
