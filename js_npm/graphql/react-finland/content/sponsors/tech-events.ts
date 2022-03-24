import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "TechEvents.online",
  about:
    "1000+ videos from leading Tech Events about Saas, Big Data, Lean UX, Virtual Reality and AI. Watch your next one. Boost your expertise.",
  image: {
    url: "sponsors/tech-events.png",
  },
  social: {
    homepage: "https://www.techevents.online/",
    facebook: "techevents.online",
    instagram: "techevents.online",
    twitter: "TechEventOnline",
  },
  location: {
    country: {
      name: "Netherlands",
      code: "NL",
    },
    city: "Amsterdam",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
