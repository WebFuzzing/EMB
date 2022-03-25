import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/carolyn-stransky";

const talk: Session = {
  people: [speaker],
  title: '"Intuitive" Tooling',
  description: `
  One of the nuances of working in frontend development is constantly being told that [insert whatever tooling, frameworks or technologies here] are "super simple" or "easy to pickup". While on the surface this seems great, the reality is that many of these technologies are riddled with syntax quirks, incomplete documentation and a high barrier to entry. 

  In this talk, we'll discuss why truly intuitive tooling doesn't exist, go over some common misconceptions in the React community and learn more inclusive ways to talk about emerging frontend technologies.
  `,
  type: SessionType.TALK,
  keywords: [],
  urls: {
    slides: "https://slides.react-finland.fi/2019/carolyn-stransky.pdf",
    video: "https://www.youtube.com/watch?v=nBy8y39Pvp4",
  },
};

export default talk;
