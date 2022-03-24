import { Session, SessionType } from "../../../server/schema/Session";
import { internet } from "../../locations";
import { kenigboloMeyaStephen } from "../../people";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [kenigboloMeyaStephen],
  title: "From Legacy to React",
  description: `Changing applications from one technology to another can turn out to be quite challenging depending on the type of tech stack. If you have read about migrating large projects then you probably know how daunting it could get, especially moving from a direct DOM manipulation pattern to a Virtual DOM pattern.

After becoming Front End Engineering lead at BCaster I became responsible for handling the migration of a couple of JQuery legacy codebases into React. At first sight, it sure did seem to be straightforward, especially with proper test coverage, however about 20% into this migration we encountered certain issues which meant a total re-evaluation of several architectural decisions that had been made in regards to this migration.

Migrating a couple thousand lines of JQuery code required an incremental approach which ensured that a stable version of the codebase was always avaialble in production despite having significant changes to it. This meant that certain steps needed to be introduced in order to save us from the ordeal of a total rewrite (in some cases rewritting "might" a valid option).

The goal of this workshop is to give you a practical hands on step by step guide on how to approach incrementally migrating legacy JavaScript or JQuery application into a React app without needing to use the CLI initially and eventually moving the new codebase into a CLI project structure. The workshop requires participants to know basic javascript and DOM manipulation. It is suitable for both beginners and intermediates alike, however, the contents are primarily beginner focused.

The workshop is divided into two sessions and will take place online (Finnish time):

* 25.05 - 10:00-14:00
* 26.05 - 10:00-14:00`,
  keywords: [],
  location: internet,
};

export default workshop;
