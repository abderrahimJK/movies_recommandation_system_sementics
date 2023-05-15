package ma.enset.movies_recommandation_system_sementicsdemo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.util.Utils;
import org.apache.jena.util.FileManager;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import com.google.gson.JsonParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MovieRecommendationSys implements Initializable {

    public ListView listView;
    public ImageView imagePoster;
    public Label title_txt;
    public Label director_txt;
    public Text plot_txt;
    public TextField search_input;
    public Button search_btn;
    public Label year_txt;
    public Label actors_txt;
    public ComboBox<String> genres_combo;
    private List<Movie> movieList;
    ObservableList<Movie> listMovies = FXCollections.observableArrayList();
    private String filePath = "src/main/resources/ma/enset/movies_recommandation_system_sementicsdemo/convertjson.xml";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            search_input.setOnKeyPressed(actionEvent -> searchMovieByTitle(search_input.getText()));
            genres_combo.setOnAction(actionEvent -> filterByGenre(genres_combo.getSelectionModel().getSelectedItem()));
            listMovies.addAll(loadMoviesFromXml(filePath));
            listView.setItems(listMovies);
            genres_combo.setItems(FXCollections.observableArrayList(List.of("Comedy", "Fantasy", "Crime", "Drama", "Music", "Adventure", "History", "Thriller", "Animation", "Family", "Mystery", "Biography", "Action", "Film-Noir", "Romance", "Sci-Fi", "War", "Western", "Horror", "Musical", "Sport")));
            listView.setOnMouseClicked(actionEvent -> {
                try {
                    onSelectedItem();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onSelectedItem() throws FileNotFoundException {
        Movie movie = (Movie) listView.getSelectionModel().getSelectedItem();
        Image image = movie.getPoster();
        if(image != null){
            imagePoster.setImage(image);
        }
        else{
            imagePoster.setImage(new Image(new FileInputStream("src/main/resources/ma/enset/movies_recommandation_system_sementicsdemo/no_poster.jpg")));
        }
        title_txt.setText(movie.getTitle());
        year_txt.setText(String.valueOf(movie.getYear()));
        actors_txt.setText(movie.getActors());
        plot_txt.setText(movie.getPlot());
        director_txt.setText(movie.getDirector());
    }


    private ObservableList<Movie> loadMoviesFromXml(String filePath) throws Exception {
        movieList = new ArrayList<>();

        File xmlFile = new File(filePath);
        InputStream inputStream = new FileInputStream(xmlFile);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);

        NodeList genreNodes = doc.getElementsByTagName("genres");
        List<String> genres = new ArrayList<>();
        for (int i = 0; i < genreNodes.getLength(); i++) {
            Node genreNode = genreNodes.item(i);
            genres.add(genreNode.getTextContent());
        }

        NodeList movieNodes = doc.getElementsByTagName("movies");
        for (int i = 0; i < movieNodes.getLength(); i++) {
            Node movieNode = movieNodes.item(i);
            if (movieNode.getNodeType() == Node.ELEMENT_NODE) {
                Element movieElement = (Element) movieNode;

                int id = Integer.parseInt(movieElement.getElementsByTagName("id").item(0).getTextContent());
                String title = movieElement.getElementsByTagName("title").item(0).getTextContent();
                int year = Integer.parseInt(movieElement.getElementsByTagName("year").item(0).getTextContent());
                int runtime = Integer.parseInt(movieElement.getElementsByTagName("runtime").item(0).getTextContent());

                NodeList genreNodesForMovie = movieElement.getElementsByTagName("genres");
                List<String> genresForMovie = new ArrayList<>();
                for (int j = 0; j < genreNodesForMovie.getLength(); j++) {
                    Node genreNodeForMovie = genreNodesForMovie.item(j);
                    genresForMovie.add(genreNodeForMovie.getTextContent());
                }

                String director = movieElement.getElementsByTagName("director").item(0).getTextContent();
                String actors = movieElement.getElementsByTagName("actors").item(0).getTextContent();
                String plot = movieElement.getElementsByTagName("plot").item(0).getTextContent();
                String posterUrl = movieElement.getElementsByTagName("posterUrl").item(0).getTextContent();
                Image poster = null;
                if(posterUrl != null && !posterUrl.isEmpty()){
                    poster = new Image(posterUrl);
                }
                Movie movie = new Movie(id, title, year, runtime, genresForMovie, director, actors, plot, poster);
                movieList.add(movie);
            }
        }
        return FXCollections.observableList(movieList);
    }

    private void searchMovieByTitle(String searchText) {

        String query = "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                "SELECT ?movie ?title ?genre" +
                "WHERE {" +
                "  ?movie a dbo:Film." +
                "  ?movie dc:title ?title. " + search_input.getText()+
                "  OPTIONAL {" +
                "    ?movie dbo:genre ?genre. " +genres_combo.getSelectionModel().getSelectedItem()+
                "  }" +
                "}" +
                "ORDER BY ?title";

        FilteredList<Movie> filteredList = new FilteredList<>(listMovies, movie -> true);
        query = searchText;
        filteredList.setPredicate(movie -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseSearchText = searchText.toLowerCase();

            return movie.getTitle().toLowerCase().contains(lowerCaseSearchText);
        });
        listView.setItems(filteredList);
    }

    public void filterByGenre(String genre) {
        // Get the list of movies
        ObservableList<Movie> allMovies = listView.getItems();

        // Create a new filtered list for the movies that match the selected genre
        ObservableList<Movie> filteredMovies = FXCollections.observableArrayList();
        for (Movie movie : allMovies) {
            for(String gn : movie.getGenresForMovie()){
                if (gn.contains(genre)) {
                    filteredMovies.add(movie);
                }
            }
        }

        // Update the ListView with the filtered movies
        listView.setItems(filteredMovies);
    }

}
