package JavaFX;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import Core.*;

/**
 * This class implements a basic drawing environment in which the user can
 * create their own custom profile image
 * 
 * @author Sushil Kumar
 * @version 1.0
 */
public class AvatarCreator extends Application {
	// The dimensions of the window
	private static final int WINDOW_WIDTH = 600;
	private static final int WINDOW_HEIGHT = 450;

	// The dimensions of the canvas
	private static final int CANVAS_WIDTH = 500;
	private static final int CANVAS_HEIGHT = 400;

	private static final Color CANVAS_COLOR = Color.LIGHTGRAY;
	private static final int DEFAULT_THICKNESS = 6;

	private Label zoomFactor;
	private BorderPane mainPane;
	private Canvas canvas;
	private GraphicsContext gc;
	private Color drawColor;
	private Scene scene;
	private ColorPicker colorPicker;
	private int drawLineThickness = DEFAULT_THICKNESS;
	private boolean eraserSelected = false;
	private boolean pencilSelected = true; // by default pencil is "set"
	private boolean lineShapeSelected = false;

	// Coordinates of start point of a line (to be drawn on canvas)
	private double currentLineX = 0;
	private double currentLineY = 0;

	// Path to Icons folder
	String iconsPath = "file:images/Icons/";
	// private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Profile Image Creator");

		primaryStage.getIcons().add(new Image(iconsPath + "avatar.png"));

