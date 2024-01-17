package JavaFX;

import java.sql.SQLException;
import java.time.LocalDate;

import Core.Datastore;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Displays statistics about popular Resources to the librarian.
 * 
 * @author Ben Kennard 965798
 */
public class LibrarianPopularResources extends Application {

	// The dimensions of the window
	private static final int WINDOW_WIDTH = 1200;
	private static final int WINDOW_HEIGHT = 450;
	// Spacing for control elements
	private static final int CONTROL_SPACING = 5;
	private static final int CONTROL_PADDING = 10;
	// Control elements
	private DatePicker from;
	private DatePicker to;
	private ComboBox<String> resourceTypeBox;
	private Spinner<Integer> limit;
	// Limit defaults
	private static final int MIN_LIMIT = 1;
	private static final int MAX_LIMIT = 9999;
	private static final int DEFAULT_LIMIT = 100;
	// Limit size
	private static final int LIMIT_WIDTH = 70;
	// The statistics bar chart
	private BarChart<String, Number> chart;
	// The statistics table
	private TableView<Data<String, Number>> dataTable;
	// The error message Label
	private Label successMessage;

	@Override
	/**
	 * Starts the application.
	 */
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Resource Popularity Statistics");
		Pane mainPane = buildRootPane();
		Scene mainScene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		// Display the scene on the stage
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	/**
	 * Builds the pane which the scene is comprised of.
	 * 
	 * @return the main pane for this application.
	 */
	private Pane buildRootPane() {
		// Create the main pane
		VBox mainPane = new VBox();
		// Create the graph pane
		BorderPane graphPane = new BorderPane();
		// Create bottom controls
		// Initialise HBox
		HBox controls = new HBox(CONTROL_SPACING);
		controls.setPadding(new Insets(CONTROL_PADDING));
		controls.setAlignment(Pos.CENTER);

		// Label to print error messages
		this.successMessage = new Label();

		// From date
		Label fromLabel = new Label("From:");
		this.from = new DatePicker(LocalDate.now().minusYears(1));

		// To date
		Label toLabel = new Label("To:");
		this.to = new DatePicker(LocalDate.now());

		// Combobox for default date ranges
		Label defaultDateLabel = new Label("Show popularity over the:");
		ComboBox<String> defaultDateRanges =
			new ComboBox<String>(FXCollections.observableArrayList("Past Week",
				"Past Month", "Past Year", "All Time"));
		// Set default
		defaultDateRanges.setValue("Past Year");
		// Set change behaviour
		defaultDateRanges.setOnAction(e -> {
			this.changeDateRange(defaultDateRanges.getValue());
		});

		// Pane for date controls
		GridPane dateControls = new GridPane();
		dateControls.setHgap(CONTROL_SPACING);
		dateControls.setVgap(CONTROL_SPACING);
		dateControls.setAlignment(Pos.CENTER);
		// Add controls
		dateControls.addRow(0, fromLabel, this.from, toLabel, this.to);
		dateControls.addRow(1, defaultDateLabel, defaultDateRanges);

		// Combobox to select resource type
		Label resourceTypeLabel = new Label("Type of Resource:");
		this.resourceTypeBox =
			new ComboBox<String>(FXCollections.observableArrayList("Any",
				"Books", "DVDs", "LaptopComputers", "VideoGames"));
		// default resource type is any
		this.resourceTypeBox.setValue("Any");

		// Button to update the bar chart and table
		Button updateChart = new Button("Update Chart");
		// Update chart and table on button press
		updateChart.setOnAction(e -> {
			try {
				this.chart = this.updateChart(this.chart);
			} catch (SQLException e1) {
				this.setErrorLabel(false, e1.getMessage());
			}
		});

		// Spinner to limit number of resources shown
		Label limitLabel = new Label("Max Number of Resources to Display:");
		this.limit = new Spinner<Integer>(MIN_LIMIT, MAX_LIMIT, DEFAULT_LIMIT);
		this.limit.setPrefWidth(LIMIT_WIDTH);
		this.limit.setEditable(true);

		controls.getChildren().addAll(dateControls, resourceTypeLabel,
			this.resourceTypeBox, limitLabel, this.limit, updateChart);
		// Add controls to main pane
		graphPane.setBottom(controls);

		// Create the data table
		this.dataTable = new TableView<Data<String, Number>>();

		// Create the statistics bar chart (also sets table data)
		try {
			this.chart = buildBarChart();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.setErrorLabel(false, e.getMessage());
		}
		// Set bar chart as centre element
		graphPane.setCenter(this.chart);

		// Set up the table's tableColumns
		TableColumn<Data<String, Number>, String> tc1 =
			new TableColumn<Data<String, Number>, String>("Resource Title");
		tc1.setCellValueFactory(data -> {
			return data.getValue().XValueProperty();
		});
		TableColumn<Data<String, Number>, Number> tc2 =
			new TableColumn<Data<String, Number>, Number>("Number of Borrows");
		tc2.setCellValueFactory(data -> {
			return data.getValue().YValueProperty();
		});

		this.dataTable.getColumns().addAll(tc1, tc2);

		mainPane.getChildren().addAll(graphPane, this.successMessage,
			this.dataTable);

		return mainPane;
	}

