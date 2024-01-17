package JavaFX;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import Core.Copy;
import Core.Customer;
import Core.Datastore;
import Core.TransactionLoan;
import Core.VideoGame;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class shown a form of user video game
 * 
 * @author Ali Alowais And Billy Roberts
 * @version 1.0
 *
 */

public class UserVideoGame extends Application {

	// Search for a particular resource using its ID
	private VideoGame selectedResource;

	// Search for a current customer using its ID
	private Customer currentCustomer;

	public void start(Stage primaryStage) throws FileNotFoundException{

		//Casts it to type VideoGame and makes selected resource refer to this
		selectedResource = (VideoGame) Datastore.getResources().get(ResourceSearch.selectedResourceID);

		//Casts it to type Customer and makes currentCustomer refer to this
		currentCustomer = (Customer) Datastore.getCurrentUser();

		GridPane gridPane = videoGameForm();

		Scene scene = new Scene(gridPane, 700, 600);

		// Add UI controls to form
		try {
			addUIControls(gridPane);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

		primaryStage.setTitle("Video Game View");

		// Set the scene in primary stage
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private GridPane videoGameForm() {

		// Instantiate a new Grid Pane
		GridPane gridPane = new GridPane();

		// Position the pane at the center of the screen
		gridPane.setAlignment(Pos.TOP_CENTER);

		// Set the horizontal gap between columns
		gridPane.setHgap(10);

		// Set the vertical gap between rows
		gridPane.setVgap(10);

		// Set a padding on each side
		gridPane.setPadding(new Insets(25, 25, 25, 25));

		return gridPane;
	}

	private void addUIControls(GridPane gridPane) throws FileNotFoundException {

		// Add the title
		Text title = new Text("Title: " + selectedResource.getTitle());
		title.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));

		// Add The Label for each resource
		Label year = new Label("Year:");
		Label yearTextField = new Label(Integer.toString(selectedResource.getYear()));

		Label publisher = new Label("Publisher:");
		Label publisherTextField = new Label(selectedResource.getPublisher());

		Label genre = new Label("Genre:");
		Label genreTextField = new Label(selectedResource.getGenre());

		Label certificateRating = new Label("Certificate Rating:");
		Label certificateRatingTextField = new Label(selectedResource.getCertificateRating());

		Label multiplayerSupport = new Label("Multiplayer Support:");
		Label multiplayerSupportTextField = new Label();
		if(selectedResource.getMultiplayerSupport() == true) {
			multiplayerSupportTextField.setText("Yes");
		} else if(selectedResource.getMultiplayerSupport() == false) {
			multiplayerSupportTextField.setText("No");
		}
		

		String profileImagePath = (selectedResource.getThumbnailImagePath());
		Image image = new Image(new FileInputStream(profileImagePath));
		ImageView imageView = new ImageView(image);

		// setting the fit height and width of the image view
		imageView.setFitHeight(100);
		imageView.setFitWidth(50);

		// Setting the preserve ratio of the image view
		imageView.setPreserveRatio(true);

		Label requestResource = new Label("Request this Resource:");
		Button requestResourceBtn = new Button("Request");
		
		Label rateAndReviewLabel = new Label("Rate or write a review:");
		Button rateAndReviewButton  = new Button("Rate or Review");
		
		Label viewLabel = new Label("View Ratings and Reviews for this resource:");
		Button viewButton = new Button("View");
		
		Label trailerLabel = new Label("View Trailer for this resource:");
		Button trailerButton = new Button("View Trailer");
		
		Label successMessage = new Label();
		
		successMessage.setVisible(false);
		gridPane.add(successMessage, 3, 10);
		
		requestResourceBtn.setOnAction(e -> {

			try {
				Copy possibleReservedCopy = selectedResource.addRequest(currentCustomer);
				successMessage.setTextFill(Color.GREEN);
				successMessage.setText("Resource requested");
				if (possibleReservedCopy != null) {
					successMessage.setText("Resource requested. "
							+ "A copy of this resource with unique ID " + 
							possibleReservedCopy.getUniqueId() + 
							" has been reserved for this user" );
				}
				else {
					successMessage.setText("Resource requested");
				}
			} catch (IllegalArgumentException e1) {
				successMessage.setTextFill(Color.RED);
				successMessage.setText(e1.getMessage());
				successMessage.setVisible(true);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			successMessage.setVisible(true);
		});
		
		rateAndReviewButton.setOnAction(event -> {
			
			boolean selectedResourceBorrowed = false;
			
			for (Copy loanedCopy : currentCustomer.getCurrentLoans()) {
				 if (loanedCopy.getResourceRef() == selectedResource) {
					 selectedResourceBorrowed = true;
				 }
			}
			
			/* 
			 * to check if the selected resource has been borrowed in the past
			 * by the current customer
			*/ 
			if (!selectedResourceBorrowed) {
				for (Copy loanedCopy : currentCustomer.getCurrentLoans()) {
					int i = 0;
					while (i < loanedCopy.getTransactionHistory().size() &&
												!selectedResourceBorrowed) {
						if (loanedCopy.getTransactionHistory().get(i).
							getCustRef() == currentCustomer && 
							loanedCopy.getResourceRef() == selectedResource ) {
							selectedResourceBorrowed = true;
						}
						i++;
					}
				}
			}
			
			if (selectedResourceBorrowed == true) {
				RatingReviewUI ratingReviewUI = new RatingReviewUI();
				ratingReviewUI.start(new Stage());
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText("Sorry! You cannot rate or review this "
						+ "resource since you have not borrowed this resource "
						+ "before.");
				alert.setHeaderText("Ineligible to rate or review"); 
				alert.setTitle("Cannot perform action");
				alert.setResult(ButtonType.OK);
				alert.showAndWait();
			}
		});

		viewButton.setOnAction(event -> {
			
			if(!selectedResource.getReviewsAndRatings().isEmpty()) {
				ViewReviewAndRating viewReviewAndRating = 
													new ViewReviewAndRating();
				viewReviewAndRating.start(new Stage());
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText("Sorry! This resource does not have any "
						+ "ratings or reviews to view.");
				alert.setHeaderText("No ratings or reviews for this resource"); 
				alert.setTitle("Cannot perform action");
				alert.setResult(ButtonType.OK);
				alert.showAndWait();
			}
		});
		
		
		//When the user clicks on the trailer button.
		trailerButton.setOnAction(event -> {
			//Opens a new window with the trailer for the VideoGame playing.
			TrailerViewer trailerView = new TrailerViewer(selectedResource);
			trailerView.start(new Stage());
		});
		
		// HBox
		HBox hboxBtn = new HBox(10);
		hboxBtn.setAlignment(Pos.BOTTOM_RIGHT);

		gridPane.add(title, 0, 0, 2, 1);
		gridPane.add(returnLine("Line1"), 0, 1, 5, 1);
		gridPane.add(imageView, 0, 2, 2, 1);
		gridPane.add(year, 0, 5);
		gridPane.add(yearTextField, 1, 5);
		gridPane.add(certificateRating, 2, 5);
		gridPane.add(certificateRatingTextField, 3, 5);
		gridPane.add(publisher, 0, 6);
		gridPane.add(publisherTextField, 1, 6);
		gridPane.add(genre, 2, 6);
		gridPane.add(genreTextField, 3, 6);
		gridPane.add(multiplayerSupport, 0, 7);
		gridPane.add(multiplayerSupportTextField, 1, 7);
		gridPane.add(returnLine("Line2"), 0, 8, 5, 1);
		gridPane.add(requestResource, 0, 9, 2, 1);
		gridPane.add(requestResourceBtn, 3, 9, 2, 1);
		gridPane.add(rateAndReviewLabel, 0, 11, 2, 1);
		gridPane.add(rateAndReviewButton, 3, 11, 2, 1);
		gridPane.add(viewLabel, 0, 13, 2, 1);
		gridPane.add(viewButton, 3, 13, 2, 1);
		gridPane.add(trailerLabel, 0, 15, 2, 1);
		gridPane.add(trailerButton, 3, 15, 2, 1);
	}

	/**
	 * This method to create a line between the layout.
	 * 
	 * @param id.  a string of id.
	 * @return line.
	 */
	private static Line returnLine(String id) {

		Line line = new Line(0, 0, 500, 0);
		line.setStroke(Color.BLACK);
		line.maxWidth(10);
		line.setId(id);
		return line;
	}

	public static void main(String[] args) {
		launch(args);

	}
}

