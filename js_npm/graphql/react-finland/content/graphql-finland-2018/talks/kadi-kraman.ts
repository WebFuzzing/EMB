import { Session, SessionType } from "../../../server/schema/Session";
import Keyword from "../../../server/schema/keywords";
import speaker from "../../people/kadi-kraman";

const talk: Session = {
  people: [speaker],
  title: "Going offline first with GraphQL",
  description: `We live in a world that is online 24/7. Even so, we are reasonably lenient on the web when we're not connected to the internet. However, for mobile apps, not working in flakey connectivity zones is borderline unacceptable. Users expect your app to work offline (though they don't phrase it like that). They really don't care that a network request has failed or is taking a long time. And thankfully, we developers can make it happen.

This talk is about how tooling in GraphQL makes it reasonably straightforward to get your React Native app working offline.`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [Keyword.GRAPHQL],
  urls: {
    web: "",
    slides: "http://slides.graphql-finland.fi/2018/03-kadi-kraman.pdf",
    video: "https://www.youtube.com/watch?v=i4Cfq7hQiws",
  },
};

export default talk;
