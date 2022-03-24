const speakerTalkQuery = `
query SpeakerTalkQuery($conferenceId: ID!, $contactName: String!) {
  contact(contactName: $contactName, conferenceId: $conferenceId) {
    name
    image {
      url
    }
    talks {
      title
    }
  }
}
`;

export default speakerTalkQuery;
