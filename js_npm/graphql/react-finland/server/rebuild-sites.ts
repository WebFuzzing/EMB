import request from "request";
import logger from "./logger";

// Expects a comma separated string
function rebuildSites(urls) {
  urls.split(",").forEach(rebuildSite);
}

function rebuildSite(url) {
  request.post(url, err => {
    if (err) {
      return logger.error(err);
    }

    return logger.info(`rebuilt ${url}`);
  });
}

export default rebuildSites;
