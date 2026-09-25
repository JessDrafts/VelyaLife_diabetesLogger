module application.Launcher {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires jbcrypt;
    requires jakarta.mail;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.pdfbox;

    opens application to javafx.fxml;
    exports application;
    exports components to javafx.fxml;
    exports controller;
    exports model;
    exports repository;
    exports utility;
    opens controller to javafx.fxml;
    opens model to javafx.base, javafx.fxml;
    opens utility to java.sql;
}