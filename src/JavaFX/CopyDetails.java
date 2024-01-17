package JavaFX;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import Core.*;

/**
 * This class implements a form showing details of a particular copy
 * 
 * @author Sushil Kumar, Mike Coomber.
 * @version 1.0
 */
public class CopyDetails extends Application {

	private static final double WINDOW_WIDTH = 700;
	private static final double WINDOW_HEIGHT = 600;

	private Copy copyReference;
	private int loanDurationChosen; // loan duration chosen by librarian
									// when issuing a copy

	public void setCopyReference(Copy copyReference) {
		this.copyReference = copyReference;
	}

	public Copy getCopyReference() {
		return this.copyReference;
	}

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException {

		primaryStage.setTitle("Copy Details");

		GridPane gridPane = createRootPane();
		gridPane.setGridLinesVisible(false);

		addUIControls(gridPane);

		Scene scene = new Scene(gridPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		// Set the scene in primary stage
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	/**
	 * Adds GUI elements to grid pane
	 * 
	 * @param gridPane the grid pane to add the controls to.
	 * @throws FileNotFoundException if the thumbnail image is not found.
	 */
	private void addUIControls(GridPane gridPane)
		throws FileNotFoundException {
		// Add the title for the copy details
		Text title = new Text("Librarian Details:");
		title.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));

		addImage(gridPane);
		addLabelsAndTextFields(gridPane);
		addLoanButton(gridPane);
		addChoiceBox(gridPane);
	}

	/**
	 * Adds a choice box to the grid pane
	 * 
	 * @param gridPane the grid pane to add the controls to.
	 */
	private void addChoiceBox(GridPane gridPane) {
		ChoiceBox<String> choiceBox = new ChoiceBox<String>();

		choiceBox.getItems().add("1 day");
		choiceBox.getItems().add("1 week");
		choiceBox.getItems().add("2 weeks");
		choiceBox.getItems().add("4 weeks");
		choiceBox.getSelectionModel().selectFirst();
		gridPane.add(choiceBox, 1, 9);

		switch ((String) choiceBox.getValue()) {
		case "1 day":
			loanDurationChosen = 1;
			break;
		case "1 week":
			loanDurationChosen = 7;
			break;
		case "2 weeks":
			loanDurationChosen = 14;
			break;
		case "4 weeks":
			loanDurationChosen = 28;
			break;
		default:
			loanDurationChosen = 0;
			break;
		}
	}

