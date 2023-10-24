package scalix

import scala.io.Source
import org.json4s.*
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods.*
import java.io.{File, PrintWriter, FileReader}

object OldScalix extends App {

  implicit val formats: Formats = DefaultFormats
  val api_key = "0e18e9be35e51aac5589fd6e348d461e"
  // Initialize cache maps
  val actorIdCache = Map.empty[(String, String), Int]
  val directorCache = Map.empty[Int, Option[(Int, String)]]

  // Use the methods with caching
  val result = collaboration("Monica Bellucci", "Daniel Craig", actorIdCache, directorCache)

  private def buildURL(route: String, query: String): String = {
    s"https://api.themoviedb.org/3/$route?api_key=$api_key&query=$query"
  }

  def findActorId(name: String, surname: String, cache: Map[(String, String), Int]): Option[Int] = {
    val query = buildURL("search/person", s"${name}%20${surname}")

    cache.get((name, surname)) match {
      case Some(actorId) =>
        Some(actorId)
      case None =>
        val cacheFile = new File(s"data/actor${name}_${surname}.json")
        if (cacheFile.exists()) {
          val contents = Source.fromFile(cacheFile).mkString
          val json = parse(contents)
          val res = (json \ "results") \ "id"
          val actorId = res.extract[List[Int]].head
          cache + ((name, surname) -> actorId)
          Some(actorId)
        } else {
          val contents = Source.fromURL(query).mkString
          val json = parse(contents)
          val res = (json \ "results") \ "id"
          val actorId = res.extract[List[Int]].head

          val out = new PrintWriter(cacheFile)
          out.print(contents)
          out.close()

          Some(actorId)
        }
    }
  }


  def findActorMovies(actorId: Int): Set[(Int, String)] =
    val query = buildURL(s"person/$actorId/movie_credits", "")
    val contents = Source.fromURL(query)
    val result = contents.mkString
    val json = parse(result)
    val movieIds = (json \ "cast" \ ("id")).extract[List[Int]]
    val movieTitle = (json \ "cast" \ "title").extract[List[String]]
    movieIds.map((id) => (id, movieTitle(movieIds.indexOf(id)))).toSet

  def findMovieDirector(movieId: Int, cache: Map[Int, Option[(Int, String)]]): Option[(Int, String)] = {
    cache.get(movieId) match {
      case Some(directorOption) =>
        directorOption
      case None =>
        val cacheFile = new File(s"data/movie$movieId.json")
        if (cacheFile.exists()) {
          val contents = Source.fromFile(cacheFile).mkString
          val json = parse(contents)
          val crew = (json \ "crew").extract[List[JObject]]
          val director = crew.find(obj => (obj \ "job").extract[String] == "Director")
          val directorOption = director.map(obj =>
            ((obj \ "id").extract[Int], (obj \ "name").extract[String])
          )
          cache + (movieId -> directorOption)
          directorOption
        } else {
          val query = buildURL(s"movie/$movieId/credits", "")
          val contents = Source.fromURL(query).mkString
          val json = parse(contents)
          val crew = (json \ "crew").extract[List[JObject]]
          val director = crew.find(obj => (obj \ "job").extract[String] == "Director")
          val directorOption = director.map(obj =>
            ((obj \ "id").extract[Int], (obj \ "name").extract[String])
          )

          val out = new PrintWriter(cacheFile)
          out.print(contents)
          out.close()

          cache + (movieId -> directorOption)
          directorOption
        }
    }
  }



  def collaboration(actor1: String, actor2: String, actorIdCache: Map[(String, String), Int], directorCache: Map[Int, Option[(Int, String)]]): Set[(String, String)] = {
    val actorId1 = findActorId(actor1.split(" ").apply(0), actor1.split(" ").apply(1), actorIdCache)
    val actorId2 = findActorId(actor2.split(" ").apply(0), actor2.split(" ").apply(1), actorIdCache)

    if (actorId1.isDefined && actorId2.isDefined) {
      val actor1Movies = findActorMovies(actorId1.get)
      val actor2Movies = findActorMovies(actorId2.get)
      val commonMovies = actor1Movies.intersect(actor2Movies)
      val collaborations = commonMovies.flatMap {
        case (movieId, movieTitle) =>
          findMovieDirector(movieId, directorCache).map {
            case (directorId, directorName) =>
              (directorName, movieTitle)
          }
      }
      collaborations
    } else {
      Set.empty
    }
  }


}