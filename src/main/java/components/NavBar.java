package components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class NavBar extends HBox {
        private boolean isAuthenticated;

        public NavBar() {
                this(false);
        }

        public NavBar(boolean isAuthenticated) {
                this.isAuthenticated = isAuthenticated;
                initialize();
        }

        private void initialize() {
                this.setSpacing(10);
                this.setPadding(new Insets(10));
                this.setStyle("-fx-background-color: #38989c;");

                Label brandLabel = new Label("VelyaLife App");
                brandLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
                this.getChildren().add(brandLabel);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                this.getChildren().add(spacer);

                if (!isAuthenticated) {
                        createUnauthenticatedNavButtons();
                }
        }

        private void createUnauthenticatedNavButtons() {
                Button homeBtn = createNavButton("Home", _ -> ViewNavigator.navigateToHome());
                Button loginBtn = createNavButton("Login", _ -> ViewNavigator.navigateToLogin());

                this.getChildren().addAll(homeBtn, loginBtn);
        }

        private Button createNavButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
                Button button = new Button(text);
                button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;");
                button.setOnAction(handler);

                button.setOnMouseEntered(_ ->
                        button.setStyle("-fx-background-color: #38989c; -fx-text-fill: white; -fx-cursor: hand;"));
                button.setOnMouseExited(_ ->
                        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;"));

                return button;
        }

        public void updateAuthStatus(boolean isAuthenticated) {
                this.isAuthenticated = isAuthenticated;
                this.getChildren().clear();
                initialize();
        }
}
