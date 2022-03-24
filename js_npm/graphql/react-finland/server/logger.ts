// https://github.com/amazeeio/lagoon/blob/master/services/api/src/logger.js
import * as fs from "fs";
import * as path from "path";
import { isString } from "util";
import { createLogger, format, transports } from "winston";

// Create the logs directory if it doesn't exist yet.
const directory = path.join(".", "logs");
if (!fs.existsSync(directory)) {
  fs.mkdirSync(directory);
}

const ignorePingAndMedia = format(info => {
  const message = info.message;

  if (
    isString(message) &&
    ["GET /ping ", "GET /media"].some(match => message.includes(match))
  ) {
    return false;
  }

  return info;
});

const logger = createLogger({
  exitOnError: false,
  format: format.combine(ignorePingAndMedia(), format.json()),
  transports: [
    new transports.Console({
      level: "debug",
      handleExceptions: true,
    }),
  ],
});

export default logger;
