import { Session, SessionType } from "../../../server/schema/Session";
import { elisaAppelsiini } from "../../locations";
import david from "../../people/david-khourshid";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [david],
  title: "Reactive State Machines and Statecharts",
  description: `Managing the many user interface states of an application easily becomes complicated. We'll discover how some historical and important computer science concepts – state machines and statecharts – and a functional + reactive approach can make it much easier to understand, visualize, implement, and automatically create tests for complex user interfaces and flows.`,
  keywords: david.keywords,
  location: elisaAppelsiini,
};

export default workshop;
