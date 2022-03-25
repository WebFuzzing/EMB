import * as path from "path";
import logger from "../logger";

function routeMedia(router, mediaUrl, mediaPath) {
  router.all(`${mediaUrl}/*`, async (req, res, next) => {
    const asset = req.params["0"];

    if ([".jpg", ".png", ".svg"].includes(path.extname(asset))) {
      try {
        return res.sendFile(path.join(mediaPath, asset));
      } catch (err) {
        logger.error(err);

        next();
      }
    } else {
      res.sendFile(asset, { root: mediaPath }, (err) => {
        res.end();

        if (err) {
          logger.error(err);
        }
      });
    }
  });
}

export default routeMedia;
