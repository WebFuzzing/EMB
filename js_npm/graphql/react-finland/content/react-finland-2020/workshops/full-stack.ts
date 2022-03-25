import { Session, SessionType } from "../../../server/schema/Session";
import { internet } from "../../locations";
import { naderDabit } from "../../people";

const workshop: Session = {
  type: SessionType.WORKSHOP,
  people: [naderDabit],
  title: "Full stack apps with React and AWS",
  description: `In this full day workshop you’ll learn how to build, end to end, a full stack application complete with serverless functions, authentication, a GraphQL API, and fine grained access control.

We’ll learn about how to set up react navigation with protected routes based on authorization rules, test and mock the APIs locally, and work with secure image storage and retrieval.

We’ll start by creating a blank React app and begin adding features using AWS and the AWS Amplify CLI. By the end of the course, you’ll have be ready to start building full stack applications that scale.`,
  keywords: [],
  location: internet,
};

export default workshop;
