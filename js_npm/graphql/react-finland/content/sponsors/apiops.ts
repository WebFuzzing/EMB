import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Apiops Meetup",
  about:
    "APIOps is about automation of any step of API design, development, onboarding, security or management.",
  image: {
    url: "sponsors/apiops.png",
  },
  social: {
    homepage: "https://www.meetup.com/Apiops-Helsinki/",
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
