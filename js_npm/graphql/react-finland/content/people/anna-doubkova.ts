import { Contact, ContactType } from "../../server/schema/Contact";

const person: Contact = {
  name: "Anna Doubková",
  about: "Senior software engineer at Hive.",
  image: {
    url: "people/anna.jpg",
  },
  social: {
    twitter: "lithinn",
    github: "lithin",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "London",
  },
  keywords: [],
  type: [ContactType.SPEAKER],
};

export default person;
