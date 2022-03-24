import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/michel-weststrate";

const talk: Session = {
  people: [speaker],
  title: "Immer: Immutability made easy",
  description:
    "Immer is a tiny package that allows you to work with immutable data structures with unprecedented ease. It doesn't require learning new data structures or update APIs, but instead creates a temporarily shadow tree which can be modified using the standard JavaScript APIs. The shadow tree will be used to generate your next immutable state tree. Join this talk to see how to write your reducers in a much more readable way, with half the code and without requiring additional large libraries.",
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.IMMER],
  urls: {
    web: "http://immer.surge.sh/",
  },
};

export default talk;
