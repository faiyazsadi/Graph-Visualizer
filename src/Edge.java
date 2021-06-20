import java.util.HashMap;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Pair;

public class Edge extends Line {
	public Edge(StackPane spane1, StackPane spane2, AnchorPane graph, Edge[][] edges, int u, int v, HashMap<Edge, Pair<Integer, Integer>> EdgeMap) {
		//getStyleClass().add("Edge");
//        startXProperty().bind(vertex1.centerXProperty().add(vertex1.translateXProperty()));
//        startYProperty().bind(vertex1.centerYProperty().add(vertex1.translateYProperty()));
//        endXProperty().bind(vertex2.centerXProperty().add(vertex2.translateXProperty()));
//        endYProperty().bind(vertex2.centerYProperty().add(vertex2.translateYProperty()));
        
        startXProperty().bind(spane1.translateXProperty().add(40));
        startYProperty().bind(spane1.translateYProperty().add(35));
        endXProperty().bind(spane2.translateXProperty().add(40));
        endYProperty().bind(spane2.translateYProperty().add(35));
        setStrokeWidth(20);
        setStroke(Color.SKYBLUE);
        edges[u][v] = this;
        edges[v][u] = this; 
        EdgeMap.put(this, new Pair(u, v));
        graph.getChildren().add(this);
        toBack();
	}
}
