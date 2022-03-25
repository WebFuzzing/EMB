import { Field, ObjectType } from "type-graphql";

@ObjectType()
export class Social {
  @Field((_) => String, { nullable: true })
  public homepage?: string;

  @Field((_) => String, { nullable: true })
  public twitter?: string;

  @Field((_) => String, { nullable: true })
  public github?: string;

  @Field((_) => String, { nullable: true })
  public facebook?: string;

  @Field((_) => String, { nullable: true })
  public googleMaps?: string;

  @Field((_) => String, { nullable: true })
  public medium?: string;

  @Field((_) => String, { nullable: true })
  public instagram?: string;

  @Field((_) => String, { nullable: true })
  public linkedin?: string;

  @Field((_) => String, { nullable: true })
  public youtube?: string;

  @Field((_) => String, { nullable: true })
  public vk?: string;

  @Field((_) => String, { nullable: true })
  public pinterest?: string;

  @Field((_) => String, { nullable: true })
  public vimeo?: string;

  @Field((_) => String, { nullable: true })
  public dribble?: string;

  @Field((_) => String, { nullable: true })
  public devto?: string;

  @Field((_) => String, { nullable: true })
  public twitch?: string;
}
