import java.util.ArrayList;


import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller {
	@FXML
	private AnchorPane graph;
	@FXML
	private Button addEdgeButton;
	SequentialTransition sequentialTransition;
	
	int vertexCount = 0;
	ArrayList<Circle> vertexList = new ArrayList<>();
	ArrayList<Edge> edgeList = new ArrayList<>(); 
	int  clickCount = 0;
	StackPane stackPane1, stackPane2;
	
	public void onGraphPressed(MouseEvent mouseEvent) {
		if(mouseEvent.isPrimaryButtonDown()) {
			addVertex(mouseEvent);
		}
	}

	public void addVertex(MouseEvent mouseEvent) {
		
        vertexCount++;
        Circle circle = new Circle();
        
        vertexList.add(circle);
        
        circle.setFill(Color.ROYALBLUE);
        circle.setCenterX(mouseEvent.getX());
        circle.setCenterY(mouseEvent.getY());
        circle.setRadius(40);
        
        Text text = new Text(String.valueOf(vertexCount));
        text.setFill(Color.WHITE);
        //text.toFront();
        
        StackPane pane = new StackPane();
        pane.setTranslateX(mouseEvent.getX() - 40);
        pane.setTranslateY(mouseEvent.getY() - 35);

        pane.setOnMouseDragged(event -> {
            if(event.isSecondaryButtonDown()) {
                pane.setTranslateX(event.getX() + pane.getTranslateX());
                pane.setTranslateY(event.getY() + pane.getTranslateY());
            }
            
        });

        circle.setOnMousePressed(event -> {
        	if(event.isControlDown() && event.isSecondaryButtonDown()) {
        		if(clickCount == 0) {
        			stackPane1 = (StackPane) circle.getParent();
        			clickCount = 1;
        		}
        		else if(clickCount == 1) {
        			stackPane2 = (StackPane) circle.getParent();
        			Edge edge = new Edge(stackPane1, stackPane2, graph);
        			edgeList.add(edge);
        			clickCount = 0;
        		}
                System.out.println("Number of Edges " + edgeList.size());
        	}
        });
       
//            FillTransition fillTransition = new FillTransition(Duration.seconds(3), circle, (Color) circle.getFill(), Color.DARKBLUE);
//            fillTransition.play();
        pane.getChildren().addAll(circle, text);
        graph.getChildren().add(pane);
            
	}
	
	public void addEdge() {
		sequentialTransition = new SequentialTransition();
		for(int i = 0; i < vertexList.size(); ++i) {
			for(int j = i + 1; j < vertexList.size(); ++j) {
				StackPane c1 = (StackPane) vertexList.get(i).getParent();
				StackPane c2 = (StackPane) vertexList.get(j).getParent();
				Edge edge = new Edge(c1, c2, graph);
				edgeList.add(edge);
				
				double startX = (double) vertexList.get(i).getCenterX();
				double startY = (double) vertexList.get(i).getCenterY();
				double endX = (double) vertexList.get(j).getCenterX();
				double endY = (double) vertexList.get(j).getCenterY();
				
				DoubleProperty signalPosition = new SimpleDoubleProperty(0);
				edge.strokeProperty().bind(Bindings.createObjectBinding(() -> 
                new LinearGradient(startX, startY, endX, endY, false, CycleMethod.NO_CYCLE, 
                    new Stop(0, Color.ROYALBLUE),
                    new Stop(signalPosition.get(), Color.ROYALBLUE),
                    new Stop(signalPosition.get(), Color.SKYBLUE),
                    new Stop(1, Color.SKYBLUE)), 
                signalPosition));

            Timeline animation = new Timeline (
                    new KeyFrame(Duration.ZERO, new KeyValue(signalPosition, 0)),
                    new KeyFrame(Duration.seconds(3), new KeyValue(signalPosition, 1))
            );
            FillTransition fillTransition = new FillTransition(Duration.seconds(2), vertexList.get(j), (Color) vertexList.get(j).getFill(), Color.LIME);
            vertexList.get(j).setFill(Color.LIME);
            sequentialTransition.getChildren().add(animation);
            sequentialTransition.getChildren().add(fillTransition);
			}
		}
		sequentialTransition.play();
	}
}