	/**
	 * Adds labels and Text fields to the grid pane
	 * 
	 * @param gridPane the pane to add fields to.
	 */
	private void addLabelsAndTextFields(GridPane gridPane) {
		Label title = new Label("Title: ");
		title.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));
		Label titleLabelField = new Label();
		titleLabelField.setText(copyReference.getResourceRef().getTitle());
		titleLabelField.setFont(Font.font("Helvetica", FontWeight.NORMAL, 17));

		Label userName = new Label("Enter Username: ");
		userName.setFont(Font.font("Helvetica", FontWeight.NORMAL, 15));

		Label currentBorrower = new Label("Current borrower: ");
		currentBorrower.setFont(Font.font("Helvetica", FontWeight.NORMAL, 15));
		Label borrowerLabelField = new Label();
		if (copyReference.getCustRef() == null) {
			borrowerLabelField.setText("No current borrower");
		} else {
			borrowerLabelField
				.setText(copyReference.getCustRef().getUsername());
		}

		Label loanDuration = new Label("Loan Duration: ");
		loanDuration.setFont(Font.font("Helvetica", FontWeight.NORMAL, 15));
		Label loanDurationLabelField = new Label();
		loanDurationLabelField
			.setText(Integer.toString(copyReference.getLoanDuration()));

		Label issueDate = new Label("Issue Date: ");
		issueDate.setFont(Font.font("Helvetica", FontWeight.NORMAL, 15));
		Label issueDateLabelField = new Label();
		if (copyReference.getCustRef() == null) {
			issueDateLabelField.setText("Not currently being borrowed");
		} else {
			issueDateLabelField
				.setText(UserUI.ddmmyy.format(copyReference.getLoanDate()));
		}

		Label dueDate = new Label("Due Date: ");
		dueDate.setFont(Font.font("Helvetica", FontWeight.NORMAL, 15));
		Label dueDateLabelField = new Label();
		if (copyReference.getDueDate() == null) {
			dueDateLabelField.setText("No due date");
		} else {
			dueDateLabelField
				.setText(UserUI.ddmmyy.format(copyReference.getDueDate()));
		}

		Label reservedBy = new Label("Reserved by: ");
		reservedBy.setFont(Font.font("Helvetica", FontWeight.NORMAL, 15));
		Label reservedByField = new Label();
		reservedByField.setFont(Font.font("Helvetica", FontWeight.NORMAL, 15));

		if (copyReference.getReservedBy() == null) {
			reservedByField.setText("Not currently reserved");
		} else {
			reservedByField
				.setText(copyReference.getReservedBy().getUsername());
		}

		Label loanTime = new Label("Set Loan Duration: ");
		loanTime.setFont(Font.font("Helvetica", FontWeight.NORMAL, 15));

		gridPane.add(title, 0, 0);
		gridPane.add(returnLine("Line1"), 0, 1, 5, 1);
		gridPane.add(titleLabelField, 1, 0);
		gridPane.add(userName, 0, 8);
		gridPane.add(currentBorrower, 0, 5);
		gridPane.add(borrowerLabelField, 1, 5);
		gridPane.add(loanDuration, 0, 6);
		gridPane.add(loanDurationLabelField, 1, 6);
		gridPane.add(issueDate, 2, 6);
		gridPane.add(issueDateLabelField, 3, 6);
		gridPane.add(dueDate, 0, 7);
		gridPane.add(dueDateLabelField, 1, 7);
		gridPane.add(loanTime, 1, 8);
		gridPane.add(reservedBy, 2, 5);
		gridPane.add(reservedByField, 3, 5);
	}

	/**
	 * Adds a button called "Loan Copy" to the grid pane
	 * 
	 * @param gridPane the grid pane to add the controls to.
	 */
	private void addLoanButton(GridPane gridPane) {
		Button loanButton = new Button("Loan copy");
		Button returnButton = new Button("Return copy");

		if (copyReference.getReservedBy() != null) {
			loanButton.setDisable(false);
			returnButton.setDisable(true);
		} else if (!copyReference.isAvailable()) {
			loanButton.setDisable(true);
			returnButton.setDisable(false);
		} else {
			loanButton.setDisable(false);
			returnButton.setDisable(true);
		}

		Label loanedErrorMessage = new Label();

		Label returnedMessage = new Label("Copy returned successfully");
		returnedMessage.setTextFill(Color.GREEN);
		returnedMessage.setVisible(false);

		TextField userNameTextField = new TextField();
		userNameTextField.setPromptText("Enter borrower name: ");
		userNameTextField.setFocusTraversable(false);

		gridPane.add(loanButton, 2, 9);
		gridPane.add(loanedErrorMessage, 3, 9);
		gridPane.add(userNameTextField, 0, 9);
		gridPane.add(returnButton, 2, 10);
		gridPane.add(returnedMessage, 3, 10);

		// Create list view of transaction history.
		ListView<String> transactionHistoryView =
			new ListView<String>(getTransactionHistory());
		transactionHistoryView.setMaxHeight(250);
		transactionHistoryView.setMinWidth(300);

		gridPane.add(transactionHistoryView, 0, 12, 4, 4);

		loanButton.setOnAction(event -> {
			Customer borrower = (Customer) Datastore.getUsers()
				.get(userNameTextField.getText());
			if (borrower == null) {
				loanedErrorMessage.setText("User not found");
			} else {
				addChoiceBox(gridPane);
				try {
					copyReference.borrow(borrower, loanDurationChosen);
					loanedErrorMessage.setTextFill(Color.GREEN);
					loanedErrorMessage.setText("Copy loaned succesfully");
					transactionHistoryView.setItems(getTransactionHistory());
					loanButton.setDisable(false);
					returnButton.setDisable(true);
				} catch (IllegalArgumentException e1) {
					loanedErrorMessage.setTextFill(Color.FIREBRICK);
					loanedErrorMessage.setText(e1.getMessage());
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
			}
		});

		returnButton.setOnAction(e -> {
			try {
				if (copyReference.isOverdue()) {
					returnedMessage
						.setText("Copy is overdue. Customer has been charged £"
							+ copyReference.getResourceRef().calculateFine(
								copyReference.getDaysOverdue()));
				}
				copyReference.returnCopy();

				returnedMessage.setVisible(true);
				transactionHistoryView.setItems(getTransactionHistory());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

	}

	/**
	 * Adds a thumbnail image to the grid pane
	 * 
	 * @param gridPane the grid pane to add the controls to.
	 * @throws FileNotFoundException if the thumbnail image is not found.
	 */
	private void addImage(GridPane gridPane) throws FileNotFoundException {
		String profileImagePath =
			(copyReference.getResourceRef().getThumbnailImagePath());
		Image image = new Image(new FileInputStream(profileImagePath));
		ImageView imageView = new ImageView(image);

		// setting the fit height and width of the image view
		imageView.setFitHeight(100);
		imageView.setFitWidth(50);

		// Setting the preserve ratio of the image view
		imageView.setPreserveRatio(true);
		gridPane.add(imageView, 0, 2, 2, 1);
	}

	/**
	 * Creates the main pane in the scene
	 * 
	 * @return gridPane
	 */
	private GridPane createRootPane() {
		GridPane gridPane = new GridPane();

		// Position the pane at the center of the screen
		gridPane.setAlignment(Pos.CENTER);

		// Set the horizontal gap between columns
		gridPane.setHgap(10);

		// Set the vertical gap between rows
		gridPane.setVgap(10);

		// Set a padding on each side
		gridPane.setPadding(new Insets(10, 10, 10, 10));

		return gridPane;
	}

	/**
	 * This method to create a line between the layout.
	 * 
	 * @param id. a string of id.
	 * @return line.
	 */
	private static Line returnLine(String id) {

		Line line = new Line(0, 0, WINDOW_WIDTH - 10, 0);
		line.setStroke(Color.BLACK);
		line.maxWidth(10);
		line.setId(id);
		return line;
	}

	public static void main(String[] args) {
		launch(args);
	}

	private ObservableList<String> getTransactionHistory() {
		// Import the copies transaction history
		ArrayList<TransactionLoan> transactionHistory =
			copyReference.getTransactionHistory();
		// Reverse the list so the most recent ones are output first.
		Collections.reverse(transactionHistory);

		// Convert them all to strings and put them in a new list
		ObservableList<String> transactionHistoryStrings =
			FXCollections.observableArrayList();
		for (TransactionLoan l : copyReference.getTransactionHistory()) {
			transactionHistoryStrings.add(l.toString());
		}

		return transactionHistoryStrings;
	}
}
