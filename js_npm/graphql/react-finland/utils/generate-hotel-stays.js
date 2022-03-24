const fs = require("fs");
const readline = require("readline");
const { google } = require("googleapis");
const { flatMap, fromPairs, sortBy, toPairs } = require("lodash");
const camelcase = require("camelcase");
const dateRangeArray = require("date-range-array");
const zerofill = require("zerofill");

// If modifying these scopes, delete token.json.
const SCOPES = ["https://www.googleapis.com/auth/spreadsheets"];
// The file token.json stores the user's access and refresh tokens, and is
// created automatically when the authorization flow completes for the first
// time.
const TOKEN_PATH = "google-token.json";

// Load client secrets from a local file. You should generate this through
// Google's interface online.
fs.readFile("google-credentials.json", (err, content) => {
  if (err) return console.log("Error loading client secret file:", err);
  // Authorize a client with credentials, then call the Google Sheets API.
  authorize(JSON.parse(content), convertToDateRanges);
});

/**
 * Create an OAuth2 client with the given credentials, and then execute the
 * given callback function.
 * @param {Object} credentials The authorization client credentials.
 * @param {function} callback The callback to call with the authorized client.
 */
function authorize(credentials, callback) {
  const { client_secret, client_id, redirect_uris } = credentials.installed;
  const oAuth2Client = new google.auth.OAuth2(
    client_id,
    client_secret,
    redirect_uris[0]
  );

  // Check if we have previously stored a token.
  fs.readFile(TOKEN_PATH, (err, token) => {
    if (err) return getNewToken(oAuth2Client, callback);
    oAuth2Client.setCredentials(JSON.parse(token));
    callback(oAuth2Client);
  });
}

/**
 * Get and store new token after prompting for user authorization, and then
 * execute the given callback with the authorized OAuth2 client.
 * @param {google.auth.OAuth2} oAuth2Client The OAuth2 client to get token for.
 * @param {getEventsCallback} callback The callback for the authorized client.
 */
function getNewToken(oAuth2Client, callback) {
  const authUrl = oAuth2Client.generateAuthUrl({
    access_type: "offline",
    scope: SCOPES,
  });
  console.log("Authorize this app by visiting this url:", authUrl);
  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
  });
  rl.question("Enter the code from that page here: ", code => {
    rl.close();
    oAuth2Client.getToken(code, (err, token) => {
      if (err)
        return console.error(
          "Error while trying to retrieve access token",
          err
        );
      oAuth2Client.setCredentials(token);
      // Store the token to disk for later program executions
      fs.writeFile(TOKEN_PATH, JSON.stringify(token), err => {
        if (err) return console.error(err);
        console.log("Token stored to", TOKEN_PATH);
      });
      callback(oAuth2Client);
    });
  });
}

/**
 * Writes amounts of hotel nights needed to the budget sheet.
 * @param {google.auth.OAuth2} auth The authenticated Google OAuth client.
 */
function convertToDateRanges(auth) {
  const spreadsheetId = "1lgRRou8qPL3yylT5MMz3IXZBtGeq_g9sqruE4x3qzqY";
  const sheets = google.sheets({ version: "v4", auth });

  sheets.spreadsheets.values.get(
    {
      spreadsheetId,
      range: "2019 Speakers!A4:I",
    },
    (err, res) => {
      if (err) {
        return console.log("The API returned an error: " + err);
      }

      const rows = res.data.values;
      if (rows.length) {
        const keys = rows[0];
        const values = rows.slice(1);
        const people = toObject(keys, values).map(
          ({ arrives, leaves, ...rest }) => ({
            ...rest,
            arrives,
            leaves,
            stays:
              arrives.length > 1 && leaves.length > 1
                ? dateRangeArray(
                    UStoYearMonthDay(arrives),
                    UStoYearMonthDay(leaves)
                  )
                : [],
          })
        );
        const stayAmounts = calculateStays(
          flatMap(people, ({ stays }) => stays)
        ).map(([date, amount]) => {
          return [date.toLocaleString().split(", ")[0], amount];
        });

        console.log(stayAmounts);

        sheets.spreadsheets.values.batchUpdate(
          {
            spreadsheetId,
            resource: {
              data: [
                {
                  range: "Hotel Information!A:C",
                  values: [["Date", "Amount"]].concat(stayAmounts),
                },
              ],
            },
            valueInputOption: "USER_ENTERED",
          },
          (err, result) => {
            if (err) {
              return console.error(err);
            }

            console.log(`${result.data.totalUpdatedCells} cells updated`);
          }
        );
      } else {
        console.log("No data found.");
      }
    }
  );
}

function calculateStays(stays) {
  const ret = {};

  stays.forEach(stay => {
    if (!ret[stay]) {
      ret[stay] = 0;
    }

    ret[stay]++;
  });

  return sortBy(
    toPairs(ret).map(([date, stays]) => {
      return [new Date(date), stays];
    }),
    ([date]) => date
  );
}

// 4/23/2019 -> 2019-04-23
function UStoYearMonthDay(date) {
  const [month, day, year] = date.split("/");

  return `${zerofill(year, 4)}-${zerofill(month, 2)}-${zerofill(day, 2)}`;
}

function toObject(keys, rows) {
  return rows.map((row, i) => {
    return fromPairs(keys.map((key, j) => [camelcase(key), row[j]]));
  });
}
