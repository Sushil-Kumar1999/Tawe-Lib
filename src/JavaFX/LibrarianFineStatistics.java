/**
 * 
 */
package JavaFX;

import java.sql.SQLException;

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
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Allows the Librarian to see statistics about the fines customers have
 * accrued.
 * 
 * @author Ben Kennard, 965798
 */
public class LibrarianFineStatistics extends Application {

	// The dimensions of the window
	private static final int WINDOW_WIDTH = 1200;
	private static final int WINDOW_HEIGHT = 450;
	// Spacing for control elements
	private static final int CONTROL_SPACING = 5;
	private static final int CONTROL_PADDING = 10;
	// Control elements
	private ComboBox<String> dataSelectionBox;
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
		primaryStage.setTitle("Customer Fine Statistics");
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

		// Combobox to select resource type
		Label dataSelectionLabel = new Label("Show data regarding:");
		this.dataSelectionBox = new ComboBox<String>(
			FXCollections.observableArrayList("Most Fined Customers",
				"Current Largest Outstanding Balance"));
		// default data is Most Fined Customers
		this.dataSelectionBox.setValue("Most Fined Customers");

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

		// Spinner to limit number of users shown
		Label limitLabel = new Label("Max Number of Customers to Display:");
		this.limit = new Spinner<Integer>(MIN_LIMIT, MAX_LIMIT, DEFAULT_LIMIT);
		this.limit.setPrefWidth(LIMIT_WIDTH);
		this.limit.setEditable(true);

		controls.getChildren().addAll(dataSelectionLabel,
			this.dataSelectionBox, limitLabel, this.limit, updateChart);
		// Add controls to main pane
		graphPane.setBottom(controls);

		// Create the data table
		this.dataTable = new TableView<Data<String, Number>>();

		// Set up the table's tableColumns
		TableColumn<Data<String, Number>, String> tc1 =
			new TableColumn<Data<String, Number>, String>("Username");
		tc1.setCellValueFactory(data -> {
			return data.getValue().XValueProperty();
		});
		// This column gets named differently depending on which data is being
		// shown
		TableColumn<Data<String, Number>, Number> tc2 =
			new TableColumn<Data<String, Number>, Number>("");
		tc2.setCellValueFactory(data -> {
			return data.getValue().YValueProperty();
		});

		this.dataTable.getColumns().addAll(tc1, tc2);

		// Create the statistics bar chart (also sets table data)
		try {
			this.chart = buildBarChart();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.setErrorLabel(false, e.getMessage());
		}
		// Set bar chart as centre element
		graphPane.setCenter(this.chart);

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
		ObservableList<Data<String, Number>> data =
			this.getData(this.dataSelectionBox.getValue());
		// set observable list
		series.setData(data);

		// Set table
		this.dataTable.setItems(data);
		// Add series to chart
		chart.getData().add(series);

		return chart;
	}

	/**
	 * Gets the data from the database for the selected dataset.
	 * 
	 * @param selectedData the type of data selected, as chosen by the
	 *                     combobox.
	 * @return the series data from the database.
	 * @throws SQLException if SQL errors.
	 */
	private ObservableList<Data<String, Number>> getData(String selectedData)
		throws SQLException {
		switch (selectedData) {
		case "Most Fined Customers":
			// Set column 2 to "number of fines"
			this.dataTable.getColumns().get(1).setText("Number of Fines");
			return Datastore.getMostFinedUsers(this.limit.getValue());
		case "Current Largest Outstanding Balance":
			// Set column 2 to "balance"
			this.dataTable.getColumns().get(1).setText("Balance");
			return Datastore.getBiggestBalances(this.limit.getValue());
		default:
			throw new RuntimeException("Invalid data selection!");
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
