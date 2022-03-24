import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/toni-ristola";

const talk: Session = {
  people: [speaker],
  title: "React Finland App - Lessons learned",
  description:
    "The first app that we could publish to App Store and play store, and what we learned on the way.",
  type: SessionType.LIGHTNING_TALK,
  keywords: speaker.keywords,
};

export default talk;
