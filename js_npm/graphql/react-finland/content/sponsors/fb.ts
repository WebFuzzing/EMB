import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Facebook",
  about: "Connect with friends and the world around you on Facebook.",
  image: {
    url: "sponsors/fb.svg",
  },
  social: {
    homepage: "https://www.facebook.com/",
    facebook: "facebook",
    instagram: "facebook",
    twitter: "facebook",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "California",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
