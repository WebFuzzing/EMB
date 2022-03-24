import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "React Finland",
  about: "Learn More about React, Explore Finland",
  aboutShort: "Learn More about React, Explore Finland",
  image: {
    url: "react-finland/logo/v2/logo-colored-with-text.svg",
  },
  social: {
    homepage: "https://www.react-finland.fi/",
    github: "ReactFinland",
    medium: "ReactFinland",
    twitter: "ReactFinland",
    youtube: "ReactFinland",
    linkedin: "react-finland",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  type: [ContactType.ORGANIZER],
};

export default sponsor;
