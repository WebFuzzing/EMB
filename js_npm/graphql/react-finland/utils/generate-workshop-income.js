const fs = require("fs-extra");
const parseCsv = require("csv-parse/lib/sync"); // TODO: Use the async version instead?
const { groupBy, mapValues, sumBy } = require("lodash");

generateWorkshopIncome();

async function generateWorkshopIncome() {
  const workshopNames = [
    "TypeScript for React Developers",
    "Design Systems for React Developers",
    "Reactive State Machines and Statecharts",
    "React and GraphQL – From zero to production",
    "Advanced React – Suspense, Time Slicing, Hooks and more",
    "Get in the Fast Lane: Measuring React Performance",
  ];
  const workshopCuts = mapValues(
    groupBy(
      parseCsv(await fs.readFile("./attendees.csv", { encoding: "utf8" }), {
        columns: true,
        delimiter: ",",
        skip_empty_lines: true,
      })
        .map(attendee => ({
          ...attendee,
          Workshop: workshopNames.find(workshopName =>
            attendee.Ticket.includes(workshopName)
          ),
          Cut: attendee.Ticket.includes("Workshop only")
            ? Number(attendee.Price) / 2
            : Number(attendee.Price) / 4,
        }))
        .filter(attendee => attendee.Workshop),
      "Workshop"
    ),
    attendees => Math.round(sumBy(attendees, "Cut"))
  );

  console.log(
    workshopCuts,
    Object.values(workshopCuts).reduce((a, b) => a + b, 0)
  );
}
