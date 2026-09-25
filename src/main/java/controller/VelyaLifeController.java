package controller;

import components.NavBar;
import components.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class VelyaLifeController {

    @FXML
    private BorderPane mainContainer;

    @FXML
    private VBox navBarContainer;

    private NavBar navBar;

    @FXML
    public void initialize() {
        // Set up the navigation bar
        navBar = new NavBar();
        navBarContainer.getChildren().add(navBar);

        // Register this controller with the ViewNavigator
        ViewNavigator.setMainController(this);

        // Load the home view by default
        ViewNavigator.navigateToHome();
    }

    /**
     * Set the content of the main area
     */
    public void setContent(Node content) {
        mainContainer.setCenter(content);
    }

    /**
     * Update the navigation bar based on authentication status
     */
    public void updateNavBar(boolean isAuthenticated) {
        navBar.updateAuthStatus(isAuthenticated);
    }
}
