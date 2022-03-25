import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/shay-keinan";

const talk: Session = {
  people: [speaker],
  title: "ReactVR",
  description:
    "WebVR enables web developers to create frictionless, immersive experiences. We’ll explore the core concepts behind VR, see different demonstrations, learn how to get started with React VR and how to add new features from the Three.js library.",
  type: SessionType.TALK,
  keywords: speaker.keywords,
};

export default talk;
