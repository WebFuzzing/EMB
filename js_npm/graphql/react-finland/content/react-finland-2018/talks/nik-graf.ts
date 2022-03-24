import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/nik-graf";

const talk: Session = {
  people: [speaker],
  title: "Get started with Reason",
  description:
    "We will kick off with the basics and then quickly go into how to leverage features like variant types and pattern matching to make impossible states impossible. After you gained some knowledge about the basics the course will dig even further into ReasonReact.",
  type: SessionType.TALK,
  keywords: [Keyword.REASON, Keyword.REACT],
  urls: {
    web: "https://speakerdeck.com/nikgraf/get-started-with-reason",
  },
};

export default talk;
