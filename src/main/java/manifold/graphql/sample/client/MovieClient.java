package manifold.graphql.sample.client;

import manifold.graphql.sample.schema.queries;

import static java.lang.System.out;
import static manifold.graphql.sample.schema.movies.Genre.*;
import static manifold.graphql.sample.schema.movies.*;
import static manifold.graphql.sample.schema.queries.*;

/**
 * This simple client demonstrates type-safe usage of the sample {@link queries} schema file {@code queries.graphql}
 * using Manifold.
 * <p/>
 * Remember to run the {@code MovieServer} before running this class :)
 */
public class MovieClient {
  private static final String ENDPOINT = "http://localhost:4567/graphql";

  public static void main(String[] args) {
    queryExample();
    mutationExample();
    localTypeSafeQueryExample();
  }

  private static void queryExample() {
    var query = MovieQuery.builder().withGenre(Action).build();
    var result = query.request(ENDPOINT).post();
    var actionMovies = result.getMovies();
    for (var movie : actionMovies) {
      out.println("Title: ${movie.getTitle()}\n" +
                  "Genre: ${movie.getGenre()}\n" +
                  "Year: ${movie.getReleaseDate().getYear()}\n");
    }
  }

  private static void mutationExample() {
    // Find the movie to review ("Le Mans")
    var movie = MovieQuery.builder().withTitle("Le Mans").build()
      .request(ENDPOINT).post().getMovies().first();
    // Submit a review for the movie
    var review = ReviewInput.builder(5).withComment("Topnotch racing film.").build();
    var mutation = ReviewMutation.builder(movie.getId(), review).build();
    var createdReview = mutation.request(ENDPOINT).post().getCreateReview();
    out.println("Review for: ${movie.getTitle()}\n" +
                "Stars: ${createdReview.getStars()}\n" +
                "Comment: ${createdReview.getComment()}\n");
  }

  // note, you can install the GraphQL plugin (https://plugins.jetbrains.com/plugin/8097-graphql)
  //  for rich display/editing of embedded graphql
  private static void localTypeSafeQueryExample()
  {
    /* [MyQuery.graphql/]
      query Movies($title: String, $genre: Genre, $releaseDate: Date) {
          movies(title: $title, genre: $genre, releaseDate: $releaseDate) {
              id
              title
              genre
              releaseDate
          }
      }
    */
    // notice MyQuery is an embedded GraphQL query that can be used directly and type-safely as if a resource file
    var actionMoviesQuery = MyQuery.Movies.builder()
            .withGenre(Action).build();
    var res = actionMoviesQuery.request(ENDPOINT).post();
    for (var movie : res.getMovies()) {
      out.println("Title: ${movie.getTitle()}\n" +
              "Genre: ${movie.getGenre()}\n" +
              "Year: ${movie.getReleaseDate().getYear()}\n");
    }
  }

}
