import themeFragment from "./fragments/themeFragment";

const themesQuery = `
query ThemesQuery {
  themes {
    ...themeFragment
  }
}

${themeFragment}
`;

export default themesQuery;
