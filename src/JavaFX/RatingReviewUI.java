package JavaFX;

import java.sql.SQLException;

import Core.Customer;
import Core.Datastore;
import Core.Resource;
import Core.ReviewAndRating;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class it shows a form of entering rating and review.
 * @author Ali Alowais
 * @version 1.0
 *
 */
public class RatingReviewUI extends Application {
	
	private Resource selectedResource;
	private Customer currentCustomer;
	
	private String editedRatingNumberString;
	private String editedReviewString;
	boolean submittedAtLeastOnce = false;

	public void start(Stage primaryStage) {
		
		//Casts it to type Resources and makes selected resource refer to this
		selectedResource = Datastore.getResources()
				.get(ResourceSearch.selectedResourceID);
		
		//Casts it to type Customer and makes currentCustomer refer to this
		currentCustomer = (Customer) Datastore.getCurrentUser();

		GridPane gridPane = ratingReview();

		Scene scene = new Scene(gridPane, 600, 500);

		addUIControls(gridPane, primaryStage);

		primaryStage.setTitle("Rating and Review");

		primaryStage.setScene(scene);
		primaryStage.show();

	}
	
	/**
	 * This method constructs a GridPane ready to use.
	 * @return gridPane The constructed grid pane.
	 */
	private GridPane ratingReview() {

		// Instantiate a new Grid Pane
		GridPane gridPane = new GridPane();

		// Position the pane at the center of the screen
		gridPane.setAlignment(Pos.CENTER);
		
		// Set the horizontal gap between columns
		gridPane.setHgap(10);
		
		// Set the vertical gap between rows
		gridPane.setVgap(10);

		gridPane.setPadding(new Insets(30, 30, 30, 30));

		return gridPane;

	}

	/**
	 * This method adds UI controls to the grid pane.
	 * @param gridPane The grid pane to which UI controls are added to.
	 * @param primaryStage The current stage.
	 */
	private void addUIControls(GridPane gridPane, Stage primaryStage) {

		// Add the title
		Text title = new Text("Rating Review Interface: ");
		title.setFont(Font.font("Helvetica", FontWeight.EXTRA_BOLD, 25));

		// Add The Label for entering a rating and write a review
		Label ratingLabel = new Label("Enter a rating: ");
		ratingLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
		TextField ratingTextField = new TextField();
		ratingTextField.setPrefWidth(50);

		Label reviewLabel = new Label("Write a review: ");
		reviewLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
		
		//Add the Text Field for write a review
		TextArea reviewTextArea = new TextArea();
		reviewTextArea.setPromptText("Write a review: ");
		reviewTextArea.setFocusTraversable(true);
		reviewTextArea.setPrefWidth(900);
		

		for(ReviewAndRating data : selectedResource.getReviewsAndRatings()) {
			if (data.getCustomer() == currentCustomer) {
				editedRatingNumberString = 
									Float.toString(data.getRatingNumber());
				editedReviewString = data.getReviewString();
				ratingTextField.setText(editedRatingNumberString);
				reviewTextArea.setText(editedReviewString);
				submittedAtLeastOnce = true;
			}
		}
		
		
		final Text actionTarget = new Text();

		Button submitButton = new Button("Submit");
		submitButton.setOnAction(event -> {
			
			if(!ratingTextField.getText().trim().isEmpty()) {
		
				float ratingNumber = Float.
										 parseFloat(ratingTextField.getText());
				String reviewString = reviewTextArea.getText();
				
				if(!submittedAtLeastOnce) {
					try {
						selectedResource.addReviewAndRating(selectedResource, 
								  currentCustomer, ratingNumber, reviewString);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				} else {
					try {
						selectedResource.editReviewAndRating(selectedResource, 
								  currentCustomer, ratingNumber, reviewString);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			
				actionTarget.setFill(Color.GREEN);
				actionTarget.setText("Submitted successfully");
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText("Attention! You cannot leave the rating "
						+ "field blank.");
				alert.setHeaderText("No rating given"); 
				alert.setTitle("Cannot submit information");
				alert.setResult(ButtonType.OK);
				alert.showAndWait();
			}
		});
		
		Button closeButton = new Button("Close");
		closeButton.setOnAction(e -> {
			primaryStage.close();
		});

		gridPane.add(title, 0, 0);
		gridPane.add(ratingLabel, 0, 2);
		gridPane.add(ratingTextField, 2, 2);
		gridPane.add(reviewLabel, 0, 3);
		gridPane.add(reviewTextArea, 0, 4, 2, 1);
		gridPane.add(submitButton, 0, 5);
		gridPane.add(closeButton, 1, 5);
		gridPane.add(actionTarget, 0, 6);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
