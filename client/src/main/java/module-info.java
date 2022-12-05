module dm.bel.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.rmi;

    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens bel.dm.client to javafx.fxml;
    exports bel.dm.client;
    exports bel.dm.client.classes;
    opens bel.dm.client.classes to javafx.fxml;
    exports bel.dm.client.controllers;
    opens bel.dm.client.controllers to javafx.fxml;
}