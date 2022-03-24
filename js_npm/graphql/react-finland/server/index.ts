import dotenv from "dotenv";
import * as path from "path";
import "reflect-metadata"; // Needed by type-graphql

// FIXME: Resolve against project root, not module as this is brittle
dotenv.config({
  path: path.resolve(__dirname, "../../.env"),
});

import logger from "./logger";
import rebuildSites from "./rebuild-sites";
import createServer from "./server";

process.on("SIGINT", () => {
  logger.info("\nGracefully shutting down from SIGINT (Ctrl-C)");

  process.exit(1);
});

(async () => {
  logger.debug("Starting application");

  try {
    await createServer();

    logger.debug("Running");

    if (process.env.NODE_ENV === "production") {
      rebuildSites(process.env.REBUILD_SITES);
    }
  } catch (e) {
    logger.error("Failed to start");
    logger.error(e.stack);
  }
})();
