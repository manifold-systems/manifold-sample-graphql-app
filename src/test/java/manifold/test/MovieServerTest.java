package manifold.test;

import manifold.api.json.IJsonBindingsBacked;
import manifold.api.json.Json;
import manifold.graphql.request.Executor;
import manifold.graphql.sample.schema.movies;
import manifold.graphql.sample.schema.queries;
import manifold.graphql.sample.server.MovieServer;
import manifold.util.ReflectUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;
import spark.servlet.SparkApplication;

import javax.script.Bindings;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static manifold.graphql.sample.schema.movies.Genre.*;
import static manifold.graphql.sample.schema.queries.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test queries against MovieServer.
 * <p/>
 * Defines test {@link queries} via the {@code queries.graphql} schema file.  Uses GraphQL types and operations defined
 * in the {@link queries} and {@link movies} schema files directly as types to test the server.
 */
public class MovieServerTest {
  private static String ENDPOINT = "http://localhost:4567/graphql";

  @BeforeClass
  public static void init() {
    MovieServer.main(new String[0]);
  }

  @AfterClass
  public static void destroy() {
    Spark.stop();
  }

  @Test
  public void testMovieQueryNonEmpty() {
    MovieQuery movieQuery = MovieQuery.builder().withGenre(Romance).build();
    MovieQuery.Result result = movieQuery.request(ENDPOINT).post();
    var movies = result.getMovies();
    assertEquals(1, movies.size());
    MovieQuery.Result.movies movie = movies.get(0);
    assertTrue(movie.getGenre().contains(Romance));
    assertEquals("The Getaway", movie.getTitle());
    assertEquals("Steve McQueen", movie.getCast().get(0).getActor().getName());
  }

  @Test
  public void testMovieQueryEmpty() {
    MovieQuery movieQuery = MovieQuery.builder().withGenre(Horror).build();
    MovieQuery.Result result = movieQuery.request(ENDPOINT).post();
    var movies = result.getMovies();
    assertTrue(movies.isEmpty());
  }

  @Test
  public void testActorQuery() {
    ActorQuery actorQuery = ActorQuery.builder("Le Mans").withGenre(Action).build();
    ActorQuery.Result result = actorQuery.request(ENDPOINT).post();
    var actors = result.getActors();
    assertEquals(1, actors.size());
    var actor = actors.get(0);
    assertEquals("Steve McQueen", actor.getName());
  }

  //
  // Java 8 without `var`
  //
//  @Test
//  public void testMovieQueryNonEmpty() {
//    MovieQuery movieQuery = MovieQuery.builder().withGenre(Romance).build();
//    MovieQuery.Result result = movieQuery.request(ENDPOINT).post();
//    List<MovieQuery.Result.movies> movies = result.getMovies();
//    assertEquals(1, movies.size());
//    MovieQuery.Result.movies movie = movies.get(0);
//    assertTrue(movie.getGenre().contains(Romance));
//    assertEquals("The Getaway", movie.getTitle());
//    assertEquals("Steve McQueen", movie.getCast().get(0).getActor().getName());
//  }
//
//  @Test
//  public void testMovieQueryEmpty() {
//    MovieQuery movieQuery = MovieQuery.builder().withGenre(Horror).build();
//    MovieQuery.Result result = movieQuery.request(ENDPOINT).post();
//    List<MovieQuery.Result.movies> movies = result.getMovies();
//    assertTrue(movies.isEmpty());
//  }
//
//  @Test
//  public void testActorQuery() {
//    ActorQuery actorQuery = ActorQuery.builder("Le Mans").withGenre(Action).build();
//    ActorQuery.Result result = actorQuery.request(ENDPOINT).post();
//    List<ActorQuery.Result.actors> actors = result.getActors();
//    assertEquals(1, actors.size());
//    ActorQuery.Result.actors actor = actors.get(0);
//    assertEquals("Steve McQueen", actor.getName());
//  }
}