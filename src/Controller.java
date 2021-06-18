import java.nio.BufferUnderflowException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import java.util.Queue;

import javafx.animation.Animation.Status;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Set;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

public class Controller {
	@FXML private AnchorPane graph;
//	@FXML private Button addEdgeButton;
	@FXML private Button callbfs;
	@FXML private Button calldfs;
	@FXML private Button reset;
	@FXML private Button clear;
	@FXML private Button pauseandplay;
	@FXML private JFXButton setRootButton;
	SequentialTransition sequentialTransition;
	
	int vertexCount = 0;
	ArrayList<Circle> vertexList;
	ArrayList<Edge> edgeList; 
	// have to clear vertexs
	int root;
	Vertex[] vertexs;
	Edge[][] edges;
	Set<Integer> nodes;

	int  clickCount = 0, vertex1ID, vertex2ID;
	StackPane stackPane1, stackPane2;
	
	
	// graph
	ArrayList<Integer> adj[];
	boolean[] discover;


	public Controller() {
		root = 1;
		vertexList = new ArrayList<>();
		edgeList = new ArrayList<>();
//		System.out.println("Constructor");
		adj = new ArrayList[100];
		for(int i = 0; i < 100; ++i) { 
			adj[i] = new ArrayList<>(); 
		}

		vertexs = new Vertex[100];
		edges  = new Edge[100][100];
		discover = new boolean[100];
		nodes = new HashSet<>();
	}
	
	public void onGraphPressed(MouseEvent mouseEvent) {
		if(mouseEvent.isPrimaryButtonDown()) {
			addVertex(mouseEvent);
		}
	}

