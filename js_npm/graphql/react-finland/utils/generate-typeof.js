const cheerio = require("cheerio");
const request = require("request-promise-native");
const fs = require("fs");
const path = require("path");

main();

async function main() {
  const outputPath = path.resolve(
    process.cwd(),
    "content/typeof-2019/data.json"
  );
  const schedules = await getSchedules("https://typeofconf.com/agenda/");
  const speakers = await getSpeakers("https://typeofconf.com");

  fs.writeFile(
    outputPath,
    JSON.stringify({ schedules, speakers }, null, 2),
    err => {
      if (err) {
        return console.error(err);
      }
    }
  );
}

async function getSchedules(source) {
  const html = await request.get(source);

  return [
    {
      day: "2019-03-27",
      description: "Workshop day",
      intervals: scrapeIntervals(html, "#workshops-day"),
    },
    {
      day: "2019-03-28",
      description: "Conference day 1",
      intervals: scrapeIntervals(html, "#conf-day-1"),
    },
    {
      day: "2019-03-29",
      description: "Conference day 2",
      intervals: scrapeIntervals(html, "#conf-day-2"),
    },
  ];
}

async function getSpeakers(source) {
  const html = await request.get(source);

  return scrapeSpeakers(html);
}

function scrapeIntervals(html, selector) {
  const $ = cheerio.load(html);
  const parent = $(selector);
  const dl = $(parent, "dl");

  const schedules = $(dl)
    .find("dt")
    .map(function(i) {
      return {
        begin: $(this).text(),
        end:
          $(dl)
            .find("dt")
            .eq(i + 1)
            .text() || $(this).text(),
      };
    })
    .get();

  $(dl)
    .find("dd")
    .map(function(i) {
      const sessions = $(this)
        .find("li")
        .map(function() {
          return {
            title: $(this)
              .text()
              .split(" | ")[0]
              .trim(),
            people: $(this)
              .text()
              .split(" | ")[1]
              ? [
                  {
                    name: $(this)
                      .text()
                      .split(" | ")[1]
                      .trim(),
                  },
                ]
              : null,
          };
        })
        .get();

      schedules[i] = { ...schedules[i], sessions };
    })
    .get();

  return schedules;
}

function scrapeSpeakers(html) {
  const $ = cheerio.load(html);

  return $("#speakers .speakerCard")
    .map(function() {
      return {
        name: $(this)
          .find(".name")
          .text(),
        about: $(this)
          .find(".title")
          .text(),
        image: {
          url: `https://typeofconf.com${$(this)
            .find(".photo")
            .attr("data-src")}`,
        },
        /* TODO
        social: {
          twitter: '',
          linkedin: '',
          github: ''
        }
        */
      };
    })
    .get();
}
