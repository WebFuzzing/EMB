const request = require("request-promise-native");
const fs = require("fs");
const path = require("path");

main();

async function main() {
  const outputPath = path.resolve(
    process.cwd(),
    "content/reason-conf-2019/data.json"
  );

  fs.writeFile(outputPath, JSON.stringify(await getData(), null, 2), err => {
    if (err) {
      return console.error(err);
    }
  });
}

async function getData() {
  try {
    const { files } = await request.get(
      "https://api.github.com/gists/992b79b40cea84ff7ca8efb8cc2350ee",
      {
        headers: {
          "User-Agent": "React-Finland",
        },
        json: true,
      }
    );

    return {
      schedules: JSON.parse(files["schedule.json"].content),
      speakers: JSON.parse(files["speakers.json"].content),
    };
  } catch (err) {
    console.error(err);
  }

  return {};
}
