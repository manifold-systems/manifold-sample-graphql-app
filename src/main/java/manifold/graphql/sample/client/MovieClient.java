package manifold.graphql.sample.client;

import manifold.graphql.sample.schema.queries;

import java.util.List;

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
  private static String ENDPOINT = "http://localhost:4567/graphql";

  public static void main(String[] args) {
    queryExample();
    mutationExample();
  }

  private static void queryExample() {
    var query = MovieQuery.builder().withGenre(Action).build();
    var result = query.request(ENDPOINT).post();
    var actionMovies = result.getMovies();
    for (var movie : actionMovies) {
      out.println(
        "Title: ${movie.getTitle()}\n" +
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
    out.println(
      "Review for: ${movie.getTitle()}\n" +
        "Stars: ${createdReview.getStars()}\n" +
        "Comment: ${createdReview.getComment()}\n"
    );
  }

  //
  // Java 8 (without `var`)
  //
//  private static void queryExample() {
//    MovieQuery query = MovieQuery.builder().withGenre(Action).build();
//    MovieQuery.Result result = query.request(ENDPOINT).post();
//    List<MovieQuery.Result.movies> actionMovies = result.getMovies();
//    for (MovieQuery.Result.movies movie : actionMovies) {
//      out.println(
//              "Title: ${movie.getTitle()}\n" +
//                      "Genre: ${movie.getGenre()}\n" +
//                      "Year: ${movie.getReleaseDate().getYear()}\n");
//    }
//  }
//
//  private static void mutationExample() {
//    // Find the movie to review ("Le Mans")
//    MovieQuery.Result.movies movie = MovieQuery.builder().withTitle("Le Mans").build()
//            .request(ENDPOINT).post().getMovies().first();
//    // Submit a review for the movie
//    ReviewInput review = ReviewInput.builder(5).withComment("Topnotch racing film.").build();
//    ReviewMutation mutation = ReviewMutation.builder(movie.getId(), review).build();
//    ReviewMutation.Result.createReview createdReview = (ReviewMutation.Result.createReview) mutation.request(ENDPOINT).post().getCreateReview();
//    out.println(
//            "Review for: ${movie.getTitle()}\n" +
//                    "Stars: ${createdReview.getStars()}\n" +
//                    "Comment: ${createdReview.getComment()}\n"
//    );
//  }
}
