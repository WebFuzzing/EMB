import { Arg, ID, Query, Resolver } from "type-graphql";
import { getConference } from "../Conference";
import { Schedule } from "../Schedule";

@Resolver(_ => Schedule)
class ScheduleResolver {
  @Query(_ => Schedule)
  public schedule(
    @Arg("conferenceId", _ => ID) conferenceId: string,
    @Arg("day") day: string
  ) {
    return getSchedule(conferenceId, day);
  }
}
function getSchedule(id: string, day: string) {
  const conference = getConference(id);
  const schedule = conference.schedules.find(c => c.day === day);

  if (schedule) {
    return schedule;
  } else {
    throw new Error("Invalid date");
  }
}

export default ScheduleResolver;