	/**
	 * Builds the bar chart, which displays the number of borrows per resource.
	 * The bar chart is initialised with all resource types' data.
	 * 
	 * @return the bar chart.
	 * @throws SQLException if SQL errors.
	 */
	private BarChart<String, Number> buildBarChart() throws SQLException {
		// Generate chart
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		BarChart<String, Number> chart =
			new BarChart<String, Number>(xAxis, yAxis);
		// Unset animated
		chart.setAnimated(false);
		// Set the bar gap to 0, as we're only plotting one series at a time
		chart.setBarGap(0);
		chart = updateChart(chart);
		return chart;
	}

	/**
	 * Sets the bar chart to display the number of borrows for the selected
	 * Resource Type.
	 * 
	 * @param chart the bar chart of borrows.
	 * @return the bar chart of borrows.
	 * @throws SQLException if SQL errors.
	 */
	private BarChart<String, Number>
		updateChart(BarChart<String, Number> chart) throws SQLException {
		// Transfer data to the chart
		// Remove all series
		chart.getData().clear();
		// Create new series
		XYChart.Series<String, Number> series =
			new XYChart.Series<String, Number>();

		// Get data from the database
		ObservableList<Data<String, Number>> data = Datastore
			.getPopularResources(this.from.getValue(), this.to.getValue(),
				this.resourceTypeBox.getValue(), this.limit.getValue());
		// set observable list
		series.setData(data);

		// Set table
		this.dataTable.setItems(data);
		// Add series to chart
		chart.getData().add(series);

		return chart;
	}

	/**
	 * Changes the date range selected when the user selects one of the default
	 * date range options.
	 * 
	 * @param selectedValue the default option selected.
	 */
	private void changeDateRange(String selectedValue) {
		this.to.setValue(LocalDate.now());
		switch (selectedValue) {
		case "Past Week":
			this.from.setValue(LocalDate.now().minusWeeks(1));
			break;
		case "Past Month":
			this.from.setValue(LocalDate.now().minusMonths(1));
			break;
		case "Past Year":
			this.from.setValue(LocalDate.now().minusYears(1));
			break;
		case "All Time":
			this.from.setValue(LocalDate.MIN);
			break;
		default:
			throw new RuntimeException("Invalid default date selected!");
		}
	}

	/**
	 * Used for testing purposes only.
	 * 
	 * @param args unused
	 * @throws SQLException if SQL errors.
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Datastore.init("jdbc:mysql://localhost:3306/tawe_lib", "root", "");
		Datastore.logIn("Librarian1");
		launch(args);
	}

	/**
	 * Sets the successMessage to the given success value and string of text.
	 * 
	 * @param success whether the operation was a success or not.
	 * @param text    the text to set the message to.
	 */
	private void setErrorLabel(boolean success, String text) {
		this.successMessage.setVisible(true);
		if (success) {
			this.successMessage.setTextFill(Color.GREEN);
		} else {
			this.successMessage.setTextFill(Color.RED);
		}
		successMessage.setText(text);
	}
}
