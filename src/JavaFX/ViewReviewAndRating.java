package JavaFX;

import Core.*;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
/**
 * This class provides the user interface for viewing ratings and
 * reviews of a particular resource
 * @author Sushil Kumar
 * @version 1.0
 */
public class ViewReviewAndRating extends Application {
	
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;
	
	private Resource selectedResource;

	public void start(Stage primaryStage) {
		
		selectedResource = Datastore.getResources()
				.get(ResourceSearch.selectedResourceID);
		
		BorderPane rootPane = buildGUI();

		Scene scene = new Scene(rootPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		primaryStage.setTitle("View ratings and reviews");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * This method constructs a BorderPane with GUI elements.
	 * @return borderPane The completely constructed border pane.
	 */
	public BorderPane buildGUI()
	{
		BorderPane borderPane  = new BorderPane();
		
		float sumOfRatings = 0;
		for(ReviewAndRating elem : selectedResource.getReviewsAndRatings()) {
			sumOfRatings += elem.getRatingNumber();	
		}
		float averageRating = sumOfRatings/selectedResource.
												getReviewsAndRatings().size();
		Label averageRatingLabel = new Label();
		averageRatingLabel.setText("Average rating for this resource ("
				+ selectedResource.getTitle() + ") : " + averageRating);
		averageRatingLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
		
		HBox averageRatingHBox = new HBox(averageRatingLabel);
		averageRatingHBox.setPadding(new Insets(25, 25, 25, 0));
		
		TableView<ReviewAndRating> tableView = new TableView<ReviewAndRating>();
		
		TableColumn<ReviewAndRating, String> userNameColumn = new
							TableColumn<ReviewAndRating, String>("User Name");
		TableColumn<ReviewAndRating, Float> ratingColumn = new
							TableColumn<ReviewAndRating, Float>("Rating");
		TableColumn<ReviewAndRating, String> reviewColumn = new
							TableColumn<ReviewAndRating, String>("Review");
		
		userNameColumn.setPrefWidth(150);
		ratingColumn.setPrefWidth(50);
		reviewColumn.setPrefWidth(550); 
		
		userNameColumn.setResizable(false);
		ratingColumn.setResizable(false);
		reviewColumn.setResizable(false);
	
		userNameColumn.setCellValueFactory(cellData ->
			new ReadOnlyStringWrapper(cellData.getValue().getCustomer().
															  getUsername()));
		ratingColumn.setCellValueFactory(new PropertyValueFactory<>
															("ratingNumber"));
		reviewColumn.setCellValueFactory(new PropertyValueFactory<>
															("reviewString"));
		 
		ObservableList<ReviewAndRating> list = FXCollections.
				observableArrayList(selectedResource.getReviewsAndRatings());
	    tableView.setItems(list);
		
		tableView.getColumns().addAll(userNameColumn, ratingColumn, reviewColumn);
		tableView.setPrefWidth(750);
		
		borderPane.setTop(averageRatingHBox);
		borderPane.setCenter(tableView);
		borderPane.setPadding(new Insets(25, 25, 25, 25));
		
		return borderPane;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
