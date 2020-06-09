package manifold.graphql.sample.server;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring.Builder;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import manifold.ext.rt.RuntimeMethods;
import manifold.graphql.rt.api.GqlScalars;
import manifold.graphql.sample.data.MovieData;
import manifold.graphql.sample.schema.movies;
import manifold.json.rt.api.DataBindings;
import manifold.json.rt.api.IJsonBindingsBacked;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static manifold.graphql.sample.schema.movies.*;

/**
 * Initialize GraphQL runtime wiring.  Create simple {@link DataFetcher}s for the {@link movies} schema.
 */
public class Setup {
  public static GraphQL init() {
    InputStream stream = MovieServer.class.getResource("/manifold/graphql/sample/schema/movies.graphql").openStream();
    TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(new InputStreamReader(stream));
    Builder runtimeWiringBuilder = newRuntimeWiring()
      .type(QueryRoot.class.getSimpleName(),
        builder -> builder
          .dataFetcher("movies", makeFieldMatchingDataFetcherList((Collection) MovieData.instance().getMovies().values()))
          .dataFetcher("actors", makeMappedFieldMatchingDataFetcherList((Collection) MovieData.instance().getMovies().values(),
            e -> ((Movie) e).getCast().stream().map(c -> c.getActor().getBindings()).toSet()))
          .dataFetcher("movie", makeFieldMatchingDataFetcherSingle((Collection) MovieData.instance().getMovies().values()))
          .dataFetcher("role", makeFieldMatchingDataFetcherSingle((Collection) MovieData.instance().getRoles().values()))
          .dataFetcher("person", makeFieldMatchingDataFetcherSingle((Collection) MovieData.instance().getPersons().values()))
          .dataFetcher("persons", makeFieldMatchingDataFetcherList((Collection) MovieData.instance().getPersons().values()))
          .dataFetcher("animal", makeFieldMatchingDataFetcherSingle((Collection) MovieData.instance().getAnimals().values()))
          .dataFetcher("review", makeFieldMatchingDataFetcherSingle((Collection) MovieData.instance().getReviews().values())))
      .type(MutationRoot.class.getSimpleName(),
        builder -> builder
          .dataFetcher("createReview", makeCreateReviewFetcher()))
      .type(Actor.class.getSimpleName(),
        builder -> builder
          .typeResolver(env -> (GraphQLObjectType) (((Map) env.getObject()).containsKey("height")
            ? env.getSchema().getType(Person.class.getSimpleName())
            : env.getSchema().getType(Animal.class.getSimpleName()))))
      .type("CastMember",
        builder -> builder
          .typeResolver(env -> (GraphQLObjectType) (((Map) env.getObject()).containsKey("height")
            ? env.getSchema().getType(Person.class.getSimpleName())
            : env.getSchema().getType(Animal.class.getSimpleName()))));
    GqlScalars.transformFormatTypeResolvers().forEach(runtimeWiringBuilder::scalar);
    graphql.schema.idl.RuntimeWiring runtimeWiring = runtimeWiringBuilder.build();
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

    return GraphQL.newGraphQL(graphQLSchema).build();
  }

  private static DataFetcher<List<DataBindings>> makeFieldMatchingDataFetcherList(Collection<IJsonBindingsBacked> list) {
    return env -> list.stream()
      .filter(item -> env.getArguments().entrySet().stream()
        .allMatch(arg -> arg.getValue() == null || isFieldMatch(item.getBindings(), arg.getKey(), arg.getValue())))
      .map(IJsonBindingsBacked::getBindings)
      .collect(Collectors.toList());
  }

  private static DataFetcher<List<DataBindings>> makeMappedFieldMatchingDataFetcherList(
          Collection<IJsonBindingsBacked> list,
          Function<DataBindings, Set<? extends DataBindings>> mapper) {
    return env -> list.stream()
      .filter(item -> env.getArguments().entrySet().stream()
        .allMatch(arg -> arg.getValue() == null || isFieldMatch(item.getBindings(), arg.getKey(), arg.getValue())))
            .map(IJsonBindingsBacked::getBindings)
      .map(mapper::apply)
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }

  private static DataFetcher<DataBindings> makeFieldMatchingDataFetcherSingle(Collection<IJsonBindingsBacked> list) {
    return env -> list.stream()
      .filter(item -> env.getArguments().entrySet().stream()
        .allMatch(arg -> arg.getValue() == null || isFieldMatch(item.getBindings(), arg.getKey(), arg.getValue())))
      .map(IJsonBindingsBacked::getBindings)
      .findFirst().orElse(null);
  }

  private static DataFetcher<DataBindings> makeCreateReviewFetcher() {
    return env -> {
      Review review = MovieData.instance()
        .createReview(env.getArgument("movieId"), env.getArgument("review"));
      return review.getBindings();
    };
  }

  private static boolean isFieldMatch(DataBindings bindings, String arg, Object value) {
    Object actualValue = bindings.get(arg);
    if (value == null) {
      return actualValue == null;
    }
    if (value.getClass().isInstance(actualValue)) {
      return Objects.equals(actualValue, value);
    }
    if (actualValue instanceof List) {
      // loose matching for lists
      return ((List) actualValue).contains(value);
    }
    // Use Scalar coercion
    actualValue = RuntimeMethods.coerce(actualValue, value.getClass());
    return Objects.equals(actualValue, value);
  }
}
