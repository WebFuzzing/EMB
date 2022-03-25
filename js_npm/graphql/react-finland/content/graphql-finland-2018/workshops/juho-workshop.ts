import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import { paasitorni } from "../../locations";
import juho from "../../people/juho-vepsalainen";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [juho],
  title: "Let’s Learn GraphQL",
  description: `
So you want to learn GraphQL? That is awesome! But like any other technology it's not easy to get started and get the basics of it mainly because all the docs are spread all over the place.

In this 8 hour workshop we will create a GraphQL server with Apollo Server, connect it to a Postgres database in Heroku to have a fully functional GraphQL Server.

After that we will jump to the frontend using React and Apollo to query and mutate a GraphQL Endpoint easily.

## Requirements

- Node installed, I have version 10.3.0 but anything that supports async await is great!
- I use yarn but npm is 💯
- Git
- Heroku Account - [https://heroku.com/](https://heroku.com/)
- Postgres DB in Heroku - Video on how to do it: [https://cdn.rawgit.com/SaraVieira/graphql-workshop/13d73f3b/howtoheroku.mp4](https://cdn.rawgit.com/SaraVieira/graphql-workshop/13d73f3b/howtoheroku.mp4)
- CodeSanbox Account is preferred so you can keep all the files in your account
- Javascript knowledge
- React knowledge

## Backend

- An overview of what GraphQL is
- The basics of a querying with GraphQL
- The basics of a mutations in GraphQL
- Connect to a Postgres DB
- Using context
- Make multiple requests in one Query

## Frontend

- Making simple queries with React and React-Apollo
- Making advanced queries with React and React-Apollo
- Creating our own query component
- Mutations with React Apollo
- Updating the UI automatically after a mutation
- Using pagination
- Questions

This workshop is free. Tickets would be distributed through a diversity ticket program. Ticket will include a free ticket to the actual conference. [Apply for a diversity ticket](https://diversitytickets.org/events/184).
`,
  keywords: [Keyword.GRAPHQL, Keyword.REACT, Keyword.APOLLO, Keyword.PRISMA],
  location: paasitorni,
};

export default workshop;
