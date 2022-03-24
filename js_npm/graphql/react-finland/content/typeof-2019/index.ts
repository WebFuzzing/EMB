import { Conference } from "../../server/schema/Conference";
import { ContactType } from "../../server/schema/Contact";
import { SessionType } from "../../server/schema/Session";
import * as people from "../people";
import * as allSponsors from "../sponsors";
import scheduleData from "./data.json";

const sponsors = [];
const partners = [];
const goldSponsors = [];
const silverSponsors = [];
const bronzeSponsors = [];

const conference: Conference = {
  id: "typeof-2019",
  series: "typeof",
  name: "typeof 2019",
  organizer: allSponsors.typeof,
  slogan: "web craftsmanship conference",
  year: "2019",
  startDate: "2019-04-27",
  endDate: "2019-04-29",
  websiteUrl: "https://typeofconf.com/",
  locations: [],
  mcs: [],
  organizers: [],
  schedules: resolveSchedules(scheduleData),
  sponsors,
  partners,
  goldSponsors,
  silverSponsors,
  bronzeSponsors,
  platformSponsors: [],
};

// TODO: Attach images from the site (needs improved data fetcher)
function resolveSchedules({ schedules, speakers }) {
  return schedules.map((schedule) => {
    return {
      ...schedule,
      intervals: schedule.intervals.map((interval) => {
        return {
          ...interval,
          sessions: interval.sessions.map((session) => {
            return {
              ...session,
              // TODO: Not correct but good enough for a demo
              type: SessionType.TALK,
              people:
                session.people &&
                session.people.map((person) => {
                  const existingPerson =
                    Object.values(people).find(({ name }) => {
                      return name === person.name;
                    }) ||
                    speakers.find(({ name }) => {
                      return name === person.name;
                    });

                  return {
                    about: "",
                    type: [ContactType.SPEAKER],
                    social: {},
                    location: {},
                    ...person,
                    ...existingPerson,
                  };
                }),
            };
          }),
        };
      }),
    };
  });
}

export default conference;
