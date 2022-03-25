import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/olavi";

const talk: Session = {
  people: [speaker],
  title:
    "How we made is.fi, Finland’s largest website faster, cleaner, and more accessible",
  description: `Full rewrite from scratch is almost always a bad idea. In most cases, the end result is barely better than the legacy service that was supposed to be renewed. However, in some cases a full rewrite is the only sensible option. That was our case when we took a critical look at our architecture and the amount of technical debt at Ilta-Sanomat web project. The project had grown over the years and was in a state where creating new features was slow, the code was not very maintainable and the performance was not at a desired level.

  The decision was made, and we started rewriting the whole project from scratch, including the backend that provides the data. We rebuilt the site as a Server Side Rendered React application using Next.js while also renewing the design. For state management we only use React’s built-in tools, hooks and the Context API. A design system was also set up during the project in order to make sure the design and development will remain in sync in the future and the development faster and more enjoyable. Better performance and accessibility were the main goals of this renewal project. How did we achieve these goals despite the scale of the project and the amount of third party scripts and other requirements?`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [],
};

export default talk;
