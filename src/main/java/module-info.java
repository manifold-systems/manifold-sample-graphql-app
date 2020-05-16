module manifold.sample.graphql.app {
    // Use the GraphQL manifold for type-safe access to GraphQL schema files, query execution, etc.
    requires manifold.graphql;
    // Use the Exceptions manifold extension to treat checked exceptions as unchecked
    requires manifold.exceptions;
    // Use the Collections extension library
    requires manifold.collections;
    // Use the String Templates library (string interpolation)
    requires manifold.strings;

    // Include transitive dependencies manually since manifold jars are "automatic" modules
    // (they don't define manifold-info.java files, thus no 'requires' to their dependencies)
    requires manifold;
    requires manifold.ext;
    requires manifold.json;
    requires graphql.java;
    requires spark.core;
    requires java.scripting;
    requires jdk.unsupported;
}