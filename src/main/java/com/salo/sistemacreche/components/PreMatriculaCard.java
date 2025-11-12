package com.salo.sistemacreche.components;

import com.salo.sistemacreche.entidades.PreMatricula;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;


import java.text.SimpleDateFormat;

public class PreMatriculaCard extends VBox {

    private PreMatricula preMatricula;
    private Runnable onAprovarAction;
    private Runnable onReprovarAction;
    private Runnable onCancelarAction;
    private Runnable onEditAction;

    // Componentes
    private Label labelNome;
    private Label labelSituacao;
    private Label labelData;
    private Button btnEditar;
    private Button btnAprovar;
    private Button btnReprovar;
    private Button btnCancelar;
    private VBox boxAcoes; // área que aparece quando clica em "Editar"

    public PreMatriculaCard(PreMatricula preMatricula) {
        this.preMatricula = preMatricula;
        initializeComponents();
        setupLayout();
        applyStyles();
        configurarBotoesPorSituacao();
    }

    private void initializeComponents() {
        // Nome da criança
        labelNome = new Label(preMatricula.getCrianca() != null
                ? preMatricula.getCrianca().getNome()
                : "Nome não disponível");
        labelNome.setPrefWidth(300.0);

        // Situação
        labelSituacao = new Label(traduzirSituacao(preMatricula.getSituacaoPreMatricula().toString()));

        // Data
        labelData = new Label();
        if (preMatricula.getDataPreMatricula() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            labelData.setText("Data da solicitação: " + sdf.format(preMatricula.getDataPreMatricula()));
        } else {
            labelData.setText("Data da solicitação: Não informada");
        }

        // Botão editar
        btnEditar = new Button("Editar");
        btnEditar.setOnAction(e -> toggleAcoesVisiveis());

        // Botões de ação
        btnAprovar = new Button("Aprovar");
        btnAprovar.setOnAction(e -> {
            if (onAprovarAction != null) onAprovarAction.run();
        });

        btnReprovar = new Button("Reprovar");
        btnReprovar.setOnAction(e -> {
            if (onReprovarAction != null) onReprovarAction.run();
        });

        btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> {
            if (onCancelarAction != null) onCancelarAction.run();
        });

        // Caixa de botões (oculta inicialmente)
        boxAcoes = new VBox(8, btnAprovar, btnReprovar, btnCancelar);
        boxAcoes.setVisible(false);
        boxAcoes.setManaged(false); // não ocupa espaço quando oculta
        boxAcoes.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
    }

    private void setupLayout() {
        // Linha superior
        HBox linhaSuperior = new HBox();
        linhaSuperior.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        linhaSuperior.setSpacing(10.0);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        linhaSuperior.getChildren().addAll(labelNome, region, labelSituacao, btnEditar);

        // Linha do meio
        HBox linhaMeio = new HBox();
        linhaMeio.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        linhaMeio.setSpacing(20.0);
        linhaMeio.getChildren().add(labelData);

        // Adiciona tudo ao card
        this.getChildren().addAll(linhaSuperior, linhaMeio, boxAcoes);
        this.setSpacing(10.0);
    }

    private void toggleAcoesVisiveis() {
        boolean novaVisibilidade = !boxAcoes.isVisible();
        boxAcoes.setVisible(novaVisibilidade);
        boxAcoes.setManaged(novaVisibilidade);
    }

    private void applyStyles() {
        // Card
        this.setStyle("-fx-background-color: white; -fx-border-color: #e8f5e8; "
                + "-fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        labelNome.setStyle("-fx-text-fill: #2e7d32;");
        labelNome.setFont(Font.font("System Bold", 14.0));

        labelData.setStyle("-fx-text-fill: #666;");

        aplicarEstiloSituacao();

        // Botões principais
        btnEditar.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; "
                + "-fx-background-radius: 5; -fx-padding: 5 10;");

        btnAprovar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; "
                + "-fx-background-radius: 5; -fx-padding: 6 12;");
        btnReprovar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; "
                + "-fx-background-radius: 5; -fx-padding: 6 12;");
        btnCancelar.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; "
                + "-fx-background-radius: 5; -fx-padding: 6 12;");
    }

    private void aplicarEstiloSituacao() {
        String situacao = preMatricula.getSituacaoPreMatricula().toString();
        switch (situacao) {
            case "APROVADA" -> labelSituacao.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold; "
                    + "-fx-background-color: #e8f5e8; -fx-padding: 2 8; -fx-background-radius: 10;");
            case "EM_ANALISE" -> labelSituacao.setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold; "
                    + "-fx-background-color: #fff3e0; -fx-padding: 2 8; -fx-background-radius: 10;");
            case "REPROVADA" -> labelSituacao.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold; "
                    + "-fx-background-color: #ffebee; -fx-padding: 2 8; -fx-background-radius: 10;");
            case "CANCELADA" -> labelSituacao.setStyle("-fx-text-fill: #9e9e9e; -fx-font-weight: bold; "
                    + "-fx-background-color: #f5f5f5; -fx-padding: 2 8; -fx-background-radius: 10;");
            default -> labelSituacao.setStyle("-fx-text-fill: #666; -fx-font-weight: bold;");
        }
    }

    private void configurarBotoesPorSituacao() {
        String situacao = preMatricula.getSituacaoPreMatricula().toString();

        btnAprovar.setVisible(true);
        btnReprovar.setVisible(true);
        btnCancelar.setVisible(true);

        switch (situacao) {
            case "APROVADA", "REPROVADA" -> {
                btnAprovar.setVisible(false);
                btnReprovar.setVisible(false);
            }
            case "CANCELADA" -> {
                btnAprovar.setVisible(false);
                btnReprovar.setVisible(false);
                btnCancelar.setVisible(false);
            }
        }
    }

    private String traduzirSituacao(String situacao) {
        return switch (situacao) {
            case "APROVADA" -> "Aprovada";
            case "EM_ANALISE" -> "Em Análise";
            case "REPROVADA" -> "Reprovada";
            case "CANCELADA" -> "Cancelada";
            default -> situacao;
        };
    }

    // Getters e Setters
    public void setOnEditAction(Runnable onEditAction) {
        this.onEditAction = onEditAction;
    }

    public void setOnAprovarAction(Runnable onAprovarAction) {
        this.onAprovarAction = onAprovarAction;
    }

    public void setOnReprovarAction(Runnable onReprovarAction) {
        this.onReprovarAction = onReprovarAction;
    }

    public void setOnCancelarAction(Runnable onCancelarAction) {
        this.onCancelarAction = onCancelarAction;
    }

    private void updateContent() {
        labelNome.setText(preMatricula.getCrianca() != null
                ? preMatricula.getCrianca().getNome()
                : "Nome não disponível");

        labelSituacao.setText(traduzirSituacao(preMatricula.getSituacaoPreMatricula().toString()));

        if (preMatricula.getDataPreMatricula() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            labelData.setText("Data da solicitação: " + sdf.format(preMatricula.getDataPreMatricula()));
        } else {
            labelData.setText("Data da solicitação: Não informada");
        }

        aplicarEstiloSituacao();
        configurarBotoesPorSituacao();
    }
}
