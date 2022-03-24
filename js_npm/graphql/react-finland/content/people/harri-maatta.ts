import { Contact, ContactType } from "../../server/schema/Contact";

const person: Contact = {
  name: "Harri Määttä",
  about:
    "Mobile Developer. Also DevOps Specialist. Otherwise nice guy. Lives in Helsinki, so can help with local issues (=I know bars!)",
  image: {
    url: "people/harri.jpg",
  },
  social: {
    homepage: "",
    twitter: "HarriMaatta",
    github: "HarriMaatta",
    linkedin: "harrimaatta",
  },
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
  },
  keywords: [],
  type: [ContactType.ORGANIZER],
};

export default person;
