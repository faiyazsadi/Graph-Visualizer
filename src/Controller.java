import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Stack;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
	@FXML private AnchorPane graph;
//	@FXML private Button addEdgeButton;
	@FXML private Button callbfs;
	@FXML private Button calldfs;
	@FXML private Button print;
	
	SequentialTransition sequentialTransition;
	
	int vertexCount = 0;
	ArrayList<Circle> vertexList;
	ArrayList<Edge> edgeList; 
	// have to clear vertexs
	Vertex[] vertexs;
	Edge[][] edges;

	int  clickCount = 0, vertex1ID, vertex2ID;
	StackPane stackPane1, stackPane2;
	
	
	// graph
	ArrayList<Integer> adj[];
	boolean[] discover;


	public Controller() {
		vertexList = new ArrayList<>();
		edgeList = new ArrayList<>();
		System.out.println("Constructor");
		adj = new ArrayList[100];
		for(int i = 0; i < 100; ++i) { 
			adj[i] = new ArrayList<>(); 
		}

		vertexs = new Vertex[100];
		edges  = new Edge[100][100];
		discover = new boolean[100];
	}
	
	public void onGraphPressed(MouseEvent mouseEvent) {
		if(mouseEvent.isPrimaryButtonDown()) {
			addVertex(mouseEvent);
		}
	}
	
//	public void callBFS() {
//		int root = 1;
//		bfsAnimation(root);
//	}

	public void addVertex(MouseEvent mouseEvent) {
		
        vertexCount++;
        Vertex vertex = new Vertex(mouseEvent, vertexCount, vertexs);
        
        vertexList.add(vertex);
//        
//        circle.setFill(Color.ROYALBLUE);
//        circle.setCenterX(mouseEvent.getX());
//        circle.setCenterY(mouseEvent.getY());
//        circle.setRadius(40);
        
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

        vertex.setOnMousePressed(event -> {
        	if(event.isControlDown() && event.isSecondaryButtonDown()) {
        		if(clickCount == 0) {
        			vertex1ID = vertex.ID;
        			stackPane1 = (StackPane) vertex.getParent();
        			clickCount = 1;
        			
        			System.out.println("BOI " + vertex1ID);
        		}
        		else if(clickCount == 1) {
        			vertex2ID = vertex.ID;
        			stackPane2 = (StackPane) vertex.getParent();
        			Edge edge = new Edge(stackPane1, stackPane2, graph, edges, vertex1ID, vertex2ID);
        			edgeList.add(edge);
        			clickCount = 0;
        			// this two vertex will have a edge in between
        			// bi-directional edges
        			adj[vertex1ID].add(vertex2ID);
        			adj[vertex2ID].add(vertex1ID);
        			System.out.println("BOI " + vertex2ID);
        			
        		}
                System.out.println("Number of Edges " + edgeList.size());
        	}
        });
       
//            FillTransition fillTransition = new FillTransition(Duration.seconds(3), circle, (Color) circle.getFill(), Color.DARKBLUE);
//            fillTransition.play();
        pane.getChildren().addAll(vertex, text);
        graph.getChildren().add(pane);
            
	}
	
