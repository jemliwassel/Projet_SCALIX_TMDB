package scalixModel

case class MovieActor(posterPath: String,
                      backdropPath: String,
                      adult: Boolean,
                      id: Int,
                      genreIds: List[Int],
                      originalLanguage: String,
                      overview: String,
                      originalTitle: String,
                      popularity: Double,
                      title: String,
                      video: Boolean,
                      voteAverage: Double,
                      voteCount: Int,
                      character: String,
                      creditId: String,
                     )
