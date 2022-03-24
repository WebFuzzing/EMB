import cors from "cors";
import express from "express";
import { redirectToHTTPS } from "express-http-to-https";
import * as path from "path";
import generateSchema from "../schema";
import routeAssetDesigner from "./asset-designer";
import routeCalendar from "./calendar";
import routeGraphQL from "./graphql";
import routeMedia from "./media";
import routePing from "./ping";

// FIXME: Resolve media path against project root, not module as this is brittle
const projectRoot = path.resolve(__dirname, "../../../");

async function createRouter() {
  // @ts-ignore
  const router = new express.Router();
  const schema = await generateSchema();
  const mediaUrl = "/media";
  const mediaPath = path.join(projectRoot, "media");
  const scriptPath = path.join(projectRoot, ".scripts");

  router.use(cors());
  router.use(redirectToHTTPS([/localhost:(\d{4})/]));

  routeAssetDesigner(router, projectRoot, scriptPath);
  routeCalendar(router);
  routeGraphQL(router, schema, projectRoot, mediaUrl);
  routeMedia(router, mediaUrl, mediaPath);
  routePing(router);
  router.use("/scripts", express.static(scriptPath));

  return router;
}

export default createRouter;
