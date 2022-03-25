import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "m4dz 🥑🦄",
  about: `Principal Developer Advocate at ‹div›RIOTS - makers of Backlight, a Design Systems IDE built for Designers and Developers - m4dz is a curious animal. Former Frontend Dev, Devtools enthusiast, he has made the Developer eXperience (DX) his cherished topic. All the Web technologies are under his radar to get things ever simpler. His moto: "Developers are users just like you." As a life-long dreamer his favorite book will ever remains "Alice's Adventures in Wonderland".`,
  image: {
    url: "people/m4dz.jpg",
  },
  social: {
    homepage: "https://m4dz.net/",
    twitter: "m4d_z",
    github: "m4dz",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "m4d-z",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "France",
      code: "FR",
    },
    city: "Lyon",
  },
  keywords: [Keyword.REACT], // TODO: check keywords for more
  type: [ContactType.SPEAKER],
};

export default person;
