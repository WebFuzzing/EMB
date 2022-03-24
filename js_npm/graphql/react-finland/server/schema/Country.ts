import { Field, ObjectType } from "type-graphql";

@ObjectType()
export class Country {
  @Field((_) => String)
  public name?: string;

  @Field((_) => String)
  public code?: string;
}
