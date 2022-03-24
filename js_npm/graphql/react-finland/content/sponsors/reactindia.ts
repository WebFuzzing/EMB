import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "React India",
  about: "The international React.js conference in Goa",
  image: {
    url: "sponsors/react-india.svg",
  },
  social: {
    homepage: "https://www.reactindia.io/",
    twitter: "react_india",
    facebook: "ReactJSIndia",
  },
  location: {
    country: {
      name: "India",
      code: "IN",
    },
    city: "Goa",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
