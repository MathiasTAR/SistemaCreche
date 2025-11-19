package com.salo.sistemacreche.components;

import com.salo.sistemacreche.entidades.Crianca;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.Period;

public class IrmaoCard extends HBox {

    private Crianca irmao;
    private CheckBox checkGemeo;
    private Runnable onGemeoSelected;
    private Runnable onGemeoDeselected;

    public IrmaoCard(Crianca irmao) {
        this.irmao = irmao;
        initializeComponents();
        setupLayout();
        applyStyles();
        setupEventHandlers();
    }

    private void initializeComponents() {
        checkGemeo = new CheckBox("É gêmeo?");

        Label nomeLabel = new Label(irmao.getNome());

        // Adiciona indicação se já é gêmeo no banco
        String infoText = calcularIdade() + " anos";
        if (irmao.getPossuiIrmaoGemeo() != null && irmao.getPossuiIrmaoGemeo()) {
            infoText += " (JÁ É GÊMEO)";
        }

        Label infoLabel = new Label(infoText);

        VBox infoBox = new VBox(3);
        infoBox.getChildren().addAll(nomeLabel, infoLabel);

        this.getChildren().addAll(checkGemeo, infoBox);
    }

    private void setupLayout() {
        this.setSpacing(10);
        this.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    }

    private void applyStyles() {
        updateCardStyle();
    }

    private void updateCardStyle() {
        if (checkGemeo.isSelected()) {
            this.setStyle("-fx-background-color: #fff3cd; " +
                    "-fx-border-color: #ffc107; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 5; " +
                    "-fx-padding: 10;");
        } else if (irmao.getPossuiIrmaoGemeo() != null && irmao.getPossuiIrmaoGemeo()) {
            this.setStyle("-fx-background-color: #e7f3ff; " +
                    "-fx-border-color: #0dcaf0; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 5; " +
                    "-fx-padding: 10;");
        } else {
            this.setStyle("-fx-background-color: #f8f8f8; " +
                    "-fx-border-color: #c8e1e6; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 5; " +
                    "-fx-padding: 10;");
        }
    }

    private void setupEventHandlers() {
        checkGemeo.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateCardStyle();
            if (newValue) {
                if (onGemeoSelected != null) {
                    onGemeoSelected.run();
                }
            } else {
                if (onGemeoDeselected != null) {
                    onGemeoDeselected.run();
                }
            }
        });
    }

    private String calcularIdade() {
        if (irmao.getDataNascimento() == null) return "0";

        LocalDate nascimento = irmao.getDataNascimento().toLocalDate();
        LocalDate hoje = LocalDate.now();

        Period periodo = Period.between(nascimento, hoje);
        return String.valueOf(periodo.getYears());
    }

    // MÉTODOS PARA CONTROLE EXTERNO
    public void setSelecionado(boolean selecionado) {
        checkGemeo.setSelected(selecionado);
        updateCardStyle();
    }

    public boolean isSelecionado() {
        return checkGemeo.isSelected();
    }

    public Crianca getIrmao() {
        return irmao;
    }

    public void setOnGemeoSelected(Runnable onGemeoSelected) {
        this.onGemeoSelected = onGemeoSelected;
    }

    public void setOnGemeoDeselected(Runnable onGemeoDeselected) {
        this.onGemeoDeselected = onGemeoDeselected;
    }

    // MÉTODO PARA DESABILITAR/SELECIONAR
    public void setDesabilitado(boolean desabilitado) {
        checkGemeo.setDisable(desabilitado);
        if (desabilitado) {
            this.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #bdbdbd; -fx-border-radius: 5; -fx-padding: 10;");
        } else {
            updateCardStyle();
        }
    }
}