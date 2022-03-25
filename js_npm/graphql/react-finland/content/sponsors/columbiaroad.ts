import { Contact, ContactType } from "../../server/schema/Contact";

const sponsor: Contact = {
  name: "Columbia Road",
  about: "A Nordic Digital Sales Consultancy",
  image: {
    url: "sponsors/columbiaroad.svg",
  },
  social: {
    homepage: "https://www.columbiaroad.com/",
    facebook: "ColumbiaRoadCom",
    github: "",
    linkedin: "company/columbia-road",
    youtube: "channel/UC5v0xp5cyUIcNCkcXqLEOLA",
    instagram: "columbiaroadcom",
    twitter: "ColumbiaRoadCom",
    pinterest: "",
  },
  // Also Stockholm, Sweden -> TODO: Make the field an array
  location: {
    country: {
      name: "Finland",
      code: "FI",
    },
    city: "Helsinki",
  },
  type: [ContactType.SPONSOR],
};

export default sponsor;
