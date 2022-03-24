import { Session, SessionType } from "../../../server/schema/Session";
import speaker from "../../people/eemeli-aro";

const talk: Session = {
  people: [speaker],
  title: "There's always a better way to handle localization",
  description:
    "From the very first prototype to a global site with content in multiple languages, the textual content of your app or project will be a series of compromises, some of which you won't even notice making. Localization is a ridiculously difficult problem in the general case, but in the specific you can get away with really simple solutions, especially if you understand the compromises you're making.",
  type: SessionType.LIGHTNING_TALK,
  keywords: speaker.keywords,
  urls: {
    web:
      "https://docs.google.com/presentation/d/e/2PACX-1vTnz2eogdCs9XG8tOsXEJw4CRBncm_ZJ1uGLoN3aqwXjIF5cL_P9KvNYRYpJfaUQNVW4JYUOjzroicp/pub?start=false&loop=false&delayms=3000&slide=id.p",
  },
};

export default talk;
