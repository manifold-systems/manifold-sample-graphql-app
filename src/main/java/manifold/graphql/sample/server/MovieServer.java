package manifold.graphql.sample.server;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import manifold.ext.DataBindings;
import manifold.graphql.request.GqlRequestBody;

import java.util.*;

import static manifold.api.json.Json.fromJson;
import static spark.Spark.port;
import static spark.Spark.post;

/**
 * A simple GraphQL Server using SparkJava and Manifold.
 * <p/>
 * See the {@code MovieClient} class to see and experiment with Manifold GraphQL type-safe queries.
 */
public class MovieServer {
  public static void main(String[] args) {
    port(4567);
    GraphQL graphQL = Setup.init();

    //
    // Handle POST request for the `movies.graphql` schema
    //
    post("/graphql", (req, res) -> {
      GqlRequestBody request = (GqlRequestBody) fromJson(req.body());
      ExecutionInput exec = ExecutionInput.newExecutionInput()
        .query(request.getQuery())
        .variables((DataBindings)request.getVariables())
        .build();
      ExecutionResult execute = graphQL.execute(exec);

      DataBindings result = new DataBindings();
      List<GraphQLError> errors = execute.getErrors();
      if (!errors.isEmpty()) {
        result.put("errors", errors.stream().map(GraphQLError::toSpecification).toList());
      }
      Object data = execute.getData();
      if (data != null) {
        result.put("data", data);
      }
      res.type("application/json");
      return result.toJson();
    });
  }
}
