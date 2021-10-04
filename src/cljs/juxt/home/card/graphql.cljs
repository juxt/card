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
