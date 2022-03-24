import expeditousEngineMemory from "expeditious-engine-memory";
import getExpeditiousCache from "express-expeditious";
import * as React from "react";
import "reflect-metadata"; // Needed for introspecting classes
import createInteractive from "./components/Interactive";
import AssetDesignerPage from "./pages/AssetDesignerPage";
import renderPage from "./render-page";

const cache = getExpeditiousCache({
  namespace: "assetdesignercache",
  defaultTtl: "1 year", // Cache till next restart
  engine: expeditousEngineMemory(), // TODO: Set up Redis
});

async function routeAssetDesigner(router, projectRoot, scriptRoot) {
  const Interactive = createInteractive(projectRoot, scriptRoot, __dirname);

  router.get(
    "/asset-designer",
    cache.withTtl("1 hour"),
    async ({ url, query }, res) => {
      const selectionId = query.selectionId;
      let bundlers = 0;

      // Given we use the same page for serving different bundles,
      // each needs a unique identifier based on the query (differing part)
      // TODO: Is this true anymore?
      const pageHtml = renderPage(
        "Asset designer",
        url,
        <Interactive.Provider
          value={{
            registerBundler: () => {
              bundlers++;
            },
            bundlingCompleted: err => {
              bundlers--;

              // TODO: Figure out how to handle Fusebox home directory
              /*if (err) {
                console.error(err);

                return res.status(500).send("Failed to bundle frontend");
              }*/

              if (bundlers === 0) {
                console.log("Done bundling");

                res.status(200).send(pageHtml);
              }
            },
          }}
        >
          <Interactive.Consumer
            relativeComponentPath="./pages/AssetDesignerPage"
            component={AssetDesignerPage}
            componentHash={selectionId}
            props={{ initialState: { selectionId } }}
          />
        </Interactive.Provider>
      );
    }
  );
}

export default routeAssetDesigner;
