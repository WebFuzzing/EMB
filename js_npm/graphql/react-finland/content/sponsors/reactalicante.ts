import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "React Alicante",
  about: "The international React.js conference in Spain",
  image: {
    url: "sponsors/react-alicante.svg",
  },
  social: {
    homepage: "http://reactalicante.es/",
    twitter: "reactalicante",
  },
  location: {
    country: {
      name: "Spain",
      code: "ES",
    },
    city: "Alicante",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
