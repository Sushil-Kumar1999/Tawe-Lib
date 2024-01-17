package JavaFX;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

import Core.*;

/**
 * Allows the user to see statistics about their borrowing history.
 * 
 * @author Ben Kennard 965798
 */
public class UserViewStatistics extends Application {

	// The dimensions of the window
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 450;
	// Spacing for control elements
	private static final int CONTROL_SPACING = 5;
	private static final int CONTROL_PADDING = 10;
	// Control elements
	private DatePicker from;
	private DatePicker to;
	private ComboBox<String> bucketSizeBox;
	// Number of buckets to show on chart by default
	private static final int DEFAULT_DAILY_BUCKET_COUNT = 7;
	private static final int DEFAULT_WEEKLY_BUCKET_COUNT = 8;
	private static final int DEFAULT_MONTHLY_BUCKET_COUNT = 12;
	// The statistics bar chart
	BarChart<String, Number> chart;
	// The statistics table
	TableView<Data<String, Number>> dataTable;
	// The error message Label
	private Label successMessage;

	@Override
	/**
	 * Starts the application.
	 */
	public void start(Stage primaryStage) {
		primaryStage.setTitle("My Borrowing Statistics");
		Pane mainPane = buildRootPane();
		Scene mainScene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		// Display the scene on the stage
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	/**
	 * Builds the pane which the scene is comprised of. The pane returned is
	 * either used for displaying as a window, or for displaying as a tab of
	 * the UserUI (hence why this method is Public).
	 * 
	 * @return the main pane for this application.
	 */
	public Pane buildRootPane() {
		// Create the main pane
		VBox mainPane = new VBox(CONTROL_SPACING);
		mainPane.setPadding(new Insets(CONTROL_PADDING));
		mainPane.setAlignment(Pos.CENTER);
		// Create the graph pane
		BorderPane graphPane = new BorderPane();
		// Create bottom controls
		// Initialise HBox
		HBox controls = new HBox(CONTROL_SPACING);
		controls.setPadding(new Insets(CONTROL_PADDING));
		controls.setAlignment(Pos.CENTER);

		// Title label
		Label title = new Label("Borrowing Statistics");
		title.setFont(new Font(16));

		// Label to print error messages
		this.successMessage = new Label();

		// From date
		Label fromLabel = new Label("From:");
		this.from = new DatePicker(LocalDate.now().minusDays(7));

		// To date
		Label toLabel = new Label("To:");
		this.to = new DatePicker(LocalDate.now());

		// Combobox to select bucket size
		this.bucketSizeBox = new ComboBox<String>(
			FXCollections.observableArrayList("Daily", "Weekly", "Monthly"));
		// default bucket size is daily
		this.bucketSizeBox.setValue("Daily");
		// On combobox change, increase date range, unless range is already
		// large enough
		this.bucketSizeBox.setOnAction(e -> {
			this.ExpandDateRange(this.bucketSizeBox.getValue());
		});

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

		controls.getChildren().addAll(fromLabel, this.from, toLabel, this.to,
			this.bucketSizeBox, updateChart);
		// Add controls to main pane
		graphPane.setBottom(controls);

		// Create the data table
		this.dataTable = new TableView<Data<String, Number>>();

		// Create the statistics bar chart (also sets table data)
		try {
			this.chart = buildBarChart();
		} catch (SQLException e) {
			this.setErrorLabel(false, e.getMessage());
		}
		// Set bar chart as centre element
		graphPane.setCenter(this.chart);

		// Set up the table's tableColumns
		TableColumn<Data<String, Number>, String> tc1 =
			new TableColumn<Data<String, Number>, String>("Date");
		tc1.setCellValueFactory(data -> {
			return data.getValue().XValueProperty();
		});
		TableColumn<Data<String, Number>, Number> tc2 =
			new TableColumn<Data<String, Number>, Number>("Number of Borrows");
		tc2.setCellValueFactory(data -> {
			return data.getValue().YValueProperty();
		});

		this.dataTable.getColumns().addAll(tc1, tc2);

		mainPane.getChildren().addAll(title, graphPane, this.successMessage,
			this.dataTable);

		return mainPane;
	}

	/**
	 * Builds the bar chart, which displays the number of borrows per unit of
	 * time bucket. The bar chart is initialised with daily data.
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
	 * Sets the bar chart to display the number of borrows in the selected
	 * bucket size.
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
		ObservableList<Data<String, Number>> data =
			this.getData(this.bucketSizeBox.getValue());
		// set observable list
		series.setData(data);
		// Set table
		this.dataTable.setItems(data);
		// Add series to chart
		chart.getData().add(series);

		return chart;
	}

	/**
	 * Gets the data for the chart based on the given bucket size.
	 * 
	 * @param bucketSize the time bucket to group the borrow data by.
	 * @return the observableList that can be used as series data.
	 * @throws SQLException if SQL errors.
	 */
	private ObservableList<Data<String, Number>> getData(String bucketSize)
		throws SQLException {
		switch (bucketSize) {
		case "Daily":
			return Datastore.getDailyBorrows(
				(Customer) Datastore.getCurrentUser(), this.from.getValue(),
				this.to.getValue());
		case "Weekly":
			return Datastore.getWeeklyBorrows(
				(Customer) Datastore.getCurrentUser(), this.from.getValue(),
				this.to.getValue());
		case "Monthly":
			return Datastore.getMonthlyBorrows(
				(Customer) Datastore.getCurrentUser(), this.from.getValue(),
				this.to.getValue());
		default:
			throw new RuntimeException("Invalid bucket size selected!");
		}
	}

	/**
	 * If the date range is too small to display much data for the selected
	 * bucket size, the date range is expanded into the past.
	 * 
	 * @param bucketSize the selected bucket size.
	 */
	private void ExpandDateRange(String bucketSize) {
		switch (bucketSize) {
		case "Daily":
			if (to.getValue().minusDays(DEFAULT_DAILY_BUCKET_COUNT)
				.isBefore(from.getValue())) {
				from.setValue(
					to.getValue().minusDays(DEFAULT_DAILY_BUCKET_COUNT));
			}
			break;
		case "Weekly":
			if (to.getValue().minusWeeks(DEFAULT_WEEKLY_BUCKET_COUNT)
				.isBefore(from.getValue())) {
				from.setValue(
					to.getValue().minusWeeks(DEFAULT_WEEKLY_BUCKET_COUNT));
			}
			break;
		case "Monthly":
			if (to.getValue().minusMonths(DEFAULT_MONTHLY_BUCKET_COUNT)
				.isBefore(from.getValue())) {
				from.setValue(
					to.getValue().minusMonths(DEFAULT_MONTHLY_BUCKET_COUNT));
			}
			break;
		default:
			throw new RuntimeException("Invalid bucket size selected!");
		}
	}

	/**
	 * Used for testing purposes.
	 * 
	 * @param args unused.
	 * @throws SQLException if SQL errors.
	 */
	public static void main(String[] args) throws SQLException {
		Datastore.init("jdbc:mysql://localhost:3306/tawe_lib", "root", "");
		Datastore.logIn("PhoneBookEnthusiast");
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
