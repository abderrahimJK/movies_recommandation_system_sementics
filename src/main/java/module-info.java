module ma.enset.movies_recommandation_system_sementicsdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.jena.arq;
    requires org.apache.jena.core;
    requires com.google.gson;
    requires java.xml;


    opens ma.enset.movies_recommandation_system_sementicsdemo to javafx.fxml;
    exports ma.enset.movies_recommandation_system_sementicsdemo;
}