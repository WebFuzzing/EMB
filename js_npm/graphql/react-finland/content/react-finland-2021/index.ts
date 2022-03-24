import { Conference } from "../../server/schema/Conference";
// import * as locations from "../locations";
import * as people from "../people";
import * as allSponsors from "../sponsors";
import schedules from "./schedules";

const partners = [
  allSponsors.a11Yclub,
  allSponsors.agentconf,
  // allSponsors.bangalore,
  allSponsors.dna325,
  allSponsors.halfstack,
  allSponsors.jscamp,
  allSponsors.jskongress,
  allSponsors.koodiasuomesta,
  // allSponsors.reactindia,
  allSponsors.reactnewyork,
  allSponsors.reactnorway,
  allSponsors.survivejs,
  allSponsors.reactbrussels,
  // allSponsors.stickermule,
  allSponsors.reactjsgirls,
  allSponsors.reactnativeeu,
  allSponsors.reactjsday,
  allSponsors.reactsummit,
  allSponsors.webexpo,
  // allSponsors.techEvents,
  allSponsors.dynatrace,
];
const goldSponsors = [
  allSponsors.gofore,
  allSponsors.futurice,
  allSponsors.smartly,
];
const silverSponsors = [
  allSponsors.digia,
  allSponsors.elisa,
  allSponsors.alma,
  allSponsors.columbiaroad,
];
const bronzeSponsors = [
  allSponsors.relex,
  allSponsors.vincit,
  allSponsors.wunder,
  allSponsors.knowit,
  allSponsors.netum,
  allSponsors.nitor,
];
const platformSponsors = [allSponsors.brella];
const sponsors = partners.concat(
  goldSponsors,
  silverSponsors,
  bronzeSponsors,
  platformSponsors
);

const conference: Conference = {
  id: "react-finland-2021",
  series: "React Finland",
  name: "React Finland 2021",
  organizer: allSponsors.reactFinland,
  slogan: "Learn More about React, Explore Finland",
  year: "2021",
  startDate: "2020-08-30",
  endDate: "2020-09-03",
  websiteUrl: "https://react-finland.fi/",
  // TODO: Resolve locations based on data
  locations: [],
  mcs: [people.juhoVepsalainen, people.saraVieira, people.manjula],
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
  schedules,
  sponsors,
  partners,
  goldSponsors,
  silverSponsors,
  bronzeSponsors,
  platformSponsors,
};

export default conference;
