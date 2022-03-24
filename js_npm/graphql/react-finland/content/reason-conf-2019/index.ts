import { Conference } from "../../server/schema/Conference";
import { SessionType } from "../../server/schema/Session";
import * as locations from "../locations";
import * as people from "../people";
import * as allSponsors from "../sponsors";
import scheduleData from "./data.json";

const sponsors = [];
const partners = [];
const goldSponsors = [];
const silverSponsors = [];
const bronzeSponsors = [];

const conference: Conference = {
  id: "reason-conf-2019",
  series: "ReasonConf",
  name: "ReasonConf 2019",
  organizer: allSponsors.reasonconf,
  slogan: "The ReasonML conference for web developers & OCaml enthusiasts",
  year: "2019",
  startDate: "2019-04-11",
  endDate: "2019-04-13",
  websiteUrl: "https://www.reason-conf.com/",
  locations: [locations.wuWien],
  mcs: [],
  organizers: [people.andreyOkonetchnikov, people.nikGraf, people.patrickEcker],
  schedules: resolveSchedules(scheduleData),
  sponsors,
  partners,
  goldSponsors,
  silverSponsors,
  bronzeSponsors,
  platformSponsors: [],
};

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
              type: resolveSessionType(session.type),
              people: resolveSessionPeople(speakers, session.people),
            };
          }),
        };
      }),
    };
  });
}

// TODO: Likely there's a smarter way to do this mapping
function resolveSessionType(type) {
  switch (type) {
    case SessionType.BREAKFAST:
      return SessionType.BREAKFAST;
    case SessionType.COFFEE_BREAK:
      return SessionType.COFFEE_BREAK;
    case SessionType.KEYNOTE:
      return SessionType.KEYNOTE;
    case SessionType.LIGHTNING_TALK:
      return SessionType.LIGHTNING_TALK;
    case SessionType.LUNCH:
      return SessionType.LUNCH;
    case SessionType.ORGANIZATIONAL:
      return SessionType.ORGANIZATIONAL;
    case SessionType.PANEL:
      return SessionType.PANEL;
    case SessionType.PARTY:
      return SessionType.PARTY;
    case SessionType.TALK:
      return SessionType.TALK;
    case SessionType.WORKSHOP:
      return SessionType.WORKSHOP;
    default:
      return SessionType.ORGANIZATIONAL;
  }
}

function resolveSessionPeople(people, sessionPeople) {
  if (!sessionPeople) {
    return undefined;
  }

  const ret = sessionPeople
    .map((name) => {
      return people.find((person) => person.name === name);
    })
    .filter(Boolean);

  return ret.length > 0 ? ret : undefined;
}

export default conference;
