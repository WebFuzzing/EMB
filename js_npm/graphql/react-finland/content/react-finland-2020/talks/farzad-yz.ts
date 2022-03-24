import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/farzad-yz";

const talk: Session = {
  people: [speaker],
  title: "Taming UI complexity with finite state machines",
  description: `Most UI components are challenging on web. I asked people on Twitter about their opinion on what are the most challenging and complex UI components they know.
  
In the answers, there were Carousels, Async selects, etc. I took Carousel as an example and reviewed the implementation of some of the most popular open source ones. They had some bugs due to complexity of configs.

I tried to model the complexity using Finite state machines. When I was done, it was super solid and those computation bugs were gone as well. Since FSM gives you the graph of all possible combinations, I generated all the permutations, rendered all the possible variations of the carousel and tested them visually using Cypress and Percy.

Using this approach, you get to develop complex UI components, Manage their complexity using FSM and get to test them using property based testing techniques and get all the possible outputs visualized without a need to test them in your UI at all!`,
  type: SessionType.TALK,
  keywords: [],
};

export default talk;
