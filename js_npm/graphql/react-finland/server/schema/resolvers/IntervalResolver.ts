import { Arg, FieldResolver, ID, Query, Resolver, Root } from "type-graphql";
import { Interval } from "../Interval";
import { Session } from "../Session";
import { getConference } from "../Conference";

@Resolver((_) => Interval)
class IntervalResolver {
  @Query((_) => Interval)
  public interval(
    @Arg("conferenceId", (_) => ID) conferenceId: string,
    @Arg("intervalTitle", (_) => String) intervalTitle: string
  ) {
    const conference = getConference(conferenceId);
    let ret;

    // This picks up only the last match right now
    conference.schedules.forEach(({ intervals }) => {
      const found = intervals.find(({ title }) => title === intervalTitle);

      if (found) {
        ret = found;
      }
    });

    return ret;
  }

  @FieldResolver((_) => [Session])
  public sessions(@Root() interval: Interval) {
    return interval.sessions.map((session) => ({
      ...session,
      interval,
    }));
  }
}

export default IntervalResolver;
