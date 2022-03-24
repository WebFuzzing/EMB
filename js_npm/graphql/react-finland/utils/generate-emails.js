const fs = require("fs-extra");
const parseCsv = require("csv-parse/lib/sync"); // TODO: Use the async version instead?

generateEmails();

async function generateEmails() {
  const attendees = parseCsv(
    await fs.readFile("./attendees.csv", { encoding: "utf8" }),
    {
      columns: true,
      delimiter: ",",
      skip_empty_lines: true,
    }
  );
  const emails = attendees.map(sel => sel['Ticket Email']).filter(Boolean);

  console.log(emails.join(','));
}
