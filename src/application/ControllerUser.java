package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;

public class ControllerUser {
	
	@FXML
	private Label textLabel;
	
	@FXML
	private Label titleLabel;
	
	@FXML
	private TextField usernameField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private Button loginButton;
	
	@FXML
	private Hyperlink switchLink;
	
	private boolean loginMode=true;
	private UserManager userManager=new UserManager();
	private User currentUser;
	
    public ControllerUser() {
        userManager.read();
    }
	

	@FXML
	private void loginModeSwitch() {
		
	    FadeTransition ftTitle = new FadeTransition(Duration.millis(200), titleLabel);
	    ftTitle.setFromValue(1.0);
	    ftTitle.setToValue(0.0);

	    FadeTransition ftButton = new FadeTransition(Duration.millis(200), loginButton);
	    ftButton.setFromValue(1.0);
	    ftButton.setToValue(0.0);

	    FadeTransition ftText = new FadeTransition(Duration.millis(200), textLabel);
	    ftText.setFromValue(1.0);
	    ftText.setToValue(0.0);

	    FadeTransition ftLink = new FadeTransition(Duration.millis(200), switchLink);
	    ftLink.setFromValue(1.0);
	    ftLink.setToValue(0.0);

	    ParallelTransition fadeOut = new ParallelTransition(ftTitle, ftButton, ftText, ftLink);

	    fadeOut.setOnFinished(e -> {
	        loginMode = !loginMode;
	        if(loginMode) {
	            loginButton.setText("Sign in");
	            titleLabel.setText("Sign in");
	            textLabel.setText("Don't have an account?");
	            switchLink.setText("Sign up now!");
	        } else {
	            loginButton.setText("Sign up");
	            titleLabel.setText("Sign up");
	            textLabel.setText("Already have an account?");
	            switchLink.setText("Log in now!");
	        }

	        FadeTransition ftTitleIn = new FadeTransition(Duration.millis(200), titleLabel);
	        ftTitleIn.setFromValue(0.0);
	        ftTitleIn.setToValue(1.0);

	        FadeTransition ftButtonIn = new FadeTransition(Duration.millis(200), loginButton);
	        ftButtonIn.setFromValue(0.0);
	        ftButtonIn.setToValue(1.0);

	        FadeTransition ftTextIn = new FadeTransition(Duration.millis(200), textLabel);
	        ftTextIn.setFromValue(0.0);
	        ftTextIn.setToValue(1.0);

	        FadeTransition ftLinkIn = new FadeTransition(Duration.millis(200), switchLink);
	        ftLinkIn.setFromValue(0.0);
	        ftLinkIn.setToValue(1.0);

	        ParallelTransition fadeIn = new ParallelTransition(ftTitleIn, ftButtonIn, ftTextIn, ftLinkIn);
	        fadeIn.play();
	    });

	    fadeOut.play();
	}

	
	@FXML
	private void loginUser() {
	    String username = usernameField.getText();
	    String password = passwordField.getText();

	    if(loginMode) {
	        User u = userManager.loginUser(username, password);
	        if(u != null) {
	            currentUser = u;
	            try {
	                Stage stage = Main.getPrimaryStage(); 

	                FXMLLoader loader = new FXMLLoader(getClass().getResource("Scene.fxml"));
	                Parent root = loader.load();

	                Controller controller = loader.getController();
	                controller.loadTasks(currentUser);

	                Scene scene = new Scene(root);
	                scene.getStylesheets().add(getClass().getResource("todolistdesign.css").toExternalForm());

	                stage.setScene(scene);
	                stage.show();
	            } catch(IOException e) {
	                e.printStackTrace();
	            }
	        } else {
	            Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Login error");
	            alert.setContentText("Incorrect username or password");
	            alert.showAndWait();
	        }
	    } else {

			if(!userManager.strongPassword(password)) {
				Alert alert=new Alert(AlertType.ERROR);
				alert.setTitle("Weak password");
				alert.setContentText("Your password must contain at least one upper case, digit and special character");
				alert.showAndWait();
				return;
			}
			boolean success=userManager.addUser(username, password);
			if(success) {
				Alert alert=new Alert(AlertType.INFORMATION);
				alert.setTitle("Sign up succesful");
				alert.setContentText("Account created successfully");
				alert.showAndWait();
				loginModeSwitch();
			}
			else {
				Alert alert=new Alert(AlertType.ERROR);
				alert.setTitle("Sign up error");
				alert.setContentText("Username already exists");
				alert.showAndWait();
			}
		}
	}
}
