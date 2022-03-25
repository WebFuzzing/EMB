import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Nur Şah Ketene",
  about: "Managing Consultant @ Columbia Road, Advisor, Coach, Teacher.",
  image: {
    url: "people/nur.jpg",
  },
  social: {
    homepage: "https://nursahketene.com/",
    twitter: "nursahketene",
    github: "nursahketene",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "nursahketene",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
