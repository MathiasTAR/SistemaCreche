package com.salo.sistemacreche.components;

import com.salo.sistemacreche.entidades.Responsavel;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class ResponsavelCard extends VBox {

    private Responsavel responsavel;
    private Runnable onEditAction;
    private Runnable onSelectAction;
    private Runnable onDeselectAction;
    private CheckBox checkSelecionar;
    private boolean selecionado = false;

    public ResponsavelCard(Responsavel responsavel) {
        this.responsavel = responsavel;
        initializeComponents();
        setupLayout();
        applyStyles();
        loadResponsavelData();
    }

    private void initializeComponents() {
        checkSelecionar = new CheckBox();
    }

    private void setupLayout() {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
    }

    private void applyStyles() {
        updateCardStyle();
    }

    private void updateCardStyle() {
        if (selecionado) {
            this.setStyle("-fx-background-color: #d4edda; " +
                    "-fx-border-color: #28a745; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 8; " +
                    "-fx-padding: 15; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(40,167,69,0.3), 5, 0, 0, 2);");
        } else {
            this.setStyle("-fx-background-color: #e8f5e8; " +
                    "-fx-border-color: #c8e1e6; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 8; " +
                    "-fx-padding: 15; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        }
    }

    private void loadResponsavelData() {
        this.getChildren().clear();

        // Checkbox de seleção
        checkSelecionar.setText("Selecionar");
        checkSelecionar.setSelected(selecionado);
        checkSelecionar.setStyle("-fx-text-fill: #0f766e; -fx-font-weight: bold;");

        checkSelecionar.selectedProperty().addListener((observable, oldValue, newValue) -> {
            setSelecionado(newValue);
        });

        // Nome do Responsável
        Label labelNome = new Label(responsavel.getPessoa().getNome());
        labelNome.setStyle("-fx-text-fill: #0f766e; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16;");

        // CPF do Responsável
        Label labelCpf = new Label("CPF: " + formatarCPF(responsavel.getPessoa().getCpf()));
        labelCpf.setStyle("-fx-text-fill: #666666; " +
                "-fx-font-size: 14;");

        // Telefone
//        Label labelTelefone = new Label("Tel: " +
//                (responsavel.getPessoa().getTelefone() != null ?
//                        responsavel.getPessoa().getTelefone() : "Não informado"));
//        labelTelefone.setStyle("-fx-text-fill: #666666; -fx-font-size: 14;");

        // Container principal para as informações
        VBox infoContainer = new VBox(5);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        infoContainer.getChildren().addAll(labelNome, labelCpf);

        // Botão de editar
        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: #0f766e; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 5 15 5 15; " +
                "-fx-cursor: hand;");

        btnEditar.setOnAction(e -> {
            if (onEditAction != null) {
                onEditAction.run();
            }
        });

        // Container horizontal para checkbox + informações
        HBox selectionContainer = new HBox(10);
        selectionContainer.setAlignment(Pos.CENTER_LEFT);
        selectionContainer.getChildren().addAll(checkSelecionar, infoContainer);

        // Container horizontal principal
        HBox mainContainer = new HBox();
        mainContainer.setSpacing(15);
        mainContainer.setAlignment(Pos.CENTER_LEFT);
        mainContainer.getChildren().addAll(selectionContainer, btnEditar);

        this.getChildren().add(mainContainer);
    }

    private String getTipoResponsavel() {
        if (responsavel.getTipoResponsavel() != null) {
            return responsavel.getTipoResponsavel().getTipoResponsavel();
        }
        return "Responsável";
    }

    private String formatarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf != null ? cpf : "Não informado";
        }

        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    // 🔥 MÉTODOS DE SELEÇÃO
    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
        checkSelecionar.setSelected(selecionado);
        updateCardStyle();

        if (selecionado && onSelectAction != null) {
            onSelectAction.run();
        } else if (!selecionado && onDeselectAction != null) {
            onDeselectAction.run();
        }
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public Responsavel getResponsavel() {
        return responsavel;
    }

    // 🔥 MÉTODOS PARA AÇÕES
    public void setOnEditAction(Runnable onEditAction) {
        this.onEditAction = onEditAction;
    }

    public void setOnSelectAction(Runnable onSelectAction) {
        this.onSelectAction = onSelectAction;
    }

    public void setOnDeselectAction(Runnable onDeselectAction) {
        this.onDeselectAction = onDeselectAction;
    }

    // Método para atualizar os dados do card
    public void updateResponsavel(Responsavel responsavel) {
        this.responsavel = responsavel;
        loadResponsavelData();
    }
}