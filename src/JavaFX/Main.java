package JavaFX;

import java.sql.SQLException;
import Core.*;
import javafx.application.Application;

/**
 * This is the driver class from which Tawe-Lib project is launched
 * 
 * @author Benjamin
 * @version 1.0
 */
public class Main {

	public static void main(String[] args) throws SQLException {
		Datastore.init("jdbc:mysql://localhost:3306/tawe_lib", "root",
			"billy123");
		Application.launch(LoginForm.class, args);
	}
}
