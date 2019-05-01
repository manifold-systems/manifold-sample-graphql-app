package graphql;

import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import manifold.api.json.IJsonBindingsBacked;
import manifold.api.json.Json;
import manifold.graphql.request.Executor;
import manifold.graphql.sample.schema.movies;
import manifold.graphql.sample.schema.queries;
import manifold.graphql.sample.server.MovieServer;
import manifold.util.JsonUtil;
import manifold.util.ReflectUtil;
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
public class MovieServerTest implements SparkApplication {
  @org.junit.ClassRule
  public static SparkServer<MovieServerTest> testServer = new SparkServer<>(MovieServerTest.class, 4567);

  public void init() {
    MovieServer.main(new String[0]);
  }

  public void destroy() {
    Spark.stop();
  }

  @Test
  public void testMovieQueryNonEmpty() {
    MovieQuery movieQuery = MovieQuery.builder().withGenre(Romance).build();
    MovieQuery.Result result = runQuery(movieQuery, MovieQuery.class);
    List<MovieQuery.Result.movies> movies = result.getMovies();
    assertEquals(1, movies.size());
    MovieQuery.Result.movies movie = movies.get(0);
    assertTrue(movie.getGenre().contains(Romance));
    assertEquals("The Getaway", movie.getTitle());
    assertEquals("Steve McQueen", movie.getCast().get(0).getActor().getName());
  }

  @Test
  public void testMovieQueryEmpty() {
    MovieQuery movieQuery = MovieQuery.builder().withGenre(Horror).build();
    MovieQuery.Result result = runQuery(movieQuery, MovieQuery.class);
    List<MovieQuery.Result.movies> movies = result.getMovies();
    assertTrue(movies.isEmpty());
  }

  @Test
  public void testActorQuery() {
    ActorQuery actorQuery = ActorQuery.builder("Le Mans").withGenre(Action).build();
    ActorQuery.Result result = runQuery(actorQuery, ActorQuery.class);
    var actors = result.getActors();
    assertEquals(1, actors.size());
    var actor = actors.get(0);
    assertEquals("Steve McQueen", actor.getName());
  }

  /**
   * For testing since we are not using HTTP to make direct requests, we use reflection to dig out the request body
   * to make the call to {@code testServer.post()}.
   */
  private <Q extends IJsonBindingsBacked, R> R runQuery(Q query, Class<Q> iface) {
    Class proxyClass = Arrays.stream(iface.getDeclaredClasses())
      .filter(c -> c.getSimpleName().equals("ProxyFactory"))
      .findFirst().orElseThrow();
    //noinspection unchecked
    Q proxy = (Q) ReflectUtil.method(proxyClass, "proxy", Map.class, Class.class)
      .invoke(ReflectUtil.constructor(proxyClass).newInstance(), query, iface);
    Executor queryExec = (Executor) ReflectUtil.method(proxy, "request", String.class).invoke("");
    PostMethod post = testServer.post("/graphql",
      JsonUtil.toJson(ReflectUtil.field(queryExec, "_reqArgs").get()), false);
    HttpResponse response = testServer.execute(post);
    Bindings bindings = (Bindings) Json.fromJson(new String(response.body()));
    //noinspection unchecked
    return (R) bindings.get("data");
  }
}