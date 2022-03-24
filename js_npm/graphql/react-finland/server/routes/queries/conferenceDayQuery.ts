const conferenceDayQuery = `
query ConferenceDayQuery {
  allSeries {
    id
    name
    conferences {
      id
      name
      schedules {
        day
      }
    }
  }
}
`;

export default conferenceDayQuery;
