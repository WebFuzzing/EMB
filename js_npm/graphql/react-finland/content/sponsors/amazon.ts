import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Amazon Web Services",
  about:
    "Amazon Web Services offers reliable, scalable, and inexpensive cloud computing services. ",
  image: {
    url: "sponsors/amazon.png",
  },
  social: {
    homepage: "https://aws.amazon.com/",
    twitter: "AWSCloud",
  },
  location: {
    country: {
      name: "USA",
      code: "US",
    },
    city: "Seattle",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
