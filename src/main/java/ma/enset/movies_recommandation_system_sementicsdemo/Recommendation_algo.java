package ma.enset.movies_recommandation_system_sementicsdemo;

import javafx.fxml.Initializable;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Resource;


import java.net.URL;
import java.util.*;

public class Recommendation_algo implements Initializable{

    private static final String DBPEDIA_ENDPOINT = "http://dbpedia.org/sparql";
    private List<Movie> movies;
    private static final int TOP_K = 5;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Load the movie data from DBpedia
        loadMoviesFromDBpedia();

        // Calculate similarity scores using cosine similarity on TF-IDF vectors
        Map<Movie, Double> similarityScores = calculateSimilarityScores(movies);

        // Recommend similar movies to the input movie
        Movie inputMovie = new Movie("Inception");
        List<Movie> recommendations = recommendMovies(similarityScores, inputMovie, TOP_K);

        // Print the recommended movies
        System.out.println("Recommended movies for " + inputMovie.getTitle() + ":");
        for (Movie movie : movies) {
            System.out.println(movie.getTitle());
        }
    }
    private List<Movie> recommendMovies(Map<Movie, Double> similarityScores, Movie inputMovie, int topK) {
        return null;
    }

    private Map<Movie, Double> calculateSimilarityScores(List<Movie> movies) {
        Map<Movie, Map<String, Double>> movieFeatures = extractFeatures(movies);
        Map<Movie, Double> similarityScores = new HashMap<>();

        for (int i = 0; i < movies.size(); i++) {
            for (int j = i + 1; j < movies.size(); j++) {
                Movie movie1 = movies.get(i);
                Movie movie2 = movies.get(j);
                Map<String, Double> features1 = movieFeatures.get(movie1);
                Map<String, Double> features2 = movieFeatures.get(movie2);
                double similarityScore = cosineSimilarity(features1, features2);
                similarityScores.put(movie1, similarityScores.getOrDefault(movie1, 0.0) + similarityScore);
                similarityScores.put(movie2, similarityScores.getOrDefault(movie2, 0.0) + similarityScore);
            }
        }

        return similarityScores;

    }

    private double cosineSimilarity(Map<String, Double> features1, Map<String, Double> features2) {
        return 0.0;
    }

    private Map<Movie, Map<String, Double>> extractFeatures(List<Movie> movies) {
        Map<Movie, Map<String, Double>> movieFeatures = new HashMap<>();
        for (Movie movie : movies) {
            Map<String, Double> features = new HashMap<>();
            String plotSummary = movie.getTitle();
            List<String> tokens = tokenize(plotSummary);
            Map<String, Double> tfidf = calculateTFIDF(tokens, movies);
            features.putAll(tfidf);
            movieFeatures.put(movie, features);
        }

        return movieFeatures;

    }

    private Map<String, Double> calculateTFIDF(List<String> tokens, List<Movie> movies) {
        return null;
    }

    private List<String> tokenize(String plotSummary) {
        return null;
    }

    private List<Movie> loadMoviesFromDBpedia() {
        List<Movie> movies = new ArrayList<>();

        String sparqlQuery = "PREFIX dbprop: <http://dbpedia.org/ontology/>"+
                " PREFIX db: <http://dbpedia.org/resource/> "+
                "SELECT ?movie ?title ?abstract WHERE { " +
                "?movie db:Film " +
                "rdfs:label ?title " +
                "dbpedia-owl:abstract ?abstract . " +
                "FILTER (lang(?title) = en && lang(?abstract) = en) }";


        Query query = QueryFactory.create(sparqlQuery);
        QueryExecution queryExecution = QueryExecutionFactory.sparqlService(DBPEDIA_ENDPOINT, query);
        ResultSet results = queryExecution.execSelect();

        while (results.hasNext()) {
            QuerySolution solution = results.next();
            Resource resource = solution.getResource("movie");
            String title = solution.getLiteral("title").getString();
            String plotSummary = solution.getLiteral("abstract").getString();
            Movie movie = new Movie(title, plotSummary);
            movies.add(movie);
        }

        queryExecution.close();

        return movies;
    }

}