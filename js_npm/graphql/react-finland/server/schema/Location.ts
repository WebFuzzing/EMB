import {
  Ctx,
  Field,
  FieldResolver,
  ObjectType,
  Resolver,
  Root,
} from "type-graphql";
import { IContext } from "./Context";
import { Country } from "./Country";
import { Image } from "./Image";
import { Social } from "./Social";

@ObjectType()
export class Location {
  @Field((_) => String, { nullable: true })
  public name?: string;

  @Field((_) => String, { nullable: true })
  public about?: string;

  @Field((_) => Image, { nullable: true })
  public image?: Image;

  @Field((_) => Social, { nullable: true })
  public social?: Social;

  @Field((_) => Country, { nullable: true })
  public country?: Country;

  @Field((_) => String, { nullable: true })
  public city?: string;

  @Field((_) => String, { nullable: true })
  public address?: string;
}

@Resolver((_) => Location)
export class LocationResolver {
  @FieldResolver((_) => Image)
  public image(@Root() location: Location, @Ctx() ctx: IContext) {
    return {
      url: location.image ? `${ctx.mediaUrl}/${location.image.url}` : "",
    };
  }
}
