package application;

import javafx.application.Application;
import utility.AdminCreator;
import utility.DBInit;

public class Launcher {
    public static void main(String[] args) {
        DBInit.launch();
        AdminCreator.launch();
        Application.launch(VelyaLifeApplication.class, args);

    }
}
