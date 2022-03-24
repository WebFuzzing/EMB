import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/ivan-goncharov";

const talk: Session = {
  people: [speaker],
  title: "Hidden gems of GraphQL query",
  description: `We all love GraphQL query syntax because it’s simple and easy to learn. At the same time, it has a few useful features that are typically overlooked. If you think that you already know everything about GraphQL queries this talk is for you. Warning: this talk will contain spoilers from GraphQL Specification.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.GRAPHQL, Keyword.GRAPHQLJS, Keyword.SPEC],
  urls: {
    web: "",
    slides: "http://slides.graphql-finland.fi/2018/10-ivan-goncharov.pdf",
    video: "https://www.youtube.com/watch?v=AeEFjFHehnM",
  },
};

export default talk;
