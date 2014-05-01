package foldr.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.vecmath.Vector3d;

import de.jreality.geometry.Primitives;
import de.jreality.math.MatrixBuilder;
import de.jreality.plugin.JRViewer;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Viewer;
import de.jreality.scene.data.Attribute;
import de.jreality.util.SceneGraphUtility;
import foldr.shape.Shape;
import foldr.shape.ShapeCollection;
import foldr.shape.ShapeGroup;

/**
 *
 * 
 */
public class GUI extends JFrame implements ActionListener, MouseListener,
		MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;

	Scanner input = new Scanner(System.in);

	ShapeCollection allShapes = ShapeCollection.getInstance();

	// the main scene graph component. All other SGC's will be a child of this.
	static SceneGraphComponent scene = SceneGraphUtility
			.createFullSceneGraphComponent("scene");

	// the swing components to create the jreality frame
	protected JFrame f;
	protected JDesktopPane desktop = new JDesktopPane();

	private JPanel mainPanel, freeViewPanel, topPanel, sidePanel, frontPanel;

	// the viewer components that render the difference camera views
	JRViewer freeJRViewer, topJRViewer, sideJRViewer, frontJRViewer;
	Viewer freeViewer, topViewer, sideViewer, frontViewer;
	// the camera containers for the different cameras of the different views
	SceneGraphComponent freeCameraContainer, topCameraContainer,
			sideCameraContainer, frontCameraContainer;

	// Hold the different camera locations (hard-coded for now)
	Vector3d frontCameraLocation = new Vector3d(0, 0, 4.5);
	Vector3d sideCameraLocation = new Vector3d(7, 0, -4.5);
	Vector3d topCameraLocation = new Vector3d(0, 7, -4.5);
	Vector3d freeCameraLocation = new Vector3d(0, 0, 0);

	double freeCamRotationDegX = 0;
	double freeCamRotationDegY = 0;

	// Capture the mouse location during drag events
	Point mouseDragLocation = null;

	// the swing components to create the menu bar
	protected JPanel menuBarPane;
	protected JMenuBar menuBar;
	protected JMenu fileMenu, editMenu, foldingMenu, windowMenu, helpMenu;
	protected JMenuItem fileOpen, fileNew, fileSave, fileSaveAs, fileExport,
			fileClose;
	protected JMenuItem editCopy, editCut, editPaste, editDelete,
			editSelectAll, editResizeShape;
	protected JMenuItem foldingThirty, foldingFortyFive, foldingNinety,
			foldingCustomAngle, foldingEdgeSelect, foldingPointSelect,
			foldingFoldShapes, foldingConnectShapes, foldingDetachShapes;
	protected JMenuItem windowShowTop, windowShowBack, windowShowLeft,
			windowShowHideTools, windowShowHideInfo, windowChangePerspective,
			windowSaveLoadPerspective, windowResizePerspective;
	protected JMenuItem helpManual, helpQuickStartGuide;

	// This method creates the menu bar
	protected void initMenuBarPane() {
		menuBarPane = new JPanel();

		// File menu items
		fileNew = new JMenuItem("New");
		fileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.META_MASK));
		fileNew.addActionListener(this);
		fileOpen = new JMenuItem("Open");
		fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.META_MASK));
		fileOpen.addActionListener(this);
		fileSave = new JMenuItem("Save");
		fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.META_MASK));
		fileSave.addActionListener(this);
		fileSaveAs = new JMenuItem("Save As");
		fileSaveAs.addActionListener(this);
		fileExport = new JMenuItem("Export");
		fileExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				ActionEvent.META_MASK));
		fileExport.addActionListener(this);
		fileClose = new JMenuItem("Close");
		fileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.META_MASK));
		fileClose.addActionListener(this);

		// Edit menu items
		editCopy = new JMenuItem("Copy");
		editCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.META_MASK));
		editCopy.addActionListener(this);
		editCut = new JMenuItem("Cut");
		editCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.META_MASK));
		editCut.addActionListener(this);
		editPaste = new JMenuItem("Paste");
		editPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				ActionEvent.META_MASK));
		editPaste.addActionListener(this);
		editDelete = new JMenuItem("Delete");
		editDelete.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_BACK_SPACE, ActionEvent.META_MASK));
		editDelete.addActionListener(this);
		editSelectAll = new JMenuItem("Select All");
		editSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.META_MASK));
		editSelectAll.addActionListener(this);
		editResizeShape = new JMenuItem("Resize Shape");
		editResizeShape.addActionListener(this);
		//
		// editCopy.setEnabled(false);
		// editCut.setEnabled(false);
		// editPaste.setEnabled(false);
		// editDelete.setEnabled(false);
		// editSelectAll.setEnabled(false);
		// editResizeShape.setEnabled(false);

		// Folding/Shapes menu items
		foldingThirty = new JMenuItem("Rotate 30 Degrees");
		foldingThirty.addActionListener(this);
		foldingFortyFive = new JMenuItem("Rotate 45 Degrees");
		foldingFortyFive.addActionListener(this);
		foldingNinety = new JMenuItem("Rotate 90 Degrees");
		foldingNinety.addActionListener(this);
		foldingCustomAngle = new JMenuItem("Custom Angle");
		foldingCustomAngle.addActionListener(this);
		foldingEdgeSelect = new JMenuItem("Edge Select");
		foldingEdgeSelect.addActionListener(this);
		foldingPointSelect = new JMenuItem("Point Select");
		foldingPointSelect.addActionListener(this);
		foldingFoldShapes = new JMenuItem("Fold Shapes");
		foldingFoldShapes.addActionListener(this);
		foldingConnectShapes = new JMenuItem("Connect Shapes");
		foldingConnectShapes.addActionListener(this);
		foldingDetachShapes = new JMenuItem("Detach Shapes");
		foldingDetachShapes.addActionListener(this);

		// foldingThirty.setEnabled(false);
		// foldingFortyFive.setEnabled(false);
		// foldingNinety.setEnabled(false);
		// foldingCustomAngle.setEnabled(false);
		// foldingEdgeSelect.setEnabled(false);
		// foldingPointSelect.setEnabled(false);
		// foldingFoldShapes.setEnabled(false);
		// foldingConnectShapes.setEnabled(false);
		// foldingDetachShapes.setEnabled(false);

		// Window menu items
		windowShowTop = new JMenuItem("Show Top");
		windowShowTop.addActionListener(this);
		windowShowBack = new JMenuItem("Show Back");
		windowShowBack.addActionListener(this);
		windowShowLeft = new JMenuItem("Show Left");
		windowShowLeft.addActionListener(this);
		windowShowHideTools = new JMenuItem("Show/Hide Tools");
		windowShowHideTools.addActionListener(this);
		windowShowHideInfo = new JMenuItem("Show/Hide Information Panel");
		windowShowHideInfo.addActionListener(this);
		windowChangePerspective = new JMenuItem("Change Perspective Layout");
		windowChangePerspective.addActionListener(this);
		windowSaveLoadPerspective = new JMenuItem(
				"Save/Load Perspective Layout");
		windowSaveLoadPerspective.addActionListener(this);
		windowResizePerspective = new JMenuItem("Resize Perspective");
		windowResizePerspective.addActionListener(this);

		// Help menu items
		helpManual = new JMenuItem("Manual");
		helpManual.addActionListener(this);
		helpQuickStartGuide = new JMenuItem("Quick Start Guide");
		helpQuickStartGuide.addActionListener(this);

		// Set up the file menu
		fileMenu = new JMenu("File");
		fileMenu.add(fileNew);
		fileMenu.add(fileOpen);
		fileMenu.add(new JSeparator());
		fileMenu.add(fileSave);
		fileMenu.add(fileSaveAs);
		fileMenu.add(new JSeparator());
		fileMenu.add(fileExport);
		fileMenu.add(fileClose);

		// Set up the edit menu
		editMenu = new JMenu("Edit");
		editMenu.add(editCopy);
		editMenu.add(editCut);
		editMenu.add(editPaste);
		editMenu.add(editDelete);
		editMenu.add(new JSeparator());
		editMenu.add(editSelectAll);
		editMenu.add(editResizeShape);

		// Set up the folding/shapes menu
		foldingMenu = new JMenu("Folding/Shapes");
		foldingMenu.add(foldingThirty);
		foldingMenu.add(foldingFortyFive);
		foldingMenu.add(foldingNinety);
		foldingMenu.add(foldingCustomAngle);
		foldingMenu.add(new JSeparator());
		foldingMenu.add(foldingEdgeSelect);
		foldingMenu.add(foldingPointSelect);
		foldingMenu.add(new JSeparator());
		foldingMenu.add(foldingFoldShapes);
		foldingMenu.add(foldingConnectShapes);
		foldingMenu.add(foldingDetachShapes);

		// Set up the window menu
		windowMenu = new JMenu("Window");
		windowMenu.add(windowShowTop);
		windowMenu.add(windowShowBack);
		windowMenu.add(windowShowLeft);
		windowMenu.add(new JSeparator());
		windowMenu.add(windowShowHideTools);
		windowMenu.add(windowShowHideInfo);
		windowMenu.add(new JSeparator());
		windowMenu.add(windowChangePerspective);
		windowMenu.add(windowSaveLoadPerspective);
		windowMenu.add(windowResizePerspective);

		// Set up the help menu
		helpMenu = new JMenu("Help");
		helpMenu.add(helpManual);
		helpMenu.add(helpQuickStartGuide);

		// Create the menuBar to contain the menus
		menuBar = new JMenuBar();

		// Add the menus to the bar
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(foldingMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);

		// Add the menu bar to the appropriate pane
		menuBarPane.setLayout(new GridLayout(1, 1));
		menuBarPane.add(menuBar);
	}

	// Action listener. For now, this method is just a placeholder.
	public void actionPerformed(ActionEvent e) {
		String theCommand = e.getActionCommand();
		System.out.println("You clicked " + theCommand);
		if (theCommand.equals("Connect Shapes")) {

			// TODO figure out how to do this by clicking on the shapes!
			// use the console to grab the shapes and vertices to connect
			// the number of a shape is its index number where it's stored in
			// ShapeCollection
			System.out.println("Enter the number of the first shape");
			int firstShape = input.nextInt();
			System.out.println("Enter the number of the vertex");
			int firstVertex = input.nextInt();
			System.out.println("Enter the number of the second shape");
			int secondShape = input.nextInt();
			System.out.println("Enter the number of the vertex");
			int secondVertex = input.nextInt();

			// grab the 2 shapes the user inputed
			Shape shapeOne = allShapes.getShapeFromCollection(firstShape);
			Shape shapeTwo = allShapes.getShapeFromCollection(secondShape);

			System.out.println("Connecting...");

			// animate it
			connectTwoShapes(shapeOne, firstVertex, shapeTwo, secondVertex);

		} else {
			System.out.println("The command is not yet implemented!");
		}
	}

	/**
	 * Animates connecting two shapes at a vertex. The entire ShapeGroup is
	 * moved as if it is one shape.
	 * 
	 * @param shapeOne
	 * @param vertexOne
	 * @param shapeTwo
	 * @param vertexTwo
	 */
	public void connectTwoShapes(Shape shapeOne, int vertexOne, Shape shapeTwo,
			int vertexTwo) {

		// store original coordinates for error calculation
		double originalX = shapeOne.getShapeSGC().getTransformation()
				.getMatrix()[3];
		double originalY = shapeOne.getShapeSGC().getTransformation()
				.getMatrix()[7];
		double originalZ = shapeOne.getShapeSGC().getTransformation()
				.getMatrix()[11];

		// figure out how much to move the first shape in each direction
		double targetX = shapeTwo.getCurrentVertexCoordinates(vertexTwo)[0]
				- shapeOne.getCurrentVertexCoordinates(vertexOne)[0];
		double targetY = shapeTwo.getCurrentVertexCoordinates(vertexTwo)[1]
				- shapeOne.getCurrentVertexCoordinates(vertexOne)[1];
		double targetZ = shapeTwo.getCurrentVertexCoordinates(vertexTwo)[2]
				- shapeOne.getCurrentVertexCoordinates(vertexOne)[2];

		// set the target coordinates
		double[] endPoint = new double[3];
		endPoint[0] = targetX;
		endPoint[1] = targetY;
		endPoint[2] = targetZ;

		// animate all shapes in the group
		ShapeGroup shapeGroupToMove = shapeOne.getGroup();
		shapeGroupToMove.animateGroup(endPoint);
		// put the newly glued shapes into the same group
		shapeGroupToMove.resetGroup(shapeTwo.getGroup());

		// print out error
		double errorX = shapeOne.getShapeSGC().getTransformation().getMatrix()[3]
				- (originalX + targetX);
		double errorY = shapeOne.getShapeSGC().getTransformation().getMatrix()[7]
				- (originalY + targetY);
		double errorZ = shapeOne.getShapeSGC().getTransformation().getMatrix()[11]
				- (originalZ + targetZ);
		// System.out.println("X error: " + errorX + ", Y error: " + errorY +
		// ", Z error: " + errorZ);

	}

	// for now, assumes these are edges that already share one vertex
	public double[][] foldTwoEdges(Shape shapeToRotate, double[] vertexOne,
			double[] vertexTwo, int vertexToCheck) {
		
		// make the array of possible end coordinates for a vertex by pivoting
		// it in a full circle

		int size = 360;
		double amountToRotate = (2*Math.PI) / (double) size;
		double[][] possibleValues = new double[size][];
		for (int i = 0; i < size; i++) {
			// TODO figure out to get the right vertex to check...
			//store the current coordinates of the vertex
			possibleValues[i] = shapeToRotate.getCurrentVertexCoordinates(vertexToCheck);
			/*System.out.println("x: " + shapeToRotate.getCurrentVertexCoordinates(vertexToCheck)[0]);
			System.out.println("y: " + shapeToRotate.getCurrentVertexCoordinates(vertexToCheck)[1]);
			System.out.println("z: " + shapeToRotate.getCurrentVertexCoordinates(vertexToCheck)[2]);
*/			//incrementally rotate both shapes
			MatrixBuilder.euclidean()
					.rotate(vertexOne, vertexTwo, i*amountToRotate).translate(shapeToRotate.translationTransformations[0], shapeToRotate.translationTransformations[1], shapeToRotate.translationTransformations[2])
					.assignTo(shapeToRotate.shapeSGC);
		}
		
		return possibleValues;
	}

	// Create the jReality viewers for each panel
	public void createJRViewers() {
		// TESTING with a visible shape @TODO: Remove this.
		/*
		 * IndexedFaceSet octo = Primitives.regularPolygon(8);
		 * SceneGraphComponent octoOne = SceneGraphUtility
		 * .createFullSceneGraphComponent("octogon1");
		 * octoOne.setGeometry(octo); scene.addChild(octoOne);
		 */

		Shape shapeOne = new Shape(4, scene);
		Shape shapeTwo = new Shape(4, scene);
		Shape shapeThree = new Shape(4, scene);
		//Shape shapeThree = new Shape(5, scene);

		shapeTwo.translate(0, 1.4, 0);
		shapeThree.translate(1.4, 0, 0);
		// shapeThree.translate(-1, -2, 0);

		
		//shapeTwo.translate(0, 1.4, 0);
		/*double[][] tstOne = foldTwoEdges(shapeTwo, shapeTwo.getCurrentVertexCoordinates(2), shapeTwo.getCurrentVertexCoordinates(3), 0);
		double[][] tstTwo = foldTwoEdges(shapeThree, shapeThree.getCurrentVertexCoordinates(1), shapeThree.getCurrentVertexCoordinates(2), 0);
		double error = .000001;
		double[] goalCoordinatesOne = {1,2,3};
		double[] goalCoordinatesTwo = {1,2,3};
		for(double[] coordinateSetOne : tstOne) {
			for (double[] coordinateSetTwo : tstTwo) {
				if (Math.abs(coordinateSetOne[0] - coordinateSetTwo[1]) < error && Math.abs(coordinateSetOne[1] - coordinateSetTwo[0]) < error && Math.abs(coordinateSetOne[2] - coordinateSetTwo[2]) < error) {
					goalCoordinatesOne = coordinateSetOne;
					goalCoordinatesTwo = coordinateSetTwo;
					System.out.println("found one!");
					System.out.println("x: " + Math.abs(coordinateSetOne[0] - coordinateSetTwo[1]));
					System.out.println("y: " +  Math.abs(coordinateSetOne[1] - coordinateSetTwo[0]));
					System.out.println("z: " +  Math.abs(coordinateSetOne[2] - coordinateSetTwo[2]));
				}
			}
		}
		
		System.out.println("x: " + goalCoordinatesOne[0]);
		System.out.println("y: " + goalCoordinatesOne[1]);
		System.out.println("z: " + goalCoordinatesOne[2]);
		
		shapeTwo.animateEdgeRotation(shapeTwo.getCurrentVertexCoordinates(2), shapeTwo.getCurrentVertexCoordinates(3), goalCoordinatesOne, 0);
*/
		double[] goalCoordinatesOne = {0.7071067811865475, 1.4, 0.7071067811865476};
		double[] goalCoordinatesTwo = {1.4, 0.7071067811865476, 0.7071067811865476};
		shapeTwo.animateEdgeRotation(shapeTwo.getCurrentVertexCoordinates(2), shapeTwo.getCurrentVertexCoordinates(3), goalCoordinatesOne, 0);
		shapeThree.animateEdgeRotation(shapeThree.getCurrentVertexCoordinates(1), shapeThree.getCurrentVertexCoordinates(2), goalCoordinatesTwo, 0);

		// Setting up the free view
		freeJRViewer = new JRViewer();
		freeJRViewer.setContent(scene);
		freeJRViewer.startupLocal();
		freeViewer = freeJRViewer.getViewer();
		freeCameraContainer = (SceneGraphComponent) freeViewer.getCameraPath()
				.get(freeViewer.getCameraPath().getLength() - 2);
		freeViewPanel.setLayout(new GridLayout());
		freeViewPanel.add((Component) freeViewer.getViewingComponent());
		freeViewPanel.setVisible(true);
		freeViewPanel.getComponent(0).addMouseMotionListener(this);
		freeViewPanel.getComponent(0).addMouseListener(this);
		freeViewPanel.getComponent(0).addMouseWheelListener(this);
		freeViewPanel.getComponent(0).setName("freeViewPanel");

		// Setting up the top view
		topJRViewer = new JRViewer();
		topJRViewer.setContent(scene);
		topJRViewer.startupLocal();
		topViewer = topJRViewer.getViewer();
		topCameraContainer = (SceneGraphComponent) topViewer.getCameraPath()
				.get(topViewer.getCameraPath().getLength() - 2);
		topPanel.setLayout(new GridLayout());
		topPanel.add((Component) topViewer.getViewingComponent());
		topPanel.setVisible(true);
		topPanel.getComponent(0).addMouseMotionListener(this);
		topPanel.getComponent(0).addMouseListener(this);
		topPanel.getComponent(0).addMouseWheelListener(this);
		topPanel.getComponent(0).setName("topPanel");

		// Setting up the side view
		sideJRViewer = new JRViewer();
		sideJRViewer.setContent(scene);
		sideJRViewer.startupLocal();
		sideViewer = sideJRViewer.getViewer();
		sideCameraContainer = (SceneGraphComponent) sideViewer.getCameraPath()
				.get(sideViewer.getCameraPath().getLength() - 2);
		sidePanel.setLayout(new GridLayout());
		sidePanel.add((Component) sideViewer.getViewingComponent());
		sidePanel.setVisible(true);
		sidePanel.getComponent(0).addMouseMotionListener(this);
		sidePanel.getComponent(0).addMouseListener(this);
		sidePanel.getComponent(0).addMouseWheelListener(this);
		sidePanel.getComponent(0).setName("sidePanel");

		// Setting up the front view
		frontJRViewer = new JRViewer();
		frontJRViewer.setContent(scene);
		frontJRViewer.startupLocal();
		frontViewer = frontJRViewer.getViewer();
		frontCameraContainer = (SceneGraphComponent) frontViewer
				.getCameraPath().get(
						frontViewer.getCameraPath().getLength() - 2);
		frontPanel.setLayout(new GridLayout());
		frontPanel.add((Component) frontViewer.getViewingComponent());
		frontPanel.setVisible(true);
		frontPanel.getComponent(0).addMouseMotionListener(this);
		frontPanel.getComponent(0).addMouseListener(this);
		frontPanel.getComponent(0).addMouseWheelListener(this);
		frontPanel.getComponent(0).setName("frontPanel");

		// Setting the initial camera positions
		MatrixBuilder
				.euclidean()
				.translate(frontCameraLocation.x, frontCameraLocation.y,
						frontCameraLocation.z).assignTo(frontCameraContainer);
		MatrixBuilder
				.euclidean()
				.translate(sideCameraLocation.x, sideCameraLocation.y,
						sideCameraLocation.z).rotateY(Math.toRadians(90))
				.assignTo(sideCameraContainer);
		MatrixBuilder
				.euclidean()
				.translate(topCameraLocation.x, topCameraLocation.y,
						topCameraLocation.z).rotateX(Math.toRadians(-90))
				.assignTo(topCameraContainer);

	}

	// Create the panes, panels and other gui elements and pack them up.
	public void initPanesAndGui() {
		// Adding the view panels (free, top, side, front)
		GridLayout gl = new GridLayout(2, 2);
		mainPanel = new JPanel(gl, true);
		mainPanel.setBackground(new Color(128, 128, 64));
		freeViewPanel = new JPanel();
		mainPanel.add(freeViewPanel);
		topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		mainPanel.add(topPanel);
		sidePanel = new JPanel();
		sidePanel.setBackground(Color.WHITE);
		mainPanel.add(sidePanel);
		frontPanel = new JPanel();
		frontPanel.setBackground(Color.GRAY);
		mainPanel.add(frontPanel);
		mainPanel.addMouseListener(this);

		// Adding border
		freeViewPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		topPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		sidePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		frontPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		createJRViewers();
		initMenuBarPane();

		// stick them both in a desktop pane
		desktop.setLayout(new BorderLayout());
		desktop.add(menuBarPane, "North");
		desktop.add(mainPanel);
		pack();

		// Create the top frame to store desktop
		f = new JFrame("Polyhedra");
		f.setLayout(new GridLayout());
		f.add(desktop);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(1000, 700);
		f.setVisible(true);
	}

	// So we can run quickly test without having to start from Driver.java
	private static GUI theProgram;

	public static void main(String[] args) {
		theProgram = new GUI();
		theProgram.initPanesAndGui();
		theProgram.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// System.out.println("Mouse Clicked: " + arg0.toString());
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// System.out.println("Mouse Entered: " + arg0.toString());

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// System.out.println("Mouse Exited: " + arg0.toString());

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// System.out.println("Mouse Pressed: " + arg0.toString());
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Reset the previous mouse location between drags, so we're only
		// recording drag position
		mouseDragLocation = null;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (mouseDragLocation == null) {
			mouseDragLocation = new Point(e.getX(), e.getY());
		} else {
			// Handling the event depending on which panel the even originated
			// from.
			if (e.getComponent().getParent().getParent().getName()
					.equals("topPanel")) {
				double xDiff = e.getX() - mouseDragLocation.x;
				double yDiff = e.getY() - mouseDragLocation.y;
				topCameraLocation
						.set(topCameraLocation.x + xDiff / -100,
								topCameraLocation.y, topCameraLocation.z
										+ yDiff / -100);
				MatrixBuilder
						.euclidean()
						.translate(topCameraLocation.x, topCameraLocation.y,
								topCameraLocation.z)
						.rotateX(Math.toRadians(-90))
						.assignTo(topCameraContainer);
				mouseDragLocation.x = e.getX();
				mouseDragLocation.y = e.getY();
			} else if (e.getComponent().getParent().getParent().getName()
					.equals("sidePanel")) {
				double xDiff = e.getX() - mouseDragLocation.x;
				double yDiff = e.getY() - mouseDragLocation.y;
				sideCameraLocation.set(sideCameraLocation.x,
						sideCameraLocation.y + yDiff / 100,
						sideCameraLocation.z + xDiff / 100);
				MatrixBuilder
						.euclidean()
						.translate(sideCameraLocation.x, sideCameraLocation.y,
								sideCameraLocation.z)
						.rotateY(Math.toRadians(90))
						.assignTo(sideCameraContainer);
				mouseDragLocation.x = e.getX();
				mouseDragLocation.y = e.getY();
			} else if (e.getComponent().getParent().getParent().getName()
					.equals("frontPanel")) {
				double xDiff = e.getX() - mouseDragLocation.x;
				double yDiff = e.getY() - mouseDragLocation.y;
				frontCameraLocation.set(frontCameraLocation.x + xDiff / -100,
						frontCameraLocation.y + yDiff / 100,
						frontCameraLocation.z);
				MatrixBuilder
						.euclidean()
						.translate(frontCameraLocation.x,
								frontCameraLocation.y, frontCameraLocation.z)
						.assignTo(frontCameraContainer);
				mouseDragLocation.x = e.getX();
				mouseDragLocation.y = e.getY();
			} else if (e.getComponent().getParent().getParent().getName()
					.equals("freeViewPanel")) {
				double xDiff = e.getX() - mouseDragLocation.x;
				double yDiff = e.getY() - mouseDragLocation.y;
				freeCamRotationDegX = freeCamRotationDegX + xDiff / 4;
				freeCamRotationDegY = freeCamRotationDegY + yDiff / 4;
				MatrixBuilder
						.euclidean()
						.translate(-freeCameraLocation.x,
								-freeCameraLocation.y, -freeCameraLocation.z)
						.rotateX(Math.toRadians(-freeCamRotationDegY))
						.rotateY(Math.toRadians(-freeCamRotationDegX))
						.conjugateBy(
								MatrixBuilder
										.euclidean()
										.translate(freeCameraLocation.x,
												freeCameraLocation.y,
												freeCameraLocation.z - 4.5)
										.getMatrix().getArray())
						.assignTo(freeCameraContainer);
				mouseDragLocation.x = e.getX();
				mouseDragLocation.y = e.getY();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// System.out.println("Mouse Moved: " + e.toString());
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double amountZoom = e.getWheelRotation();
		if (e.getComponent().getParent().getParent().getName()
				.equals("topPanel")) {
			topCameraLocation.set(topCameraLocation.x, topCameraLocation.y
					+ amountZoom / 20, topCameraLocation.z);
			MatrixBuilder
					.euclidean()
					.translate(topCameraLocation.x, topCameraLocation.y,
							topCameraLocation.z).rotateX(Math.toRadians(-90))
					.assignTo(topCameraContainer);
		} else if (e.getComponent().getParent().getParent().getName()
				.equals("sidePanel")) {
			sideCameraLocation.set(sideCameraLocation.x + amountZoom / 20,
					sideCameraLocation.y, sideCameraLocation.z);
			MatrixBuilder
					.euclidean()
					.translate(sideCameraLocation.x, sideCameraLocation.y,
							sideCameraLocation.z).rotateY(Math.toRadians(90))
					.assignTo(sideCameraContainer);
		} else if (e.getComponent().getParent().getParent().getName()
				.equals("frontPanel")) {
			frontCameraLocation.set(frontCameraLocation.x,
					frontCameraLocation.y, frontCameraLocation.z + amountZoom
							/ 20);
			MatrixBuilder
					.euclidean()
					.translate(frontCameraLocation.x, frontCameraLocation.y,
							frontCameraLocation.z)
					.assignTo(frontCameraContainer);
		} else if (e.getComponent().getParent().getParent().getName()
				.equals("freeViewPanel")) {
			freeCameraLocation.set(freeCameraLocation.x, freeCameraLocation.y,
					freeCameraLocation.z + amountZoom / 20);
			MatrixBuilder
					.euclidean()
					.translate(-freeCameraLocation.x, -freeCameraLocation.y,
							-freeCameraLocation.z)
					.rotateX(Math.toRadians(-freeCamRotationDegY))
					.rotateY(Math.toRadians(-freeCamRotationDegX))
					.conjugateBy(
							MatrixBuilder
									.euclidean()
									.translate(freeCameraLocation.x,
											freeCameraLocation.y,
											freeCameraLocation.z - 4.5)
									.getMatrix().getArray())
					.assignTo(freeCameraContainer);
		}

	}

}
