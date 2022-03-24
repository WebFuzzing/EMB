[![Codeship Status for ReactFinland/graphql-api](https://app.codeship.com/projects/f27d4b30-0088-0137-84e3-2ae3d6f1076b/status?branch=master)](https://app.codeship.com/projects/324651)

# GraphQL API for TOSKA conferences

Simple GraphQL API for our conferences:

To see the query API:

1.  `NODE_ENV="development" npm start`
2.  `http://localhost:3000/graphql` or `http://localhost:3000/calendar-2022.ics` (calendar)

## Image Processing

There's no special image processing in place at the moment.

> You can access images through `http://localhost:3000/media`. Example: `http://localhost:3000/media/people/ken.jpg`.

## Webhooks

See the webhooks below.

### Heroku to Netlify

The API has been connected to the site using a webhook so that it builds whenever the API changes:

- `heroku config:set REBUILD_SITES=<Netlify urls separate by comma go here> -a react-finland-api`

> Remember to run the server in production mode. That will enable site rebuilding hook!
