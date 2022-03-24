import Keyword from "../../../server/schema/keywords";
import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/artem-zakharchenko";

const talk: Session = {
  people: [speaker],
  title: "Creating layouts that last",
  description: `What if we could wield layout composition as an actual React component? Spoiler: that would solve the #1 issue that prevents our layouts from being truly reusable.`,
  type: SessionType.TALK,
  keywords: [Keyword.CSS_GRID, Keyword.LAYOUT, Keyword.REACT],
  urls: {
    video: "https://www.youtube.com/watch?v=_HrXUB97xQs",
  },
};

export default talk;
