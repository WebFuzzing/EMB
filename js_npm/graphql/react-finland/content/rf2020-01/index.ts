import { Conference } from "../../server/schema/Conference";
import { SessionType } from "../../server/schema/Session";
import * as locations from "../locations";
import * as people from "../people";
import * as allSponsors from "../sponsors";
import * as talks from "./talks";

const partners = [
  allSponsors.agentconf,
  allSponsors.bangalore,
  allSponsors.halfstack,
  allSponsors.jscamp,
  allSponsors.jskongress,
  // allSponsors.koodiasuomesta,
  // allSponsors.reactindia,
  allSponsors.reactnewyork,
  allSponsors.reactnorway,
  allSponsors.survivejs,
  // allSponsors.typeof,
  // allSponsors.stickermule,
  // allSponsors.reactalicante,
  allSponsors.reactdayberlin,
  allSponsors.reactjsgirls,
  allSponsors.reactnativeeu,
  // allSponsors.reactjsday,
  allSponsors.reactsummit,
  allSponsors.rlc,
  allSponsors.webexpo,
  // allSponsors.techEvents,
];
const goldSponsors = [
  allSponsors.gofore,
  allSponsors.futurice,
  allSponsors.smartly,
];
const silverSponsors = [
  allSponsors.valamis,
  allSponsors.elisa,
  allSponsors.alma,
  allSponsors.columbiaroad,
];
const bronzeSponsors = [
  allSponsors.quentic,
  allSponsors.vincit,
  allSponsors.digia,
  allSponsors.wunder,
  allSponsors.knowit,
  allSponsors.netum,
];
const sponsors = partners.concat(goldSponsors, silverSponsors, bronzeSponsors);

const conference: Conference = {
  id: "rf-2020-01",
  series: "React Finland",
  name: "React Finland 2020 - Mini-conference #1 - Documentation",
  organizer: allSponsors.reactFinland,
  slogan: "Learn More about React, Explore Finland",
  year: "2020",
  startDate: "2020-05-28",
  endDate: "2020-05-28",
  websiteUrl: "https://react-finland.fi/",
  // TODO: Resolve locations based on data
  locations: [],
  mcs: [people.janiEvakallio], // , people.saraVieira],
  organizers: [
    people.aleksiPousar,
    people.eemeliAro,
    people.harriMaatta,
    people.juhoVepsalainen,
    people.mikhailNovikov,
    people.samuliHakoniemi,
    people.toniRistola,
    people.tuuliTiilikainen,
  ],
  schedules: [
    {
      day: "2020-05-28",
      description: "Online mini-conference",
      location: locations.internet,
      intervals: [
        {
          begin: "17:00",
          end: "17:30",
          sessions: [
            {
              type: SessionType.ORGANIZATIONAL,
              title: "Registration",
              description: "",
            },
          ],
        },
        {
          begin: "17:30",
          end: "17:45",
          sessions: [
            {
              type: SessionType.ORGANIZATIONAL,
              title: "Opening",
              description: "",
            },
          ],
        },
        {
          begin: "17:45",
          end: "18:15",
          sessions: [talks.carolyn],
        },
        {
          begin: "18:15",
          end: "18:30",
          sessions: [
            {
              type: SessionType.ORGANIZATIONAL,
              title: "QA",
              description: "",
            },
          ],
        },
        {
          begin: "18:30",
          end: "19:00",
          sessions: [talks.kenigboloMeyaStephen],
        },
        {
          begin: "19:00",
          end: "19:15",
          sessions: [
            {
              type: SessionType.ORGANIZATIONAL,
              title: "QA",
              description: "",
            },
          ],
        },
        {
          begin: "19:15",
          end: "19:45",
          sessions: [talks.margarita],
        },
        {
          begin: "19:45",
          end: "20:00",
          sessions: [
            {
              type: SessionType.ORGANIZATIONAL,
              title: "QA",
              description: "",
            },
          ],
        },
        {
          begin: "20:00",
          end: "20:30",
          sessions: [
            {
              type: SessionType.ORGANIZATIONAL,
              title: "Panel",
              description: "",
            },
          ],
        },
        {
          begin: "20:30",
          end: "21:00",
          sessions: [
            {
              type: SessionType.ORGANIZATIONAL,
              title: "Freeform",
              description: "",
            },
          ],
        },
      ],
    },
  ],
  sponsors,
  partners,
  goldSponsors,
  silverSponsors,
  bronzeSponsors,
  platformSponsors: [],
};

export default conference;
