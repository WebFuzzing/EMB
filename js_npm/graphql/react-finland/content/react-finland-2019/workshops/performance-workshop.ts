import { Session, SessionType } from "../../../server/schema/Session";
import { paasitorni } from "../../locations";
import { saraVieira } from "../../people";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [saraVieira],
  title: "Advanced Performance Tuning for React Applications",
  description: `Getting performance right is hard, even when we have the luxury of starting our apps from scratch. It’s even harder when the ideal crashes headlong into reality, and we need to improve the performance of existing apps, as is so often the case. In this workshop, Sia will lead you through the process of:

- Assessing an existing React app
- Diagnosing performance problems
- Prioritizing the fixes that will make the biggest impact
- Implementing performance fixes

During this full-day workshop, you’ll learn advanced techniques for improving the performance of React apps, including:

- Lazy loading resources & components (including React.lazy and Suspense)
- Leveraging Service Workers for performance
- Seamlessly preloading and prefetching assets
- Automatically optimizing images and fonts
- Mitigating the performance impact of third-party scripts
- Code splitting and bundle optimization
- Using psychological tricks to improve perceived performance

By the end of the workshop, you’ll be able to diagnose performance problems and solve them with a combination of modern and time-tested performance techniques. You’ll also learn how to weigh the trade-offs of implementing fixes to ensure that both your apps and your teams perform well. The tools added to your toolbox will continue to serve you, your team, and your users for years to come.

**Prerequisites**: To attend this workshop, you must already have a working understanding of React and JavaScript in addition to HTML, CSS, and the command line. You must also have a basic understanding of Chrome DevTools, including inspecting an element and using the console. A basic understanding of webpack would also be helpful but is not required.

**Preparation**: Please come with a laptop ready for development. You must have Chrome, the [React DevTools extension](https://chrome.google.com/webstore/detail/react-developer-tools/fmkadmapgofadopljbjfkapdkoienihi?hl=en), and Node (v 8+) installed.

This workshop was prepared in partnership with Eli Fitch and Jason Lengstorf.

> Before coming to the workshop, make sure you set up [advanced-react-perf repository](https://github.com/jlengstorf/advanced-react-perf) and follow the instructions. Doing this will save time and help you get started faster. You could also [check out the workshop slides beforehand](https://siakaramalegos.github.io/react-perf-workshop/).
`,
  keywords: saraVieira.keywords,
  location: paasitorni,
};

export default workshop;
