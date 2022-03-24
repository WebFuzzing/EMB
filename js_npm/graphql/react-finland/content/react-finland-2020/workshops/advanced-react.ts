import { Session, SessionType } from "../../../server/schema/Session";
import { internet } from "../../locations";
import { nikGraf } from "../../people";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [nikGraf],
  title: "Advanced React – Suspense, Time Slicing, Hooks and more …",
  description: `React has changed quite a lot in the past couple months: Hooks, Suspense, Error Boundaries and in addition to that Time Slicing is around the corner.

In this workshop we go through several concepts usually don't need every day. The examples will include challenges that include the following topics:

- useRef to persist values for the full component lifetime
- useLayoutEffect to manipulate the DOM before the browser paints
- useCallback to memoize functions across multiple renders
- Suspense and React.lazy for code splitting
- Suspense for data fetching
… and more

The workshop is targeting intermediate and experienced React developers, that are looking to not only learn the mentioned topics from a theoretical standpoint, but also try them out in practice.

The workshop is split into several modules. Each of them starts with a 20 min presentation, 30-50 min coding session and is followed by a 10 min retrospective.

The workshop takes place online 26.05 09:00-16:00 (Finnish time).`,
  keywords: [],
  location: internet,
};

export default workshop;
