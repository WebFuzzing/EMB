import { Conference } from "../../server/schema/Conference";
import { Schedule } from "../../server/schema/Schedule";
import * as locations from "../locations";
// import * as people from "../people";
import * as allSponsors from "../sponsors";

const sponsors = [];
const partners = [];
const goldSponsors = [];
const silverSponsors = [];
const bronzeSponsors = [];

const conferenceDate = "2019-09-19";

const schedule: Schedule = {
  day: conferenceDate,
  description: "Conference day",
  location: locations.ankerSaal,
  intervals: [],
};

const conference: Conference = {
  id: "halfstack-vienna-2019",
  series: "halfstack",
  name: "HalfStack 2019",
  organizer: allSponsors.halfstack,
  slogan: "One day, single track, fun with JavaScript",
  year: "2019",
  startDate: conferenceDate,
  endDate: conferenceDate,
  websiteUrl: "https://halfstackconf.com/vienna/",
  locations: [locations.ankerSaal],
  mcs: [],
  organizers: [],
  schedules: [schedule],
  sponsors,
  partners,
  goldSponsors,
  silverSponsors,
  bronzeSponsors,
  platformSponsors: [],
};

export default conference;
