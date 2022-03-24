import { Conference } from "../../server/schema/Conference";
import * as locations from "../locations";
import * as people from "../people";
import * as allSponsors from "../sponsors";
import schedules from "./schedules";

const partners = [allSponsors.reactFinland, allSponsors.survivejs];
const goldSponsors = [];
const silverSponsors = [];
const bronzeSponsors = [];
const sponsors = partners.concat(goldSponsors, silverSponsors, bronzeSponsors);

const conference: Conference = {
  id: "freezing-edge-2020",
  series: "Freezing Edge",
  name: "Freezing Edge 2020",
  organizer: allSponsors.reactFinland,
  slogan: "The edge isn't bleeding, it's freezing!",
  year: "2020",
  startDate: "2020-06-01",
  endDate: "2020-06-01",
  websiteUrl: "https://freezing-edge.fi/",
  locations: [locations.amanda],
  mcs: [], // TODO
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
