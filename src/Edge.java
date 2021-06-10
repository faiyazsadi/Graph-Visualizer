import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Edge extends Line {
	public Edge(StackPane vertex1, StackPane vertex2, AnchorPane graph) {
		//getStyleClass().add("Edge");
//        startXProperty().bind(vertex1.centerXProperty().add(vertex1.translateXProperty()));
//        startYProperty().bind(vertex1.centerYProperty().add(vertex1.translateYProperty()));
//        endXProperty().bind(vertex2.centerXProperty().add(vertex2.translateXProperty()));
//        endYProperty().bind(vertex2.centerYProperty().add(vertex2.translateYProperty()));
        
        startXProperty().bind(vertex1.translateXProperty().add(40));
        startYProperty().bind(vertex1.translateYProperty().add(35));
        endXProperty().bind(vertex2.translateXProperty().add(40));
        endYProperty().bind(vertex2.translateYProperty().add(35));
        this.setStrokeWidth(20);
        this.setStroke(Color.SKYBLUE);

        graph.getChildren().add(this);
        this.toBack();
	}
}
