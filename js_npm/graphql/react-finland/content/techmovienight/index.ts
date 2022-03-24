import { Conference } from "../../server/schema/Conference";
import { Schedule } from "../../server/schema/Schedule";
import * as locations from "../locations";
import * as people from "../people";
import * as allSponsors from "../sponsors";

const latestMeetupDate = "2019-06-22";

const schedule: Schedule = {
  day: latestMeetupDate,
  description: "React meetup",
  location: locations.saeWien,
  intervals: [],
};

const conference: Conference = {
  id: "techmovienight",
  series: "techmovienight",
  name: "Tech Movie Night",
  organizer: allSponsors.survivejs,
  slogan: "Tech movies together",
  year: "2019",
  startDate: latestMeetupDate,
  endDate: latestMeetupDate,
  websiteUrl: "https://techmovienight.com/",
  locations: [locations.saeWien],
  mcs: [],
  organizers: [people.juhoVepsalainen],
  schedules: [schedule],
  sponsors: [],
  partners: [
    allSponsors.survivejs,
    allSponsors.halfstack,
    allSponsors.honeypot,
    allSponsors.sae,
  ],
  goldSponsors: [],
  silverSponsors: [],
  bronzeSponsors: [],
  platformSponsors: [],
};

export default conference;
