import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/rotem-mizrachi-meidan";

const talk: Session = {
  people: [speaker],
  title: "Detox: A year in. Building it, Testing with it",
  description: `
A year in, developing and using Detox in production taught us a lot. From designing its API to consuming it, testing real user scenarios to advanced mocking, we learned what makes sense when E2E testing an app and what doesn’t.

In this talk, we’ll discuss how Detox works and what makes it deterministic, cover some advanced use cases and methodologies, go over new features and tease the ones that are upcoming.
`,
  type: SessionType.TALK,
  keywords: speaker.keywords,
};

export default talk;
