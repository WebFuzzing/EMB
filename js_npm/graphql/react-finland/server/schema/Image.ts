import { InterpolationWithTheme } from "@emotion/core";
import { Field, ObjectType } from "type-graphql";

@ObjectType()
class Style {
  @Field((_) => String)
  public backgroundSize!: string;
}

@ObjectType()
export class Image {
  @Field((_) => String)
  public url!: string;

  @Field((_) => String, { nullable: true })
  public title?: string;

  @Field((_) => Style, { nullable: true })
  public style?: InterpolationWithTheme<any>;
}
