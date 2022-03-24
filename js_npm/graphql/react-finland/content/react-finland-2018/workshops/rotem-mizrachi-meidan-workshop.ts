import { Session, SessionType } from "../../../server/schema/Session";
import { valkoinenSali } from "../../locations";
import rotem from "../../people/rotem-mizrachi-meidan";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [rotem],
  title: "Advanced E2E Testing with Detox",
  description: `In this workshop we will get to know the cool features hidden in Detox. By getting to know how Detox works and what makes it deterministic, you will be able write better and more stable tests.

You will either build a demo app to experiment with its features (basic and advanced), or work on your existing app’s test suite.
  
We will experiment with the following:

1. Running app against mock server
2. Mocking mechanisms: permissions, user notifications
3. Understanding synchronization issues
4. Debugging failed tests
5. Stabilizing E2E builds in CI`,
  keywords: rotem.keywords,
  location: valkoinenSali,
};

export default workshop;
