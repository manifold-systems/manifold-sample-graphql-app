# manifold-sample-graphql-app

A sample GraphQL application using [Manifold GraphQL](http://manifold.systems/docs.html#graphql) and
[SparkJava](http://sparkjava.com/).

Notable:
* Demonstrates **Schema-first** and **single source of truth** solely with GraphQL schema files. **No code gen step!**, no POJOs, no annotations.
* A **GraphQL server** with SparkJava is a natural fit with Manifold's GraphQL support as defined in `MovieServer`
* **GraphQL client** code is made simple as illustrated in the `MovieClient` class
* **Rich development experience**. With Manifold's IntelliJ IDEA plugin you can rapidly build queries and mutations with
GraphQL. Quickly navigate between GraphQL schema files and usages in your code, refactor, find usages, code completion,
incremental compilation, etc. All without the burden of a code generation step or maintaining POJOs -- your GraphQL
schema files *are* your API!

## Usage

### IntelliJ IDEA
Manifold is best experienced in [IntelliJ IDEA](https://www.jetbrains.com/idea/download/).
* Install the Manifold IntelliJ plugin directly from IntelliJ IDEA:

   <kbd>Settings</kbd> ➜ <kbd>Plugins</kbd> ➜ <kbd>Marketplace</kbd> ➜ search: `Manifold`

* Close and relaunch IDEA
* Open this project: `manifold-sample-graphql-app`
* Be sure to setup an SDK for <b>Java 11</b>:

  <kbd>Project Structure</kbd> ➜ <kbd>SDKs</kbd> ➜ <kbd>+</kbd> ➜ <kbd>JDK</kbd>
* Or change the `pom.xml` file to use a JDK of your choosing, Manifold fully supports Java 8 - 12

>**Note:** Don't forget to install the [JS GraphQL](https://plugins.jetbrains.com/plugin/8097-js-graphql) plugin
for superb GraphQL file editing support in your project. 

### Running the `MovieServer`
* Run the `MovieServer` class directly with Java
* _or_ load this project in IntelliJ and run the `MovieServer` class

### Running the `MovieClient`
* The `MovieClient` is a simple client to illustrate basic query building and execution.