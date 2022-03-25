import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/benjie-gillam";

const talk: Session = {
  people: [speaker],
  title: "Database-first GraphQL Development",
  description: `Learn how a database-centric approach to GraphQL API development can give your engineers more time to focus on the important parts of your application. Topics covered include authorization, adhering to GraphQL best practices, embracing the power of PostgreSQL, and avoiding common pitfalls.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.GRAPHQL],
  urls: {
    web: "",
    slides: "http://slides.graphql-finland.fi/2018/15-benjie-gillam.pdf",
    video: "https://www.youtube.com/watch?v=XDOrhTXd4pE",
  },
};

export default talk;
