const { request } = require("graphql-request");
const { range } = require("lodash");
const stringifyCsv = require("csv-stringify/lib/sync"); // TODO: Use the async version instead?

generateAttendeesCsv();

async function generateAttendeesCsv() {
  const attendees = adjustAttendees(await fetchAttendees());
  const csv = stringifyCsv(attendees, { header: true });

  console.log(csv);
}

async function fetchAttendees() {
  const {
    conference: { attendees },
  } = await request(
    "http://localhost:3000/graphql",
    `
  query AttendeeQuery($conferenceId: ID!) {
    conference(id: $conferenceId) {
      attendees {
         firstName
        lastName
        company
        social {
          twitter
        }
        type
      }
    }
  } 
 `,
    {
      conferenceId: "react-finland-2019",
    }
  );

  return attendees;
}

function adjustAttendees(attendees) {
  return attendees
    .map(({ firstName, lastName, company, social: { twitter }, type }) => ({
      firstName,
      lastName,
      company: company && company.split("Software Mansion")[0],
      twitter: twitter ? `@${twitter.split("https://twitter.com/")[1]}` : "",
      type: type[0],
    }))
    .concat(range(10).map(() => generateAttendee("ATTENDEE")))
    .concat(range(10).map(() => generateAttendee("ORGANIZER")))
    .concat(range(10).map(() => generateAttendee("SPONSOR")));
}

function generateAttendee(type) {
  return {
    firstName: "",
    lastName: "",
    twitter: "",
    type,
  };
}
