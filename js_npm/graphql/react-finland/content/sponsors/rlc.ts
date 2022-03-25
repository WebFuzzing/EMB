import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "React La Conferencia",
  about: "The First React conference for Spanish speakers in Latin America",
  image: {
    url: "sponsors/rlc.svg",
  },
  social: {
    homepage: "https://reactlaconf.co",
    facebook: "",
    instagram: "",
    twitter: "reactlaconf",
  },
  location: {
    country: {
      name: "Colombia",
      code: "CO",
    },
    city: "Medellín",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