//	public void addEdge() {
//		sequentialTransition = new SequentialTransition();
//		for(int i = 0; i < vertexList.size(); ++i) {
//			for(int j = i + 1; j < vertexList.size(); ++j) {
//				StackPane c1 = (StackPane) vertexList.get(i).getParent();
//				StackPane c2 = (StackPane) vertexList.get(j).getParent();
//				Edge edge = new Edge(c1, c2, graph, edges, 1, 2);
//				edgeList.add(edge);
//				
//				double startX = (double) vertexList.get(i).getCenterX();
//				double startY = (double) vertexList.get(i).getCenterY();
//				double endX = (double) vertexList.get(j).getCenterX();
//				double endY = (double) vertexList.get(j).getCenterY();
//				
//				DoubleProperty signalPosition = new SimpleDoubleProperty(0);
//				edge.strokeProperty().bind(Bindings.createObjectBinding(() -> 
//                new LinearGradient(startX, startY, endX, endY, false, CycleMethod.NO_CYCLE, 
//                    new Stop(0, Color.ROYALBLUE),
//                    new Stop(signalPosition.get(), Color.ROYALBLUE),
//                    new Stop(signalPosition.get(), Color.SKYBLUE),
//                    new Stop(1, Color.SKYBLUE)), 
//                signalPosition));
//
//            Timeline animation = new Timeline (
//                    new KeyFrame(Duration.ZERO, new KeyValue(signalPosition, 0)),
//                    new KeyFrame(Duration.seconds(3), new KeyValue(signalPosition, 1))
//            );
//            FillTransition fillTransition = new FillTransition(Duration.seconds(2), vertexList.get(j), (Color) vertexList.get(j).getFill(), Color.LIME);
//            vertexList.get(j).setFill(Color.LIME);
//            sequentialTransition.getChildren().add(animation);
//            sequentialTransition.getChildren().add(fillTransition);
//			}
//		}
//		sequentialTransition.play();
//	}

	public void bfsAnimation() {
		resetAnimation();
		Arrays.fill(discover, false);

		int root = 1;
		sequentialTransition = new SequentialTransition();
        FillTransition fillTransition = new FillTransition(Duration.seconds(2), vertexs[root], (Color) vertexs[root].getFill(), Color.LIME);
        sequentialTransition.getChildren().add(fillTransition);

		discover[root] = true;
		Queue<Integer> queue = new ArrayDeque<>();
		queue.add(root);
		while(!queue.isEmpty()) {
			int u = queue.poll();
			for(int v : adj[u]) { 
				if(discover[v] == false) {

					double startX = (double) vertexs[u].getCenterX();
					double startY = (double) vertexs[u].getCenterY();
					double endX = (double) vertexs[v].getCenterX();
					double endY = (double) vertexs[v].getCenterY();

                    DoubleProperty signalPosition = new SimpleDoubleProperty(0);
                    Edge edge = edges[u][v];
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
                    
                    sequentialTransition.getChildren().add(animation);
                    fillTransition = new FillTransition(Duration.seconds(2), vertexs[v], (Color) vertexs[v].getFill(), Color.LIME);

                    sequentialTransition.getChildren().add(fillTransition);
                    
					queue.add(v);
					discover[v] = true; 
				}
			}
		}
		sequentialTransition.play();
	}
	
	public void callDFS() {
		resetAnimation();
		Arrays.fill(discover, false);
		int root = 1;
		sequentialTransition = new SequentialTransition();
		dfsAnimation(root, sequentialTransition);
		sequentialTransition.play();
	}
	
	public void dfsAnimation(int u, SequentialTransition sequentialTransition) {
		if(vertexs[u].getFill() != Color.LIME) {
            FillTransition fillTransition = new FillTransition(Duration.seconds(2), vertexs[u], (Color) vertexs[u].getFill(), Color.LIME);
            vertexs[u].setFill(Color.LIME);
            sequentialTransition.getChildren().add(fillTransition);
		}

		discover[u]	= true;

		for(int v : adj[u]) {

			if(discover[v] == false) {
				
                double startX = (double) vertexs[u].getCenterX();
                double startY = (double) vertexs[u].getCenterY();
                double endX = (double) vertexs[v].getCenterX();
                double endY = (double) vertexs[v].getCenterY();

                DoubleProperty signalPosition = new SimpleDoubleProperty(0);
                Edge edge = edges[u][v];
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
                
                sequentialTransition.getChildren().add(animation);
                if(vertexs[v].getFill() != Color.LIME) {
                    FillTransition fillTransition = new FillTransition(Duration.seconds(2), vertexs[v], (Color) vertexs[v].getFill(), Color.LIME);
                    vertexs[v].setFill(Color.LIME);
                    sequentialTransition.getChildren().add(fillTransition);
                }

                dfsAnimation(v, sequentialTransition);
			}
		}
	}

	public void resetAnimation() {
		for(int i = 1; i <= vertexCount; ++i) {
			vertexs[i].setFill(Color.ROYALBLUE);
		}
		for(int i = 1; i <= vertexCount; ++i) {
			for(int j = 1; j <= vertexCount; ++j) {
				if(edges[i][j] != null) {
					edges[i][j].setFill(Color.SKYBLUE);
				}
			}
		}
	}

	public void printGraph() {
		for(int i = 0; i <= vertexCount; ++i) {
			System.out.println(i + ": ");
			for(int j = 0; j < adj[i].size(); ++j) {
				System.out.println(adj[i].get(j));
			}
		}
	}

	public void clear() {
		vertexCount = 0;
		sequentialTransition.getChildren().clear();
		for(int i = 0; i < 100; ++i) {
			adj[i].clear();
			vertexs[i] = null;
			
		}
		vertexList.clear();
		edgeList.clear();
	}
}
