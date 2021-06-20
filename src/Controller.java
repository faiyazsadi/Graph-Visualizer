import java.nio.BufferUnderflowException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import java.util.Set;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

public class Controller {
	@FXML private AnchorPane graph;
//	@FXML private Button addEdgeButton;
	@FXML private JFXButton callbfs;
	@FXML private JFXButton calldfs;
	@FXML private JFXButton reset;
	@FXML private JFXButton clear;
	@FXML private JFXButton pauseandplay;
	@FXML private JFXButton setRootButton;
	SequentialTransition sequentialTransition;
	
	int vertexCount = 0;
	ArrayList<Circle> vertexList;
	ArrayList<Edge> edgeList; 
	// have to clear vertexs
	int root;
	Vertex[] vertexs;
	Edge[][] edges;
	HashMap<Edge, Pair<Integer, Integer>> EdgeMap;
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
		EdgeMap = new HashMap<>();
	}
	
	public void onGraphPressed(MouseEvent mouseEvent) {
		if(mouseEvent.isPrimaryButtonDown()) {
			addVertex(mouseEvent); }
	}

	public void addVertex(MouseEvent mouseEvent) {
		
        vertexCount++;
        Vertex vertex = new Vertex(mouseEvent, vertexCount, vertexs);
        
        vertexList.add(vertex);
        nodes.add(vertex.ID);
        
        
        Text text = new Text(String.valueOf(vertexCount));
        text.setFill(Color.BLACK);
        text.setFont(Font.font("Roboto", FontWeight.BOLD, 16));
        text.setOnMousePressed(null);
        //text.toFront();
        
        StackPane pane = new StackPane();
        pane.setTranslateX(mouseEvent.getX() - 40);
        pane.setTranslateY(mouseEvent.getY() - 35);

        pane.setOnMouseDragged(event -> {
            if(event.isSecondaryButtonDown()) {
//                pane.setTranslateX(event.getX() + pane.getTranslateX());
//                pane.setTranslateY(event.getY() + pane.getTranslateY());
                pane.setTranslateX(event.getX() + pane.getTranslateX() - 40);
                pane.setTranslateY(event.getY() + pane.getTranslateY() - 35);
                vertex.setCenterX(pane.getTranslateX());
                vertex.setCenterY(pane.getTranslateY());
            }
            
        });

        pane.setOnMousePressed(event -> {
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
        			Edge edge = new Edge(stackPane1, stackPane2, graph, edges, vertex1ID, vertex2ID, EdgeMap);
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
        					Pair<Integer, Integer> pair = EdgeMap.get(edge);
        					int u = pair.getKey();
        					int v = pair.getValue();
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
        					EdgeMap.remove(edge);
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
//        		Vertex vertex1 = (Vertex) pane.getChildren().get(0);
//        		int id = vertex1.ID;
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
        		if(!nodes.contains(root)) {
        			root = nodes.iterator().next();
        		}
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
		pauseandplay.setText("Pause Animation");
		Arrays.fill(discover, false);

//		int root = 1;
		sequentialTransition = new SequentialTransition();
        FillTransition fillTransition = new FillTransition(Duration.seconds(2), vertexs[root], (Color) vertexs[root].getFill(), Color.web("#98FB98"));
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
                        new KeyFrame(Duration.seconds(2), new KeyValue(signalPosition, 1))
                    );
                    
                    sequentialTransition.getChildren().add(animation);
                    fillTransition = new FillTransition(Duration.seconds(2), vertexs[v], (Color) vertexs[v].getFill(), Color.web("#98FB98"));

                    sequentialTransition.getChildren().add(fillTransition);
                    
					queue.add(v);
					discover[v] = true; 
				}
			}
		}
		sequentialTransition.play();
        sequentialTransition.setOnFinished((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	System.out.println("Animation Finished!");
		    	animationFinished("BFS ");
		    }
		});
	}
	
	public void callDFS() {
		resetAnimation();
		pauseandplay.setText("Pause Animation");
		Arrays.fill(discover, false);
//		int root = 1;
		sequentialTransition = new SequentialTransition();
		dfsAnimation(root, sequentialTransition);
		sequentialTransition.play();

		sequentialTransition.setOnFinished((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	System.out.println("Animation Finished!");
		    	animationFinished("DFS ");
		    }
		});
	}
	
	public void dfsAnimation(int u, SequentialTransition sequentialTransition) {
		if(vertexs[u].getFill() != Color.web("#98FB98")) {
            FillTransition fillTransition = new FillTransition(Duration.seconds(1), vertexs[u], (Color) vertexs[u].getFill(), Color.web("#98FB98"));
            vertexs[u].setFill(Color.web("#98FB98"));
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
                    new KeyFrame(Duration.seconds(2), new KeyValue(signalPosition, 1))
                );
                
                sequentialTransition.getChildren().add(animation);
                if(vertexs[v].getFill() != Color.web("#98FB98")) {
                    FillTransition fillTransition = new FillTransition(Duration.seconds(1), vertexs[v], (Color) vertexs[v].getFill(), Color.web("#98FB98"));
                    vertexs[v].setFill(Color.web("#98FB98"));
                    sequentialTransition.getChildren().add(fillTransition);
                }
                
                dfsAnimation(v, sequentialTransition);
			}
		}
		FillTransition fillTransition1 = new FillTransition(Duration.millis(500), vertexs[u], (Color) vertexs[u].getFill(), Color.web("#FF6347"));
		FillTransition fillTransition2 = new FillTransition(Duration.millis(500), vertexs[u], Color.web("#FF6347"), (Color) vertexs[u].getFill());
		sequentialTransition.getChildren().addAll(fillTransition1, fillTransition2);
	}

	public void animationFinished(String algo) {
	// put jfoenix dialog here	
		StackPane stackpane = new StackPane();
		
		stackpane.setTranslateX(600);
		stackpane.setTranslateY(300);

		JFXDialogLayout content = new JFXDialogLayout();

		Text heading = new Text("Animation Status");
		heading.setFill(Color.BLACK);
		heading.setFont(Font.font("Roboto", FontWeight.BOLD, 18));
		content.setHeading(heading);
		

		Text body = new Text(algo + "Animation has finished.");
		body.setFill(Color.BLACK);
		body.setStyle("-fx-font: 16 Roboto_Mono");
		content.setBody(body);
		content.getStyleClass().add("jfx-dialog-overlay-pane");

		JFXDialog dialog = new JFXDialog(stackpane, content, JFXDialog.DialogTransition.CENTER);
		dialog.setPrefSize(40, 5);

		JFXButton button = new JFXButton();
		button.setText("Okay");
		button.setFont(Font.font("Roboto", FontWeight.BOLD, 14));
		button.setTextFill(Color.WHITE);
		button.setFocusTraversable(false);
		button.setStyle("-fx-background-color: #1E90FF;");
		button.setPrefWidth(60);
		button.setOnAction(event -> {
			dialog.close();
		});
		
		content.setActions(button);
		dialog.show();
		graph.getChildren().add(stackpane);
	}

	public void resetAnimation() {
		for(int i = 1; i <= vertexCount; ++i) {
			if(vertexs[i] != null) {
				vertexs[i].setFill(Color.web("#87CEEB"));
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
			pauseandplay.setText("Play Animation");
			sequentialTransition.pause();
		}
		else if(sequentialTransition.getStatus() == Status.PAUSED) {
			pauseandplay.setText("Pause Animation");
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
		EdgeMap.clear();
		nodes.clear();
		vertexList.clear();
		edgeList.clear();
		graph.getChildren().clear();
		pauseandplay.setText("Pause Animation");
		graph.getChildren().addAll(pauseandplay, callbfs, calldfs, reset, clear, setRootButton);
	}

	public void setRoot() {
		
		TextInputDialog dialog = new TextInputDialog("1");
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(this.getClass().getResourceAsStream("setroot.png")));

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

		dialog.setHeaderText("Choose Root Node from these: " + availableNodes.toString());
		dialog.setTitle("Set New Root");
		
//		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		
		dialog.setContentText("New Root Node: ");
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent()) {
			System.out.println(result.get());
		}
		else {
			System.out.println("Enter Correct Value.");
			return;
		}
		
		int newRoot;
		
		try {
			newRoot = Integer.parseInt(result.get());
			if(nodes.contains(newRoot)) {
				setNewRoot(newRoot);
			}
			else {
				setRootButton.setDisable(true);
				showErrorDialog();
				return;
			}
		}
		catch (NumberFormatException e) {
			showErrorDialog();
			return;
		}
	}
	
	
	public void setNewRoot(int newRoot) {
		root = newRoot;
	}

	public void showErrorDialog() {
		StackPane stackpane = new StackPane();
		stackpane.setTranslateX(600);
		stackpane.setTranslateY(300);
		System.out.println("New Root is not present in the Graph");
		JFXDialogLayout content = new JFXDialogLayout();

		content.setStyle("-fx-background-color: white;");
		content.getStyleClass().add("jfx-dialog-overlay-pane");
		Text heading = new Text("Node Not Found!");
		heading.setFill(Color.BLACK);
		heading.setFont(Font.font("Roboto", FontWeight.BOLD, 24));
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
		body.setFill(Color.BLACK);
		body.setFont(Font.font("Roboto", FontWeight.NORMAL, 20));
		content.setBody(body);
		JFXDialog dialog = new JFXDialog(stackpane, content, JFXDialog.DialogTransition.CENTER);
		dialog.setPrefSize(100, 100);
		JFXButton button = new JFXButton();
		button.setText("Okay");
		button.setFont(Font.font("Roboto", FontWeight.BOLD, 14));
		button.setTextFill(Color.WHITE);
		button.setFocusTraversable(false);
		button.setStyle("-fx-background-color: #1E90FF;");
		button.setOnAction(event -> {
			dialog.close();
			setRootButton.setDisable(false);
			setRoot();
		});

		content.setActions(button);
		dialog.show();
		graph.getChildren().add(stackpane);
	}

}
