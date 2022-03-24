import { Field, ObjectType } from "type-graphql";
import { Contact } from "./Contact";
import { Interval } from "./Interval";
import Keyword from "./keywords";
import Level from "./Level";
import { Location } from "./Location";
import SessionType from "./SessionType";

@ObjectType()
export class SessionUrls {
  @Field((_) => String, { nullable: true })
  public web?: string;

  @Field((_) => String, { nullable: true })
  public slides?: string;

  @Field((_) => String, { nullable: true })
  public video?: string;

  @Field((_) => String, { nullable: true })
  public demo?: string;
}

@ObjectType()
export class Session {
  @Field((_) => SessionType)
  public type!: SessionType;

  @Field((_) => Level)
  public level?: Level;

  @Field((_) => String)
  public title!: string;

  // TODO: Eliminate and push title customization to frontend?
  @Field((_) => Boolean)
  public hasTitle?: boolean;

  @Field((_) => String, { nullable: true })
  public description?: string;

  @Field((_) => [String], { nullable: true })
  public keywords?: Keyword[];

  @Field((_) => Location, { nullable: true })
  public location?: Location;

  @Field((_) => [Contact], { nullable: true })
  public people?: Contact[];

  @Field((_) => SessionUrls, { nullable: true })
  public urls?: SessionUrls;

  @Field((_) => [Session], { nullable: true })
  public sessions?: Session[];

  // This refers to the closest interval. Note the recursion above.
  @Field((_) => Interval)
  public parent?: Interval;

  // Derived from a parent Schedule if it exists
  @Field((_) => String, { nullable: true })
  public day?: string;

  // Derived from a parent Interval if it exists
  @Field((_) => String, { nullable: true })
  public begin?: string;

  // Derived from a parent Interval if it exists
  @Field((_) => String, { nullable: true })
  public end?: string;
}

// Backwards-compatibility with content. TODO: refactor out
export { SessionType, Level };
