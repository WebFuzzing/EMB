import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/kenneth-sutherland";

const talk: Session = {
  people: [speaker],
  title: "Notes to my younger self",
  description: `Target audience – Those that have been in the industry for a relatively short period of time, or haven’t been through half a dozen companies already.

  Want to hear about getting the most out of the company you are in, whether it’s a dead end job or perhaps the company are on the brink of sinking into oblivion?  Maybe you’ve hit the apparent jackpot and are working in the most fantastic start up – is it though?

  It's also about you! Listen to this talk and hear real life lessons on why Monday mornings are not painful, but are instead something that can be looked forward to.

  And of course the code, all those things I’ve done or not done over the years – would I do it all again the same? No!`,
  type: SessionType.LIGHTNING_TALK,
  keywords: [],
};

export default talk;
