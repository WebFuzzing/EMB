import freezingEdge2020 from "../content/freezing-edge-2020";
import graphQLFinland2018 from "../content/graphql-finland-2018";
import graphQLFinland2020 from "../content/graphql-finland-2020";
import halfstackVienna2019 from "../content/halfstack-vienna-2019";
import reactFinland2018 from "../content/react-finland-2018";
import reactFinland2019 from "../content/react-finland-2019";
import reactFinland2020 from "../content/react-finland-2020";
import reactFinland2021 from "../content/react-finland-2021";
import reactFinland2022 from "../content/react-finland-2022";
import reasonConf2019 from "../content/reason-conf-2019";
import techMovieNight from "../content/techmovienight";
import typeof2019 from "../content/typeof-2019";
import { Conference } from "./schema/Conference";

const conferences: { [id: string]: Conference } = {
  ["freezing-edge-2020"]: attachParents(freezingEdge2020),
  ["techmovienight"]: attachParents(techMovieNight),
  ["halfstack-vienna-2019"]: attachParents(halfstackVienna2019),
  ["reason-conf-2019"]: attachParents(reasonConf2019),
  ["react-finland-2018"]: attachParents(reactFinland2018),
  ["react-finland-2019"]: attachParents(reactFinland2019),
  ["react-finland-2020"]: attachParents(reactFinland2020),
  ["react-finland-2021"]: attachParents(reactFinland2021),
  ["react-finland-2022"]: attachParents(reactFinland2022),
  ["typeof-2019"]: attachParents(typeof2019),
  ["graphql-finland-2018"]: attachParents(graphQLFinland2018),
  ["graphql-finland-2020"]: attachParents(graphQLFinland2020),
};

function attachParents(conference: Conference): Conference {
  return {
    ...conference,
    schedules: conference.schedules.map((schedule) => ({
      ...schedule,
      intervals: schedule.intervals.map((interval) => ({
        ...interval,
        sessions: interval.sessions.map((session) => ({
          ...session,
          day: schedule.day,
          begin: interval.begin,
          end: interval.end,
        })),
      })),
    })),
  };
}

export default conferences;
