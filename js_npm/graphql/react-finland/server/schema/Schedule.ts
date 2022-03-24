import flatMap from "lodash/flatMap";
import uniq from "lodash/uniq";
import { Field, ObjectType } from "type-graphql";
import { Interval } from "./Interval";
import { Location } from "./Location";
import SessionType from "./SessionType";

@ObjectType()
export class Schedule {
  @Field((_) => String)
  public day!: string;

  @Field((_) => Location, { nullable: true })
  public location?: Location;

  @Field((_) => String, { nullable: true })
  public description?: string;

  @Field((_) => [Interval])
  public intervals!: Interval[];
}

export function resolveSessions(
  schedules: Schedule[],
  sessionTypes: SessionType[]
) {
  return uniq(
    flatMap(schedules, ({ intervals }) =>
      flatMap(intervals, ({ sessions }) =>
        flatMap(sessions, (session) =>
          [session].concat(session.sessions || [])
        ).filter(({ type }) => sessionTypes.includes(type))
      )
    )
  );
}
