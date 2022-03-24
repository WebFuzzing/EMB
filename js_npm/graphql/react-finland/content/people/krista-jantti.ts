import { Contact, ContactType } from "../../server/schema/Contact";

const person: Contact = {
  name: "Krista Jäntti",
  about: "",
  image: {
    url: "people/krista.jpg",
  },
  social: {
    homepage: "",
    twitter: "kristatuulia",
    github: "",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [],
  type: [ContactType.SPEAKER],
};

export default person;
