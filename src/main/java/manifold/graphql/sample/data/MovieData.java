package manifold.graphql.sample.data;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static manifold.graphql.sample.schema.movies.*;
import static manifold.graphql.sample.schema.movies.Genre.*;
import static manifold.graphql.sample.schema.movies.Type.Main;
import static manifold.graphql.sample.schema.movies.Type.Supporting;

/**
 * Simple in-memory sample data for the {@code movies.sample} schema
 */
//@SuppressWarnings("Convert2MethodRef")
public class MovieData {
  private static MovieData INSTANCE = null;

  private static int _ID = 0;

  private final Map<String, Person> _persons;
  private final Map<String, Animal> _animals;
  private final Map<String, Role> _roles;
  private final Map<String, Movie> _movies;
  private final Map<String, Review> _reviews;

  public static MovieData instance() {
    return INSTANCE == null ? INSTANCE = new MovieData() : INSTANCE;
  }

  private MovieData() {
    Person STEVE_MCQUEEN = Person.builder(id(), "Steve McQueen", date(1930, 3, 24))
      .withHeight(1.77)
      .withNationality("American")
      .build();
    Person SLIM_PICKENS = Person.builder(id(), "Slim Pickens", date(1919, 6, 29))
      .withHeight(1.91)
      .withNationality("American")
      .build();
    Person JAMES_GARNER = Person.builder(id(), "James Garner", date(1928, 4, 7))
      .withHeight(1.87)
      .withNationality("American")
      .build();
    _persons = Stream.of(STEVE_MCQUEEN, SLIM_PICKENS, JAMES_GARNER).toMap(person -> person.getId());

    Animal TRIGGER = Animal.builder(id(), "Trigger")
      .withKind("Horse")
      .withNationality("American")
      .build();
    _animals = Stream.of(TRIGGER).toMap(animal -> animal.getId());

    Role MICHAEL_DELANEY = Role.builder(id(), STEVE_MCQUEEN, "Michael Delaney", Main)
      .build();
    Role HILTS = Role.builder(id(), STEVE_MCQUEEN, "Hilts 'The Cooler King'", Main)
      .build();
    Role DOC_MCCOY = Role.builder(id(), STEVE_MCQUEEN, "Doc McCoy", Main)
      .build();
    Role COWBOY = Role.builder(id(), SLIM_PICKENS, "Cowboy", Supporting)
      .build();
    Role HENDLY = Role.builder(id(), JAMES_GARNER, "Hendly 'The Scrounger'", Supporting)
      .build();
    Role COMANCHE = Role.builder(id(), TRIGGER, "Comanche", Main)
      .build();
    Role ACE = Role.builder(id(), SLIM_PICKENS, "Ace", Type.Flat)
      .build();
    _roles = Stream.of(MICHAEL_DELANEY, HILTS, DOC_MCCOY, COWBOY, HENDLY, COMANCHE, ACE).toMap(role -> role.getId());

    Movie LE_MANS = Movie.builder(id(), "Le Mans", list(Action), date(1971, 6, 3), list(MICHAEL_DELANEY))
      .withStarring(STEVE_MCQUEEN)
      .build();
    Movie THE_GREAT_ESCAPE = Movie.builder(id(), "The Great Escape", list(Action, Drama), date(1963, 7, 4),
      list(HILTS, HENDLY))
      .withStarring(STEVE_MCQUEEN)
      .build();
    Movie THE_GETAWAY = Movie.builder(id(), "The Getaway", list(Action, Drama, Romance), date(1972, 12, 6),
      list(DOC_MCCOY, COWBOY))
      .withStarring(STEVE_MCQUEEN)
      .build();
    Movie TONKA = Movie.builder(id(), "Tonka", list(Drama, Western), date(1958, 12, 25),
      list(COMANCHE, ACE))
      .withStarring(TRIGGER)
      .build();
    _movies = Stream.of(LE_MANS, THE_GREAT_ESCAPE, THE_GETAWAY, TONKA).toMap(movie -> movie.getId(), e -> e);

    _reviews = new LinkedHashMap<>();
  }

  public Review createReview(String movieId, ReviewInput reviewInput) {
    Movie movie = _movies.get(movieId);
    Review review = Review.builder(id(), movie, reviewInput.getStars())
      .withComment(reviewInput.getComment()).build();
    _reviews.put(review.getId(), review);
    return review;
  }

  public Map<String, Person> getPersons() {
    return _persons;
  }

  public Map<String, Animal> getAnimals() {
    return _animals;
  }

  public Map<String, Role> getRoles() {
    return _roles;
  }

  public Map<String, Movie> getMovies() {
    return _movies;
  }

  public Map<String, Actor> getActors() {
    HashMap<String, Actor> map = new HashMap<>(getPersons());
    map.putAll(getAnimals());
    return map;
  }

  public Map<String, Review> getReviews() {
    return _reviews;
  }

  private static String id() {
    return String.valueOf(++_ID);
  }

  private static LocalDate date(int year, int month, int day) {
    return LocalDate.of(year, month, day);
  }

  @SafeVarargs
  private static <E> List<E> list(E... e) {
    return Arrays.asList(e);
  }
}
