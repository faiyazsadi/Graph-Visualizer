
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Vertex extends Circle {
	int ID;
	public Vertex(MouseEvent mouseEvent, int vertexCount, Vertex[] vertexs) {
		ID = vertexCount;
		setFill(Color.ROYALBLUE);
        setCenterX(mouseEvent.getX());
        setCenterY(mouseEvent.getY());
        setRadius(40);
        vertexs[ID] = this;
	}
	
}
