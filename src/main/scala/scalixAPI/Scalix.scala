package scalixAPI

import scala.io.Source
import org.json4s.*
import org.json4s.native.JsonMethods.*
import org.json4s.native.JsonMethods.parse
import scalixModel.{PaginatedResponse, Actor, ActorMoviesResponse, MovieDirectorResponse, FullName}


import java.net.URLEncoder

object Config:
  val api_key = "1423dc6a84212684621cdc8eba17af54"
object Scalix extends App{
  implicit val formats: Formats = DefaultFormats
  import Config.api_key
  private def buildURL(route: String, query: String): String = {
    s"https://api.themoviedb.org/3$route?api_key=$api_key&query=$query"
  }

  def findActorId(name: String, surname: String): Option[Int] = {
    val query = URLEncoder.encode(s"$name $surname", "UTF-8")
    val response = Source.fromURL(buildURL("/search/person", query))
    val paginatedResponse = parse(response.mkString).camelizeKeys.extract[PaginatedResponse[Actor]]
    paginatedResponse.results.headOption.map(_.id)
  }

  def findActorMovies(actorId: Option[Int]): Set[(Int, String)] = {
    val response = Source.fromURL(buildURL(s"/person/${actorId.get}/movie_credits", ""))
    val responseMovies = parse(response.mkString).camelizeKeys.extract[ActorMoviesResponse]
    responseMovies.cast.map(movieCredits => (movieCredits.id, movieCredits.originalTitle)).toSet
  }

  def findMovieDirector(movieId: Int): Option[(Int, String)] = {
    val response = Source.fromURL(buildURL(s"/movie/$movieId/credits", ""))
    val responseMovies = parse(response.mkString).camelizeKeys.extract[MovieDirectorResponse]
    responseMovies.crew.find(_.job == "Director").map(director => (director.id, director.name))
  }

  def collaboration(actor1: FullName, actor2: FullName): Set[(String, String)] = {
    val actorId1 = findActorId(actor1.name, actor1.surname)
    val actorId2 = findActorId(actor2.name, actor2.surname)
    if (actorId1.isDefined && actorId2.isDefined) {
      val actor1Movies = findActorMovies(actorId1)
      val actor2Movies = findActorMovies(actorId2)
      val commonMovies = actor1Movies.intersect(actor2Movies)
      val collaborations = commonMovies.flatMap {
        case (movieId, movieTitle) => findMovieDirector(movieId).map { case (directorId, directorName) =>
          (directorName, movieTitle)
        }
      }
      collaborations
    }
    else{
        Set.empty
      }
  }
}
