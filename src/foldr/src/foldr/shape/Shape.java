
package foldr.shape;


import de.jreality.geometry.Primitives;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.FaceDragEvent;
import de.jreality.tools.FaceDragListener;
import de.jreality.tools.LineDragEvent;
import de.jreality.tools.LineDragListener;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jreality.util.SceneGraphUtility;

import java.util.*;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.scene.IndexedFaceSet;

/**
 * <p>
 * A group of objects (Vertices, Faces, "Geometries," etc.) that form a closed
 * 3D shape of some thickness.
 * </p>
 * 
 * @author vogtb and couretn
 */
public class Shape {

	/**
	 * <p>
	 * To facilitate the process of finding which ShapeGroup a Shape is apart
	 * of.
	 * </p>
	 */
	private ShapeCollection allShapes = ShapeCollection.getInstance();
	private ShapeGroup group;
	public SceneGraphComponent shapeSGC;
	// make a new instance of the animation tool
	// TODO leave public or make a getter? The only reason for making it public
	// is so that the JUnit test can see it
	public AnimateMovement animateShape = new AnimateMovement();
	public AnimateRotation rotateShape = new AnimateRotation();
	public AnimateRotation2 rotateShapeTheOtherWay = new AnimateRotation2();
	private DragEventTool shapeClicked = new DragEventTool();
	public boolean inMotion = false;


	public SceneGraphComponent getShapeSGC() {
		return shapeSGC;
	}

