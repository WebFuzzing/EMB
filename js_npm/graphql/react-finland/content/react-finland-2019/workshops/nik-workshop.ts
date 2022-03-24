import { Session, SessionType } from "../../../server/schema/Session";
import { paasitorni } from "../../locations";
import { nikGraf } from "../../people";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [nikGraf],
  title: "Advanced React – Suspense, Time Slicing, Hooks and more …",
  description: `React has changed quite a lot in the past couple months. New lifecycle methods were released, Suspense and Time Slicing is around the corner and we even got a new API: React Hooks.

In this workshop we build an application relying on new techniques. The examples will include challenges that include the following topics:

- Async React (Suspense, Time Slicing)
- Advanced React Hooks (useLayoutEffect, useReducer, …)
- React.lazy
- Refs
- Context

The workshop is targeting intermediate and experienced React developers, that are looking to not only learn the mentioned topics from a theoretical standpoint, but also try them out in practice.

The workshop is split into several modules. Each of them starts with a 20 min presentation, 30-50 min coding session and is followed by a 10 min retrospective.
`,
  keywords: nikGraf.keywords,
  location: paasitorni,
};

export default workshop;
