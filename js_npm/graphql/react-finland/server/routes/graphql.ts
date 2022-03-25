import graphql from "express-graphql";
import process from "process";

function routeGraphQL(router, schema, projectRoot, mediaUrl) {
  router.all(
    "/graphql",
    graphql((request) => {
      const hostname = getHostname(request);

      return {
        graphiql: true,
        pretty: true,
        schema,
        context: {
          hostname,
          mediaUrl: `${hostname}${mediaUrl}`,
          projectRoot,
        },
      };
    })
  );
}

// TODO: Move to utils
function getHostname(req) {
  if (process.env.HEROKU_HOSTNAME) {
    return process.env.HEROKU_HOSTNAME;
  }

  return req.protocol + "://" + req.get("host");
}

export default routeGraphQL;
