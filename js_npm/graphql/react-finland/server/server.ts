import createApp from "./app";
import logger from "./logger";

const normalizePort = (value: string) => {
  const port = Number(value);

  if (!isNaN(port) && port > 0) {
    return port;
  }

  return false;
};

async function createServer() {
  logger.debug("Starting to boot the server.");

  const port = normalizePort(process.env.PORT || "3000");
  const app = await createApp();
  let server;

  try {
    server = app.listen(port, () =>
      logger.debug(
        `Finished booting the server. The API is reachable at http://localhost:${port.toString()}/graphql.`
      )
    );
  } catch (err) {
    logger.error(err);

    process.exit(1);
  }

  // eslint-disable-line
  return server;
}

export default createServer;
