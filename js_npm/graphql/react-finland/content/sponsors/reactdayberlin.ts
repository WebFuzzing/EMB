import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "React Day Berlin",
  about: "Fully Packed Day of Your Favorite React Content",
  image: {
    url: "sponsors/react-day-berlin.png",
  },
  social: {
    homepage: "https://reactday.berlin/",
    facebook: "reactdayberlin",
    twitter: "reactdayberlin",
    youtube: "UC1EYHmQYBUJjkmL6OtK4rlw",
  },
  location: {
    country: {
      name: "Germany",
      code: "DE",
    },
    city: "Berlin",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
