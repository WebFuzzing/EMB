import { Field, ID, ObjectType } from "type-graphql";
import { Conference } from "./Conference";

@ObjectType()
export class Series {
  @Field((_) => ID)
  public id!: string;

  @Field((_) => String)
  public name!: string;

  @Field((_) => [Conference])
  public conferences!: [Conference];
}
