const sponsorQuery = `
query PageQuery($conferenceId: ID!) {
  conference(id: $conferenceId) {
    goldSponsors {
      ...SponsorFragment
    }
    silverSponsors {
      ...SponsorFragment
    }
    bronzeSponsors {
      ...SponsorFragment
    }
    platformSponsors {
      ...SponsorFragment
    }
    partners {
      ...SponsorFragment
    }
  }
}

fragment SponsorFragment on Contact {
  name
  social {
    homepage
  }
  about
  image {
    url
  }
}
`;

export default sponsorQuery;
