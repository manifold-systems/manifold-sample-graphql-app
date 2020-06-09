//!! Note this module-info.java file is unnecessary, but exists to demonstrate how to set one
//!! up if your project must be a JPMS multi-module application.
module manifold.sample.graphql.app {
    requires manifold.rt;
    requires manifold.ext.rt;
    requires manifold.json.rt;
    requires manifold.graphql.rt;
    requires manifold.collections;
    requires graphql.java;
    requires spark.core;
    requires jdk.unsupported;
}