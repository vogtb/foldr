package foldr.shape;

import de.jreality.math.MatrixBuilder;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;

/**
 * A Jreality tool, which animates a shape an inputed degree of rotation along
 * an axis defined by the two points given.
 * 
 * @author Ellery Addington-White
 * 
 * 
 */
public class AnimateRotationVector extends AbstractTool {

	int currentFrame = 0;
	// this variable determines the number of frames the animation will
	// occur in
	int totalFramesForAnimation = 200;

	// the shape and scg that are being moved
	Shape shapeToMove;
	SceneGraphComponent sgcToMove;

	// the interval the shape will rotate every frame
	double intervalToRotate;

	// The vertex's that will create the axis to rotate around
	double[] vectorToMatch1;
	double[] vectorToMatch2;

	// the new coordinates to translate the shape to. These start as equal
	// to the current coordinates.
	double newX;
	double newY;
	double newZ;

	private final InputSlot TIME = InputSlot.SYSTEM_TIME;

	public AnimateRotationVector() {
		addCurrentSlot(TIME);

	}

	/**
	 * This method will rotate the shape along an axis defined by the two points
	 * given.
	 * 
	 * @param newShapeToMove
	 * @param angleToRotate
	 *            Use Radians!
	 * @param vertexToMatch1
	 * @param vertexToMatch2
	 */
	public void setEndPoints(Shape newShapeToMove, double[] v1, double[]v2) {
		shapeToMove = newShapeToMove;
		sgcToMove = shapeToMove.getShapeSGC();
		this.vectorToMatch1 = v1;
		this.vectorToMatch2 = v2;

		// Calculate the amount the shape should rotate for each frame.
		//intervalToRotate = angleToRotate / (double) totalFramesForAnimation;
		System.out.println(intervalToRotate);
		currentFrame = 0;

	}

	@Override
	public void perform(ToolContext tc) {

		// check if we've looped through the correct number of frames
		if (currentFrame == totalFramesForAnimation) {
			System.out.println("goal reached!");
			shapeToMove.inMotion = false;
			// animation is done, so get remove this tool from the shape
			sgcToMove.removeTool(this);
		} else {
			MatrixBuilder
					.euclidean().rotateFromTo(vectorToMatch1, vectorToMatch2);
					
//					rotate(vertexToMatch1, vertexToMatch2,
//							intervalToRotate * currentFrame)
//					.assignTo(sgcToMove);

			// Increment current Frame
			currentFrame++;
		}

	}

}
