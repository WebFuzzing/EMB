import { Session, SessionType } from "../../../server/schema/Session";
import { valkoinenSali } from "../../locations";
import michel from "../../people/michel-weststrate";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [michel],
  title: "State Management",
  description: `In this workshop we will dive into the basics and more advanced usage patterns of Mobx-State-Tree (MST) in combination with React. MST is a tool that helps to model complex problem domains and combines the best features of the modern state technologies out there.

1. The state tree philosophy.
2. Modeling your data structures and state
3. Views & actions
4. Asynchronous processes
5. Composing trees and types
6. Connecting to GraphQL
7. Writing your own middleware`,
  keywords: michel.keywords,
  location: valkoinenSali,
};

export default workshop;
