import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "React Day Bangalore",
  about:
    "A day long React conference dedicated to bringing 500+ react developers together.",
  image: {
    url: "sponsors/bangalore.svg",
  },
  social: {
    homepage: "https://reactday.in",
    twitter: "ReactDayIn",
  },
  location: {
    country: {
      name: "India",
      code: "IN",
    },
    city: "Bangalore",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
