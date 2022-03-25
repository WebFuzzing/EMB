import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Agent Conf",
  about: "The international event for coding inspiration",
  image: {
    url: "sponsors/agent.png",
  },
  social: {
    homepage: "https://www.agent.sh/",
    facebook: "agentsh",
    instagram: "teamagent",
    twitter: "agentconf",
  },
  location: {
    country: {
      name: "Austria",
      code: "AT",
    },
    city: "Dornbirn",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
