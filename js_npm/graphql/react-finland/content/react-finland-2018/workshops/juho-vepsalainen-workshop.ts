import { Session, SessionType } from "../../../server/schema/Session";
import { elisaAppelsiini } from "../../locations";
import juho from "../../people/juho-vepsalainen";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [juho],
  title: "Webpack - The Good Parts",
  description: `In this high-level overview you'll learn how to configure Webpack. Even if you know it already, there might be some surprises in store as you gain insight to the tool you otherwise might miss.

The topics covered include:

* Fundamental ideas of webpack
* Development techniques
* Build techniques
* Asset management
* Bundle/code splitting
* Build analysis
* Optimizing the build`,
  keywords: juho.keywords,
  location: elisaAppelsiini,
};

export default workshop;
