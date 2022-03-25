import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/ken-wheeler";

const talk: Session = {
  people: [speaker],
  title: "How React changed everything",
  description: `
This talk will examine how React changed the front end landscape as we know it. We'll start by recounting the pre-React landscape, including prior art. Next, we'll venture into the introduction of React, and its reception and growth.

But most importantly, we will take a look at the core idea of React, and why it transcends language or rendering target and posit on what that means going forward, including what React might look like years from now.
  `,
  type: SessionType.KEYNOTE,
  keywords: speaker.keywords,
  urls: {
    web: "https://reactfinland.surge.sh/",
  },
};

export default talk;