	/**
	 * A simple constructor to create a regular polygon on the canvas.
	 * 
	 * @param numSides
	 *            The number of sides the polygon should have.
	 * @param parentScene
	 *            The SceneGraphComponent that this shape will be a child of.
	 */
	public Shape(int numSides, SceneGraphComponent parentScene) {

		// create the polygon
		IndexedFaceSet shapeGeometry = Primitives.regularPolygon(numSides);
		shapeSGC = SceneGraphUtility.createFullSceneGraphComponent();
		shapeSGC.setGeometry(shapeGeometry);
		parentScene.addChild(shapeSGC);
		// add it to the collection of all shapes on screen
		allShapes.addShapeToCollection(this);
		// make a new shape group and add this shape into it
		group = new ShapeGroup();
		group.shapesInGroup.add(this);

		/**
		 * Experimenting around with pre-made listeners. This tells you how many
		 * shapes are in a shapegroup when you click on a face.
		 */
		shapeClicked.addFaceDragListener(new FaceDragListener() {

			@Override
			public void faceDragged(FaceDragEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void faceDragEnd(FaceDragEvent e) {
				System.out.println("This shape group has "
						+ group.shapesInGroup.size() + " shapes in it");
			}

			@Override
			public void faceDragStart(FaceDragEvent e) {
				// TODO Auto-generated method stub

			}
		});
		shapeSGC.addTool(shapeClicked);

	}

	/**
	 * Method that simplifies translation of shapes. Just using it to test
	 * animation.
	 */
	public void translate(double x, double y, double z) {
		MatrixBuilder.euclidean().translate(x, y, z).assignTo(this.shapeSGC);
	}

	/**
	 * <p>
	 * Highlights the edges of the Shape with a bright color.
	 * </p>
	 */
	public void changeAppearance() {

	}



	/**
	 * Set the Shape to be apart of a group.
	 * 
	 * @param group
	 *            The group that you are adding this Shape to.
	 */
	public void setGroup(ShapeGroup group) {
		this.group = group;
		this.group.shapesInGroup.add(this);
	}

	/**
	 * Returns the current coordinates of a vertex
	 * 
	 * @param vertex
	 *            Which vertex to get the coordinates of
	 * @return An array of doubles in the format [x,y,z]
	 */
	public double[] getCurrentVertexCoordinates(int vertex) {

		// the original vertex coordinates as strings
		String vertexCoorX = shapeSGC.getGeometry().getAttributes("VERTEX")
				.get(Attribute.COORDINATES, vertex).get(0).toString();
		String vertexCoorY = shapeSGC.getGeometry().getAttributes("VERTEX")
				.get(Attribute.COORDINATES, vertex).get(1).toString();
		String vertexCoorZ = shapeSGC.getGeometry().getAttributes("VERTEX")
				.get(Attribute.COORDINATES, vertex).get(2).toString();

		// the original vertex coordinates as doubles
		double originalVertexX = Double.parseDouble(vertexCoorX.substring(1,
				vertexCoorX.length() - 1));
		double originalVertexY = Double.parseDouble(vertexCoorY.substring(1,
				vertexCoorY.length() - 1));
		double originalVertexZ = Double.parseDouble(vertexCoorZ.substring(1,
				vertexCoorZ.length() - 1));

		// the current translation
		double currentTranslationX = shapeSGC.getTransformation().getMatrix()[3];
		double currentTranslationY = shapeSGC.getTransformation().getMatrix()[7];
		double currentTranslationZ = shapeSGC.getTransformation().getMatrix()[11];

		// the array with the current coordinates of the selected vertex
		double[] allCurrentVertexCoor = {
				originalVertexX + currentTranslationX,
				originalVertexY + currentTranslationY,
				originalVertexZ + currentTranslationZ };
		return allCurrentVertexCoor;
	}

	/**
	 * The public method used for animation. Will only run an animation if the
	 * shape is not already being animated. If called on a shape in animation,
	 * this method will simply return false.
	 * 
	 * @param endPoints
	 *            The coordinates the shape will be translated to.
	 * @param vertexToTranslate
	 *            The vertex
	 * @return false if the shape is already being animated, true if it is not
	 */
	public boolean animateShape(double[] endPoints) {
		if (inMotion) {
			return false;
		} else {
			inMotion = true;
			// attach the animation tool to this sgc
			shapeSGC.addTool(animateShape);
			animateShape.setEndPoints(this, endPoints);
			return true;
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean rotateShape(double angleToRotate, char planeOfRotation) {
		if (inMotion) {
			return false;
		} else {
			inMotion = true;
			// attach rotate shape tool
			shapeSGC.addTool(rotateShape);
			rotateShape.setEndPoints(this, angleToRotate, planeOfRotation);
		}
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public boolean rotateShapeOtherWay(double angleToRotate,
			double[] vertexToMatch1, double[] vertexToMatch2) {
		if (inMotion) {
			return false;
		} else {
			inMotion = true;
			// attach rotate shape tool
			shapeSGC.addTool(rotateShapeTheOtherWay);
			rotateShapeTheOtherWay.setEndPoints(this, angleToRotate, vertexToMatch1,
					vertexToMatch2);
		}
		return true;
	}

    /**
     * <p>
     * To facilitate the process of finding which ShapeGroup a Shape is apart
     * of.
     * </p>
     */
   
    private Boolean        isHighlighted;
    private IndexedFaceSet set;

    /**
     * <p>
     * Default constructor creates a Shape with no points, edges, or faces.
     * Those are added once the shape is constructed. Should also be given an
     * origin.
     * </p>
     */
    public Shape() {

        group = new ShapeGroup();
        set = new IndexedFaceSet();
        isHighlighted = false;
    }
    
    public Shape(double[][] v, int[][] f) {
        IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
        ifsf.setVertexCount(v.length);
        ifsf.setVertexCoordinates(v);
        ifsf.setFaceCount(f.length);
        ifsf.setFaceIndices(f);
        ifsf.update();
        group = new ShapeGroup();
        set = ifsf.getIndexedFaceSet();
        isHighlighted = false;
    }

    /**
     * <p>
     * Turns the highlight appearance on or off.
     * </p>
     */
    public void setHighlight(Boolean b) {

        isHighlighted = b;
    }
    
    public boolean isHighlight() {
        return isHighlighted;
    }

    public IndexedFaceSet getFaceSet() {

        IndexedFaceSetFactory factory = new IndexedFaceSetFactory();
        return set;
    }

    /**
     * <p>
     * Returns the ShapeGroup that the Shape is in.
     * </p>
     * 
     * @return The ShapeGroup the Shape is in.
     */
    public ShapeGroup getGroup() {

        return group;
    }
    
    /**
     * 
     * @return
     */
    public int getVertexCount() {
        return set.getVertexAttributes().getListLength();
    }

  

}