package scalixAPI

import scala.io.Source
import org.json4s.*
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods.*

object OldScalix extends App {

  implicit val formats: Formats = DefaultFormats
  val api_key = "0e18e9be35e51aac5589fd6e348d461e"

  private def buildURL(route: String, query: String): String = {
    s"https://api.themoviedb.org/3/$route?api_key=$api_key&query=$query"
  }

  def findActorId(name: String, surname: String): Option[Int] =
    val query = buildURL("search/person", s"${name}%20${surname}")
    val contents = Source.fromURL(query)
    val result = contents.mkString
    val json = parse(result)
    val res = (json \ "results") \ "id"
    Some(res.extract[List[Int]].head);

  def findActorMovies(actorId: Int): Set[(Int, String)] =
    val query = buildURL(s"person/$actorId/movie_credits", "")
    val contents = Source.fromURL(query)
    val result = contents.mkString
    val json = parse(result)
    val movieIds = (json \ "cast" \ ("id")).extract[List[Int]]
    val movieTitle = (json \ "cast" \ "title").extract[List[String]]
    movieIds.map((id) => (id, movieTitle(movieIds.indexOf(id)))).toSet

  def findMovieDirector(movieId: Int): Option[(Int, String)] =
    val query = buildURL(s"movie/$movieId/credits", "")
    val contents = Source.fromURL(query)
    val result = contents.mkString
    val json = parse(result)
    val crew = (json \ "crew").extract[List[JObject]]
    val director = crew.filter(obj => (obj \ "job").extract[String] == "Director").last
    Some(((director \ "id").extract[Int], (director \ "name").extract[String]))

  def collaboration(actor1: String, actor2: String): Set[(String, String)] =
    val actorId1 = findActorId(actor1.split(" ").apply(0), actor1.split(" ").apply(1))
    val actorId2 = findActorId(actor2.split(" ").apply(0), actor2.split(" ").apply(1))
    if (actorId1.isDefined && actorId2.isDefined)
      val actor1Movies = findActorMovies(actorId1.get)
      val actor2Movies = findActorMovies(actorId2.get)
      val commonMovies = actor1Movies.intersect(actor2Movies)
      val collaborations = commonMovies.flatMap {
        case (movieId, movieTitle) => findMovieDirector(movieId).map { case (directorId, directorName) =>
          (directorName, movieTitle)
        }
      }
      collaborations
    else
      Set.empty
}