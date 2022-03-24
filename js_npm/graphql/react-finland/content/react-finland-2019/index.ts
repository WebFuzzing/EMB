import { Conference } from "../../server/schema/Conference";
import * as locations from "../locations";
import * as people from "../people";
import * as allSponsors from "../sponsors";
import schedules from "./schedules";

const partners = [
  allSponsors.agentconf,
  allSponsors.halfstack,
  allSponsors.jscamp,
  allSponsors.jskongress,
  allSponsors.koodiasuomesta,
  allSponsors.reasonconf,
  allSponsors.reactindia,
  allSponsors.reactnewyork,
  allSponsors.reactnorway,
  allSponsors.survivejs,
  allSponsors.typeof,
  allSponsors.zapier,
  allSponsors.stickermule,
  allSponsors.reactalicante,
  allSponsors.reactdayberlin,
  allSponsors.reactjsgirls,
  allSponsors.reactnativeeu,
  allSponsors.reactjsday,
  allSponsors.webexpo,
  allSponsors.techEvents,
];
const goldSponsors = [
  allSponsors.gofore,
  allSponsors.futurice,
  allSponsors.digia,
];
const silverSponsors = [
  allSponsors.alma,
  allSponsors.elisa,
  allSponsors.nightingale,
  allSponsors.reaktor,
  allSponsors.solita,
];
const bronzeSponsors = [
  allSponsors.codento,
  allSponsors.columbiaroad,
  allSponsors.fb,
  allSponsors.formidable,
  allSponsors.gatsby,
  allSponsors.ginetta,
  allSponsors.netum,
  allSponsors.meiko,
  allSponsors.vincit,
  allSponsors.verkkokauppa,
  allSponsors.wunder,
  allSponsors.zeroplusx,
];
const sponsors = partners.concat(goldSponsors, silverSponsors, bronzeSponsors);

const conference: Conference = {
  id: "react-finland-2019",
  series: "React Finland",
  name: "React Finland 2019",
  organizer: allSponsors.reactFinland,
  slogan: "Learn More about React, Explore Finland.",
  year: "2019",
  startDate: "2019-04-24",
  endDate: "2019-04-26",
  websiteUrl: "https://react-finland.fi/",
  // TODO: Resolve locations based on data
  locations: [locations.paasitorni, locations.tiivistamo],
  mcs: [people.janiEvakallio, people.saraVieira],
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
  platformSponsors: [],
};

export default conference;
