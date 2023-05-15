package ma.enset.movies_recommandation_system_sementicsdemo;

import javafx.scene.image.Image;

import java.util.List;
import java.util.Objects;

public class Movie {

    private int id;
    private int year;
    private int runtime;
    private List<String> genresForMovie;
    private String director;
    private String actors;
    private String plot;
    private Image poster;
    private String title;

    public Movie(String title) {
        this.title = title;
    }

    public Movie(int id, String title, int year, int runtime, List<String> genresForMovie, String director, String actors, String plot, Image poster) {
        this.id = id;
        this.year = year;
        this.runtime = runtime;
        this.genresForMovie = genresForMovie;
        this.director = director;
        this.actors = actors;
        this.plot = plot;
        this.poster = poster;
        this.title = title;
    }

    public Movie(String title, String plotSummary) {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<String> getGenresForMovie() {
        return genresForMovie;
    }

    public void setGenresForMovie(List<String> genresForMovie) {
        this.genresForMovie = genresForMovie;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public Image getPoster() {
        return poster;
    }

    public void setPoster(Image poster) {
        this.poster = poster;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return title.equals(movie.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return title;
    }
}
