import { Session, SessionType } from "../../../server/schema/Session";
import { internet } from "../../locations";
import { kentCDodds } from "../../people";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [kentCDodds],
  title: "React Performance",
  description: `> React is fast, until it isn't

In this workshop, you'll learn everything you need to diagnose, profile, and fix performance problems in your React application using the Browser Performance Profiler, React DevTools Profiler, and proven React optimization techniques.

The tools we have for profiling React Applications are extremely powerful, but they can often be really difficult to grok. Sure, starting a profiling session is simple enough, but how do you go from all the information in the flamegraph to actionable steps to improve your application's performance? And once you know what parts of your app are causing performance issues, what then? How do you optimize your React components without making the whole application a complex mess?

Oh, and don't forget that you should profile your app in production mode to make sure you get accurate measurements. Do you know how to do that?

Hi, I'm Kent C. Dodds and in my latest workshop I'll teach you the ins and outs of optimizing React applications. I will take you on a deep dive browser profiling tools, and show you what you need to know to start using them to make your applications fast right away.

**When you are done you will:**

* Profile React components with the React DevTools profiler
* Profile JavaScript with the Chrome DevTools profiler
* Utilize built-in React features (like React.memo, useMemo, anduseCallback) to optimize React components
* Know how to apply better patterns for application state management to optimize components by default

> Note that in order to participate the workshop and get most out of it, you should be already familiar with React hooks.`,
  keywords: [],
  location: internet,
};

export default workshop;