		// Build the GUI
		try {
			Pane rootPane = buildRootPane();
			// Create a scene from the GUI
			scene = new Scene(rootPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		// Display the scene on the stage
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// only used for testing purposes
	public static void main(String[] args) throws SQLException {
		Datastore.init("jdbc:mysql://localhost:3306/tawe_lib", "root", "");
		Datastore.logIn("TestUser");
		launch(args);
	}

	/**
	 * Creates a border pane containing GUI elements
	 * 
	 * @return mainPane The root pane with GUI elements added
	 * @throws SQLException if SQL errors.
	 */
	private Pane buildRootPane() throws SQLException {
		mainPane = new BorderPane();

		canvas = addCanvas();
		mainPane.setCenter(canvas);

		HBox upperPane = addHbox();
		mainPane.setTop(upperPane);

		GridPane rightPane = addGridPane();
		mainPane.setRight(rightPane);

		return mainPane;
	}

	/**
	 * Implements a working canvas
	 * 
	 * @return canvas The canvas on which user can draw
	 */
	private Canvas addCanvas() {
		Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(CANVAS_COLOR);
		gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

		canvas.setOnMouseEntered(event -> {
			scene.setCursor(Cursor.CROSSHAIR);
			if (eraserSelected) {
				Image eraserCursor =
					new Image(iconsPath + "canvasEraserIcon.png");
				scene.setCursor(new ImageCursor(eraserCursor));
			}
			if (pencilSelected || lineShapeSelected) {
				scene.setCursor(Cursor.CROSSHAIR);
			}
		});

		canvas.setOnMouseExited(event -> {
			scene.setCursor(Cursor.DEFAULT);
		});

		canvas.setOnMouseDragged((event) -> {
			gc.setStroke(drawColor);
			gc.setLineWidth(drawLineThickness);
			if (!lineShapeSelected) {
				// Draw ovals on mouse drag
				gc.strokeOval(event.getX(), event.getY(), 1, 1);
			}
			// gc.strokeLine(event.getX(), event.getY(), 10, 10); // for fun
			// only
		});
		return canvas;
	}

	/**
	 * Creates a grid pane to hold the slider
	 * 
	 * @return gridPane The grid pane containing zoom slider and zoom factor
	 */
	private GridPane addGridPane() {
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(15, 15, 15, 15));
		gridPane.setOnMouseEntered(event -> {
			scene.setCursor(Cursor.OPEN_HAND);
		});

		Slider zoomSlider = addZoomSlider();
		zoomFactor = new Label(Double.toString(zoomSlider.getValue()));
		gridPane.add(zoomSlider, 0, 0);
		gridPane.add(zoomFactor, 1, 0);
		// gridPane.setGridLinesVisible(true);
		return gridPane;
	}

	/**
	 * Adds a functional slider to be used to zoom the image on canvas
	 * 
	 * @return zoomSlider A slider allowing user to zoom on the canvas
	 */
	private Slider addZoomSlider() {
		Slider zoomSlider = new Slider();
		zoomSlider.setOrientation(Orientation.VERTICAL);
		zoomSlider.setMinHeight(350);
		zoomSlider.setMin(1);
		zoomSlider.setMax(5);
		zoomSlider.setValue(1);
		zoomSlider.setShowTickLabels(true);
		zoomSlider.setShowTickMarks(true);
		zoomSlider.setBlockIncrement(1);
		zoomSlider.setMajorTickUnit(1);
		zoomSlider.setTooltip(new Tooltip("Slide to zoom"));

		// Event Listener for slider
		zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
				Number oldValue, Number newValue) {
				canvas.setScaleX(newValue.doubleValue());
				canvas.setScaleY(newValue.doubleValue());
				if (newValue.doubleValue() <= 2.5) {
					zoomFactor.setTextFill(Color.GREEN);
				} else if (newValue.doubleValue() <= 4.0) {
					zoomFactor.setTextFill(Color.DARKORANGE);
				} else {
					zoomFactor.setTextFill(Color.RED);
				}
				zoomFactor.setText(String.format("%.2f", newValue));
			}
		});

		return zoomSlider;
	}

	/**
	 * Creates a HBox container node
	 * 
	 * @return hbox A hbox containing a tool bar
	 * @throws SQLException if SQL errors.
	 */
	private HBox addHbox() throws SQLException {
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(3, 0, 3, 0));
		// hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().add(addToolBar());

		return hbox;
	}

	/**
	 * Creates a tool bar
	 * 
	 * @return toolBar A tool bar containing various control components
	 * @throws SQLException if SQL errors.
	 */
	private ToolBar addToolBar() throws SQLException {
		ToolBar toolBar = new ToolBar();

		toolBar.setOnMouseEntered(event -> {
			scene.setCursor(Cursor.HAND);
		});

		toolBar.setOnMouseExited(event -> {
			scene.setCursor(Cursor.CROSSHAIR);
		});

		Button saveButton = addSaveButton();
		colorPicker = addColorPicker();
		MenuButton drawingToolMenuButton = addDrawingToolMenuButton();
		ComboBox<String> comboBox = addComboBox();
		MenuButton optionsMenuButton = addOptionsMenuButton();
		MenuButton shapesMenuButton = addShapesMenuButton();

		toolBar.getItems().addAll(saveButton, new Separator(), colorPicker,
			new Separator(), drawingToolMenuButton, new Separator(), comboBox,
			new Separator(), shapesMenuButton, new Separator(),
			optionsMenuButton);

		return toolBar;
	}

	/**
	 * Adds a save button to tool bar
	 * 
	 * @return saveButton The save Button
	 * @throws SQLException if SQL errors
	 */
	private Button addSaveButton() throws SQLException {
		Button saveButton = new Button("", addImage("saveIcon.jpg", 18, 18));
		saveButton.setPrefSize(45, 25);
		// can modify to return file path of saved image
		saveImageOnClick(saveButton);

		Tooltip buttonToolTip = new Tooltip("Save image");
		saveButton.setTooltip(buttonToolTip);

		return saveButton;
	}

	/**
	 * Adds a colour picker component to choose draw colour
	 * 
	 * @return colorPicker A colour picker tool
	 */
	private ColorPicker addColorPicker() {
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.getStyleClass().add("split-button");
		colorPicker.setPrefWidth(100);
		colorPicker.setValue(Color.BLACK);
		Tooltip colorToolTip = new Tooltip("Choose fill color");
		colorPicker.setTooltip(colorToolTip);

		colorPicker.setOnAction(event -> {
			drawColor = colorPicker.getValue();
		});

		return colorPicker;
	}

	/**
	 * Adds a menu button to choose pencil or eraser as tool
	 * 
	 * @return drawingToolMenuButton a menu button having pencil and eraser as
	 *         options
	 */
	private MenuButton addDrawingToolMenuButton() {
		MenuItem pencilItem = new MenuItem("Pencil");
		pencilItem.setGraphic(addImage("pencilIcon.png", 15, 15));

		MenuItem eraserItem = new MenuItem("Eraser");
		eraserItem.setGraphic(addImage("eraserIcon.png", 15, 15));

		MenuButton drawingToolMenuButton = new MenuButton("Drawing Tool",
			addImage("drawIcon.png", 18, 18), pencilItem, eraserItem);

		pencilItem.setOnAction(event -> {
			drawColor = colorPicker.getValue();
			pencilSelected = true;
			eraserSelected = false;
			lineShapeSelected = false;
		});

		eraserItem.setOnAction(event -> {
			drawColor = CANVAS_COLOR;
			eraserSelected = true;
			pencilSelected = false;
			lineShapeSelected = false;
		});

		Tooltip primaryToolTip = new Tooltip("Choose drawing tool");
		drawingToolMenuButton.setTooltip(primaryToolTip);

		return drawingToolMenuButton;
	}

	/**
	 * Adds a combo box to choose line thickness
	 * 
	 * @return comboBox A combo box with preset options
	 */
	private ComboBox<String> addComboBox() {
		ComboBox<String> comboBox = new ComboBox<String>();
		comboBox.setEditable(true);
		comboBox.setPrefWidth(55);
		comboBox.setPromptText("6");
		comboBox.getItems().add("1");
		comboBox.getItems().add("2");
		comboBox.getItems().add("4");
		comboBox.getItems().add("6");
		comboBox.getItems().add("8");
		comboBox.getItems().add("10");

		comboBox.setOnAction(event -> {
			drawLineThickness = Integer.parseInt(comboBox.getValue());
		});

		Tooltip thicknessTooltip = new Tooltip("Choose line thickness");
		comboBox.setTooltip(thicknessTooltip);

		return comboBox;
	}

	/**
	 * Adds a menu button providing different options
	 * 
	 * @return optionsMenuButton
	 */
	private MenuButton addOptionsMenuButton() {
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setGraphic(addImage("exitIcon.png", 17, 17));

		MenuItem clearScreenItem = new MenuItem("Clear Screen");
		clearScreenItem.setGraphic(addImage("clearScreenIcon.png", 17, 17));

		MenuItem rotateItem = new MenuItem("Rotate 90");
		rotateItem.setGraphic(addImage("rotateIcon.png", 17, 17));

		MenuButton optionsMenuButton =
			new MenuButton("Options", addImage("optionsIcon.png", 18, 18),
				clearScreenItem, rotateItem, exitItem);

		exitItem.setOnAction(event -> {
			Platform.exit();
		});

		clearScreenItem.setOnAction(event -> {
			gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		});

		rotateItem.setOnAction(event -> {
			SnapshotParameters parameters = new SnapshotParameters();
			parameters.setDepthBuffer(true);
			Image canvasContent = canvas.snapshot(parameters, null);
			gc.save(); // Save default transform
			Affine rotate = new Affine();
			rotate.appendRotation(90, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2);
			gc.setTransform(rotate);
			gc.drawImage(canvasContent, 0, 0);
			gc.restore(); // Restore default transform
		});

		Tooltip optionsToolTip = new Tooltip("View Options");
		optionsMenuButton.setTooltip(optionsToolTip);

		return optionsMenuButton;
	}

	/**
	 * Adds a menu button to select shapes
	 * 
	 * @return shapesMenuButton A menu button allowing user to draw line shape
	 */
	private MenuButton addShapesMenuButton() {
		MenuItem lineItem = new MenuItem("Line");
		lineItem.setGraphic(addImage("lineIcon.png", 15, 15));

		MenuButton shapesMenuButton = new MenuButton("Shapes",
			addImage("shapesIcon.png", 18, 18), lineItem);

		lineItem.setOnAction(event -> {
			lineShapeSelected = true;
			// When the mouse is pressed, get the position to start the line
			canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					public void handle(MouseEvent e) {
						if (lineShapeSelected) {
							// start current line
							currentLineX = e.getX();
							currentLineY = e.getY();
						}
					}
				});
			/*
			 * When the mouse is released, draw the line from the position
			 * earlier to the current mouse position
			 */
			canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
				new EventHandler<MouseEvent>() {
					public void handle(MouseEvent e) {
						if (lineShapeSelected) {
							gc.strokeLine(currentLineX, currentLineY, e.getX(),
								e.getY());
						}
					}
				});
		});

		Tooltip shapesToolTip = new Tooltip("Choose shape");
		shapesMenuButton.setTooltip(shapesToolTip);

		return shapesMenuButton;
	}

	/**
	 * Method to save image the image drawn on canvas upon clicking a control
	 * component
	 * 
	 * @param saveButton The button component
	 * @throws NullPointerException if null pointer
	 */
	private void saveImageOnClick(Button saveButton)
		throws NullPointerException {
		saveButton.setOnMousePressed(event -> {
			String customImagePath = null;

			try {
				customImagePath = Datastore.getCurrentUser()
					.generateAndSetCustomProfileImage("png");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			File file = new File(customImagePath);

			WritableImage writableImage =
				new WritableImage(CANVAS_WIDTH, CANVAS_HEIGHT);
			canvas.snapshot(null, writableImage);
			RenderedImage renderedImage =
				SwingFXUtils.fromFXImage(writableImage, null);
			try {
				ImageIO.write(renderedImage, "png", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	/**
	 * Method to add an image to a node
	 * 
	 * @param imageName   Name of the image
	 * @param imageWidth  width of the image in pixels
	 * @param imageHeight height of the image in pixels
	 * @return imageView An imageView containing an image
	 */
	private ImageView addImage(String imageName, int imageWidth,
		int imageHeight) {
		Image image = new Image(iconsPath + imageName);
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(imageWidth);
		imageView.setFitHeight(imageHeight);
		imageView.setPreserveRatio(true);

		return imageView;
	}
}
