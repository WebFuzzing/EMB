import parse from "csv-parse/lib/sync"; // TODO: Use the async version instead?
import * as fs from "fs-extra";
import filter from "lodash/filter";
import map from "lodash/map";
import mapValues from "lodash/mapValues";
import trimStart from "lodash/trimStart";
import upperFirst from "lodash/upperFirst";
import { Conference } from "../Conference";
import { Contact, ContactType } from "../Contact";

async function loadAttendees(conference: Conference, csvPath: string) {
  if (fs.existsSync(csvPath)) {
    return convertData(
      getSponsors(conference),
      parse(await fs.readFile(csvPath, { encoding: "utf8" }), {
        columns: true,
        skip_empty_lines: true,
      })
    );
  }

  return [];
}

function getSponsors(conference: Conference): Contact[] {
  return conference.sponsors.filter(
    sponsor => !sponsor.type.includes(ContactType.PARTNER)
  );
}

function convertData(sponsors: Contact[], tickets): Contact[] {
  return map(
    filter(
      map(tickets, row => mapValues(row, v => (v === "-" ? null : v))),
      t => !t["Void Status"] && !t.Ticket.startsWith("Workshop only")
    ),
    row => ({
      name: getName(row),
      firstName: getFirstName(row),
      lastName: getLastName(row),
      company: getCompany(row),
      about: "", // TODO
      image: { url: "" }, // TODO
      location: {}, // TODO
      type: [
        getType(
          sponsors,
          row.Ticket || row["Ticket Type"],
          getCompany(row),
          row.Email
        ),
      ],
      social: {
        twitter: getTwitter(row.Twitter || row["What's your Twitter handle?"]),
      },
    })
  );
}

function getName(row) {
  return `${getFirstName(row)} ${getLastName(row)}`;
}

function getFirstName(row) {
  return upperFirst(row["Ticket First Name"] || row["First Name"]);
}

function getLastName(row) {
  return upperFirst(row["Ticket Last Name"] || row["Last Name"]);
}

function getCompany(row): string {
  const name = getName(row);
  const companyName = row["Ticket Company Name"] || row["Order Company Name"];

  // Remove company if it's same as the name
  if (name === companyName || !companyName) {
    return "";
  }

  return companyName
    .split(". z o.o.")[0]
    .replace(/[ ]+/g, " ")
    .trim();
}

function getType(
  sponsors: Contact[],
  type: string,
  company: string,
  email: string
) {
  if (isSponsor(sponsors, type, company, email)) {
    return ContactType.SPONSOR;
  }

  switch (type) {
    case "Press": {
      return ContactType.PRESS;
    }
    case "Organizer": {
      return ContactType.ORGANIZER;
    }
    case "Volunteer": {
      return ContactType.ORGANIZER;
    }
    case "Sponsor": {
      return ContactType.SPONSOR;
    }
    case "Speaker": {
      return ContactType.SPEAKER;
    }
    default:
      return ContactType.ATTENDEE;
  }
}
function isSponsor(
  sponsors: Contact[],
  type: string,
  company: string,
  email: string
) {
  return sponsors.some(({ name, social: { homepage } }) => {
    if (name === company.split(" ")[0]) {
      return true;
    }

    if (email && homepage && homepage.includes(email.split("@")[1])) {
      return true;
    }

    if (company && type.includes(company.split(" ")[0])) {
      return true;
    }

    return type.includes(name);
  });
}

function getTwitter(twitter): string {
  if (twitter && twitter.split(" ").length > 1) {
    return "";
  }

  return trimStart(twitter, "'@") || "";
}

export default loadAttendees;
