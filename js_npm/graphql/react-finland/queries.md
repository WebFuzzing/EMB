```
query PageQuery($conferenceId: ID!) {
  conference(id: $conferenceId) {
    schedules {
      day
      description
      intervals {
        begin
        end
        title
        sessions {
          type
          title
          description

          people {
            name
            image {
              url
            }
          }
        }
      }
    }
  }
}
```

```
fragment SpeakerFragment on Contact {
  name
  about
  social {
    homepage
    github
    twitter
    linkedin
  }
  image {
    url
  }
}

query PageQuery($conferenceId: ID!) {
  conference(id: $conferenceId) {
    mcs {
      ...SpeakerFragment
    }
    workshops {
      speakers {
        ...SpeakerFragment
      }
    }
    speakers {
      ...SpeakerFragment
    }
  }
}
```

```
query PageQuery($conferenceId: ID!) {
  conference(id: $conferenceId) {
    workshops {
      speakers {
        name
        about
        image {
          url
        }
      }
      title
      description
      location {
        name
        address
        city
      }
    }
  }
}
```

```
query PageQuery($conferenceId: ID!) {
  conference(id: $conferenceId) {
    speakers {
      name
      about
      social {
        homepage
        github
        twitter
        linkedin
      }
      image {
        url
      }
      country {
        code
      }
    }
  }
}
```

```
query RootQuery($conferenceId: ID!) {
  conference(id: $conferenceId) {
    partners {
      ...SponsorFragment
    }
    goldSponsors {
      ...SponsorFragment
    }
    silverSponsors {
      ...SponsorFragment
    }
    bronzeSponsors {
      ...SponsorFragment
    }
  }
  contact(contactName: "Ellie Day", conferenceId: $conferenceId) {
    name
    about
    aboutShort
    image {
      url
    }
    talks {
      title
      description
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
```

```
query RootQuery($conferenceId: ID!) {
  contact(contactName: "Ellie Day", conferenceId: $conferenceId) {
    name
    about
    aboutShort
    image {
      url
    }
    talks {
      title
      description
    }
    workshops {
      title
      description
    }
  }
}
```
