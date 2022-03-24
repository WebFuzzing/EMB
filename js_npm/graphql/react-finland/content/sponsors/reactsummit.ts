import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "React Summit",
  about: "The Biggest React Conference Worldwide",
  image: {
    url: "sponsors/react-summit.svg",
  },
  social: {
    homepage: "https://reactsummit.com",
    facebook: "",
    twitter: "",
    youtube: "ReactConferences",
  },
  location: {
    country: {
      name: "Netherlands",
      code: "NL",
    },
    city: "Amsterdam",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
