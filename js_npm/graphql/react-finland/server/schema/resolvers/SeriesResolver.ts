import { Arg, FieldResolver, ID, Query, Resolver, Root } from "type-graphql";
import conferences from "../../conferences";
import { Conference } from "../Conference";
import { Series } from "../Series";
import series from "./conferenceSeries";

@Resolver(_ => Series)
class SeriesResolver {
  @Query(_ => Series)
  public series(@Arg("id", _ => ID) id: string) {
    if (series[id]) {
      return series[id];
    } else {
      throw new Error("Unknown conference");
    }
  }

  @Query(_ => [Series])
  public allSeries() {
    return Object.keys(series).map(id => series[id]);
  }

  @FieldResolver(_ => [Conference])
  public conferences(@Root() series: Series) {
    return Object.values(conferences).filter(
      conference => series.name === conference.series
    );
  }
}

export default SeriesResolver;