	public void addVertex(MouseEvent mouseEvent) {
		
        vertexCount++;
        Vertex vertex = new Vertex(mouseEvent, vertexCount, vertexs);
        
        vertexList.add(vertex);
        nodes.add(vertex.ID);
        
        
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
        			
        			System.out.println(vertex1ID);
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
        			System.out.println(vertex2ID);

        			edge.setOnMousePressed(edgeEvent -> {
        				// this removes an edge from the canvas.
        				if(edgeEvent.isAltDown() && edgeEvent.isSecondaryButtonDown()) {
        					graph.getChildren().remove(edge);
        					int u = vertex1ID;
        					int v = vertex2ID;
        					edges[u][v] = null;
        					edges[v][u] = null; 
        					for(int i = 0; i < adj[u].size(); ++i) {
        						if(adj[u].get(i) == v) {
        							adj[u].remove(i);
        						}
        					}
        					for(int i = 0; i < adj[v].size(); ++i) {
        						if(adj[v].get(i) == u) {
        							adj[v].remove(i);
        						}
        					}
        					printGraph();
        				}
        			});
        			
        		}
                System.out.println("Number of Edges " + edgeList.size());
        	}
        	else if(event.isAltDown() && event.isSecondaryButtonDown()) {
        		// this is here to delete selected node from the graph.
//        		vertexCount--;
        		int id = vertex.ID;
        		StackPane saPane = (StackPane) vertex.getParent();
        		graph.getChildren().remove(saPane);
        		int u = id;
        		for(int i = 0; i < adj[id].size(); ++i) {
        			int v = adj[id].get(i);
        			if(edges[u][v] != null) {
        				graph.getChildren().remove(edges[u][v]);
        			}
        			if(edges[v][u] != null) {
        				graph.getChildren().remove(edges[v][u]);
        			}
        			edges[u][v] = null; 
        			edges[v][u] = null; 
        			for(int j = 0; j < adj[v].size(); ++j) {
        				if(adj[v].get(j) == u) {
        					adj[v].remove(j);
        					System.out.println("Removed " + u);
        				}
        			}
        		}
        		nodes.remove(id);
        		root = nodes.iterator().next();
        		System.out.println("Next Root is: " + root);
        		vertexs[id] = null; 
        		adj[id].clear();
        		printGraph();
        	}
        });
       
        pane.getChildren().addAll(vertex, text);
        graph.getChildren().add(pane);
            
	}
	
	public void bfsAnimation() {
		resetAnimation();
		pauseandplay.setText("Pause");
		Arrays.fill(discover, false);

//		int root = 1;
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
		pauseandplay.setText("Pause");
		Arrays.fill(discover, false);
//		int root = 1;
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
			if(vertexs[i] != null) {
				vertexs[i].setFill(Color.ROYALBLUE);
			}
		}
		for(int i = 1; i <= vertexCount; ++i) {
			for(int j = 1; j <= vertexCount; ++j) {
				if(edges[i][j] != null) {
					edges[i][j].strokeProperty().unbind();
					edges[i][j].setStroke(Color.SKYBLUE);
					
				}
				if(edges[j][i] != null) {
					edges[j][i].strokeProperty().unbind();
					edges[j][i].setStroke(Color.SKYBLUE);
				}
			}
		}
	}
	
	public void pauseAndplayAnimation() {
		if(sequentialTransition.getStatus() == Status.RUNNING) {
			pauseandplay.setText("Play");
			sequentialTransition.pause();
		}
		else if(sequentialTransition.getStatus() == Status.PAUSED) {
			pauseandplay.setText("Pause");
			sequentialTransition.play();
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
		root = 1;
		vertexCount = 0;
		for(int i = 0; i < 100; ++i) {
			adj[i].clear();
			vertexs[i] = null;
			for(int j = 0; j < 100; ++j) {
				edges[i][j] = null;
			}
		}
		nodes.clear();
		vertexList.clear();
		edgeList.clear();
		graph.getChildren().clear();
		pauseandplay.setText("Pause");
		graph.getChildren().addAll(pauseandplay, callbfs, calldfs, reset, clear);
	}

	public void setRoot() {
		VBox vBox = new VBox();
		TextField textField = new TextField();
		textField.setPrefSize(20, 30);
		Button button = new Button("Set Root");
		Label label = new Label("Enter New Root.");
		vBox.getChildren().addAll(label, textField, button);
		vBox.setLayoutX(500);
		vBox.setLayoutY(300);
		vBox.setSpacing(20);
		button.setOnAction(event -> {

			String string = textField.getText();
			
			if(string.isEmpty()) {
				showErrorDialog();
				return;
			}
			
			int newRoot;
			
			try {
				newRoot = Integer.parseInt(string);
			}
			catch (NumberFormatException e) {
				graph.getChildren().remove(vBox);
				showErrorDialog();
				return;
			}

			System.out.println(newRoot);
			if(nodes.contains(newRoot)) {
				setNewRoot(newRoot);
				vBox.getChildren().clear();
				graph.getChildren().remove(vBox);
			}
			else {
				graph.getChildren().remove(vBox);
				showErrorDialog();
			}

		});
		graph.getChildren().add(vBox);
	}
	
	
	public void setNewRoot(int newRoot) {
		root = newRoot;
	}

	public void showErrorDialog() {
		StackPane stackpane = new StackPane();
		stackpane.setLayoutX(400);
		stackpane.setLayoutY(500);
		System.out.println("New Root is not present in the Graph");
		JFXDialogLayout content = new JFXDialogLayout();
		content.setStyle("-fx-background-color: royalblue;");
		Text heading = new Text("Node Not Found!");
		heading.setFill(Color.WHITE);
		heading.setStyle("-fx-font: 24 Roboto");
		content.setHeading(heading);
		
		StringBuilder availableNodes = new StringBuilder();
		availableNodes.append(new String("["));
		int count = 0;
		for(int elem : nodes) {
			count += 1;
			availableNodes.append(String.valueOf(elem));
			if(count != nodes.size()) {
				availableNodes.append(", ");
			}
		}
		availableNodes.append("]");
		System.out.println(availableNodes);
		
		Text body = new Text("Choose one of these Nodes " + availableNodes.toString());
		body.setFill(Color.WHITE);
		body.setStyle("-fx-font: 20 Roboto;");
		content.setBody(body);
		JFXDialog dialog = new JFXDialog(stackpane, content, JFXDialog.DialogTransition.CENTER);
		JFXButton button = new JFXButton();
		button.setText("Okay");
		button.setTextFill(Color.BLACK);
		button.setStyle("-fx-background-color: white;");

		button.setOnAction(event -> {
			dialog.close();
		});
		content.setActions(button);
		dialog.show();
		graph.getChildren().add(stackpane);
		setRoot();
	}

}
