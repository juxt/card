(ns juxt.home.card.graphql)

(def all-holidays
  "{
  allHolidays {
    startDate
    id
    endDate
    description
    user {
      id
    }
  }
}")

(def gql-test
  "query GetLearnWithJasonEpisodes($now: DateTime!) {
          allEpisode(limit: 10, sort: {date: ASC}, where: {date: {gte: $now}}) {
            date
            title
            guest {
              name
              twitter
            }
            description
          }
        }")
