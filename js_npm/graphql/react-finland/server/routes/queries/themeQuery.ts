import themeFragment from "./fragments/themeFragment";

const themeQuery = `
query ThemeQuery($conferenceSeriesId: ID!) {
  theme(conferenceId: $conferenceSeriesId) {
    ...themeFragment
  }
}

${themeFragment}
`;

export default themeQuery;
