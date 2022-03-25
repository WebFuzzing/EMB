import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/nader-dabit";

const talk: Session = {
  people: [speaker],
  title: "The Future of Real-time | Offline | Data",
  description: `Complexity, and the consistent attempts to reduce complexity, is at the core of the evolution of technology. As technology evolves, we then find harder problems to solve and are presented with new challenges. In the client space, we’ve seen innovation that has addressed how we deal with modern application concerns like real-time and offline data while at the same time GraphQL has continued to gain in market share.

  What happens when we take the advancements that GraphQL has introduced as a paradigm and combine them with a mental model that all data should be local and offline first, with eventual consistency to your database as a second thought? In this talk, I’ll talk about a data store paradigm that allows developers to work with a single, local database and source of truth, and the idea that you should not have to make more than one write action to have (eventual) consistency across the client and server.`,
  type: SessionType.TALK, // SessionType.LIGHTNING_TALK, SessionType.KEYNOTE, SessionType.WORKSHOP are possible too
  keywords: [],
};

export default talk;
