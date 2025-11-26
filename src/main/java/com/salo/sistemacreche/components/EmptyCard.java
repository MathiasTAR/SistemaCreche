package com.salo.sistemacreche.components;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EmptyCard extends VBox {

    public EmptyCard() {
        initializeComponents();
        setupLayout();
        applyStyles();
    }

    public EmptyCard(String message) {
        this();
        setMessage(message);
    }

    private void initializeComponents() {
        // Pode adicionar mais componentes se necessário
    }

    private void setupLayout() {
        this.setAlignment(javafx.geometry.Pos.CENTER);
    }

    private void applyStyles() {
        this.setStyle("-fx-background-color: #fff3e0; " +
                "-fx-border-color: #ffb74d; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 20; " +
                "-fx-cursor: hand;");
    }

    public void setMessage(String message) {
        this.getChildren().clear();

        Label labelAviso = new Label(message);
        labelAviso.setStyle("-fx-text-fill: #ef6c00; -fx-font-weight: bold; -fx-font-size: 14;");

        this.getChildren().add(labelAviso);
    }
}