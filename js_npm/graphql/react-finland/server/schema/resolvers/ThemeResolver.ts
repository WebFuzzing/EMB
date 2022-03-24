import { Arg, ID, Query, Resolver } from "type-graphql";
import { Theme } from "../Theme";
import allSeries from "./conferenceSeries";

@Resolver(_ => Theme)
class ThemeResolver {
  @Query(_ => [Theme])
  public themes() {
    return Object.values(allSeries).map(({ theme }) => theme);
  }

  @Query(_ => Theme)
  public theme(@Arg("conferenceId", _ => ID) conferenceId: string) {
    const series = allSeries[conferenceId];

    if (series) {
      return series.theme;
    }

    throw new Error("Conference id did not match series");
  }
}

export default ThemeResolver;
