import { Conference } from "../../server/schema/Conference";
import * as locations from "../locations";
import * as people from "../people";
import * as allSponsors from "../sponsors";
import schedules from "./schedules";

const partners = [
  allSponsors.agentconf,
  allSponsors.apiops,
  allSponsors.designsystemslondon,
  allSponsors.elisa,
  allSponsors.halfstack,
  allSponsors.osaango,
  allSponsors.postgraphile,
  allSponsors.reactalicante,
  allSponsors.reactdayberlin,
  allSponsors.reactjsday,
  allSponsors.reactnativeeu,
  allSponsors.survivejs,
  allSponsors.webexpo,
  allSponsors.yglfkyiv,
  allSponsors.zapier,
  allSponsors.techEvents,
];
const goldSponsors = [allSponsors.digia, allSponsors.gofore];
const silverSponsors = [allSponsors.alma];
const bronzeSponsors = [
  allSponsors.amazon,
  allSponsors.apollo,
  allSponsors.codento,
  allSponsors.gatsby,
  allSponsors.hasura,
  allSponsors.prisma,
];
const sponsors = partners.concat(goldSponsors, silverSponsors, bronzeSponsors);

const conference: Conference = {
  id: "graphql-finland-2018",
  series: "GraphQL Finland",
  name: "GraphQL Finland 2018",
  organizer: allSponsors.graphqlFinland,
  slogan: "Learn More about GraphQL, Explore Finland.",
  year: "2018",
  startDate: "2018-10-18",
  endDate: "2018-10-19",
  websiteUrl: "https://graphql-finland.fi/",
  locations: [locations.paasitorni],
  organizers: [
    people.aleksiPousar,
    people.harriMaatta,
    people.juhoVepsalainen,
    people.mikhailNovikov,
    people.samuliHakoniemi,
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
