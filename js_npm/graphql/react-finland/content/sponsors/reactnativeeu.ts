import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "React Native EU",
  about:
    "First community conference in Europe, focused exclusively on React Native. Community, core contributors, insights, networking and tons of knowledge.",
  image: {
    url: "sponsors/react-native-eu.svg",
  },
  social: {
    homepage: "https://react-native.eu/",
    twitter: "react_native_eu",
  },
  location: {
    country: {
      name: "Poland",
      code: "PL",
    },
    city: "Wrocław",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
