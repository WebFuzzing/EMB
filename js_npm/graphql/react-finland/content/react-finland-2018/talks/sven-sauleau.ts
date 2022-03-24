import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/sven-sauleau";

const talk: Session = {
  people: [speaker],
  title: "Understanding the differences is accepting",
  description:
    "I want to show you that the unexpected JavaScript things you encountered are understandable by humans and formally specified.",
  type: SessionType.LIGHTNING_TALK,
  keywords: speaker.keywords,
  urls: {
    web: "http://understanding-the-differences-is-accepting.ralf.cc/",
  },
};

export default talk;
