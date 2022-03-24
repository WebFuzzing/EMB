// TODO: Separate location query
const conferenceDaysQuery = `
query ConferenceDaysQuery($conferenceId: ID!) {
  conference(id: $conferenceId) {
    slogan
    schedules {
      day
    }
    locations {
      city
      country {
        name
      }
    }
  }
}
`;

export default conferenceDaysQuery;
