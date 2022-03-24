import { Session, SessionType } from "../../../server/schema/Session";
import { valkoinenSali } from "../../locations";
import gant from "../../people/gant-laborde";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [gant],
  title: "React Native",
  description: `In this beginner workshop you will learn what React Native (RN) consists of, how to write basic applications, how to use Storybook with RN, how to test RN, and finally how to kickstart your own project.`,
  keywords: gant.keywords,
  location: valkoinenSali,
};

export default workshop;
