package com.salo.sistemacreche.components;

import com.salo.sistemacreche.entidades.PreMatricula;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.text.SimpleDateFormat;

public class PreMatriculaCard extends VBox {

    private PreMatricula preMatricula;
    private Runnable onEditAction;
    private Runnable onAprovarAction;
    private Runnable onReprovarAction;
    private Runnable onCancelarAction;

    // Componentes do card
    private Label labelNome;
    private Label labelSituacao;
    private Label labelData;
    private Label labelSituacaoHabitacional;
    private Button btnEditar;
    private Button btnAprovar;
    private Button btnReprovar;
    private Button btnCancelar;

    public PreMatriculaCard(PreMatricula preMatricula) {
        this.preMatricula = preMatricula;
        initializeComponents();
        setupLayout();
        applyStyles();
        configurarBotoesPorSituacao();
    }

    private void initializeComponents() {
        // Nome da criança
        labelNome = new Label();
        if (preMatricula.getCrianca() != null && preMatricula.getCrianca().getNome() != null) {
            labelNome.setText(preMatricula.getCrianca().getNome());
        } else {
            labelNome.setText("Nome não disponível");
        }
        labelNome.setPrefWidth(300.0);

        // Situação da pré-matrícula
        labelSituacao = new Label();
        String situacao = preMatricula.getSituacaoPreMatricula().toString();
        String situacaoTraduzida = traduzirSituacao(situacao);
        labelSituacao.setText(situacaoTraduzida);

        // Data da pré-matrícula
        labelData = new Label();
        if (preMatricula.getDataPreMatricula() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(preMatricula.getDataPreMatricula());
            labelData.setText("Data da solicitação: " + dataFormatada);
        } else {
            labelData.setText("Data da solicitação: Não informada");
        }

        // Situação habitacional - CORREÇÃO AQUI
        labelSituacaoHabitacional = new Label();
        if (preMatricula.getSituacaoHabitacional() != null) {
            // Verifica o tipo da situação habitacional
            Object situacaoHabitacional = preMatricula.getSituacaoHabitacional();
            if (situacaoHabitacional instanceof String) {
                labelSituacaoHabitacional.setText("Habitação: " + situacaoHabitacional);
            } else if (situacaoHabitacional instanceof Integer) {
                String descricao = traduzirSituacaoHabitacional((Integer) situacaoHabitacional);
                labelSituacaoHabitacional.setText("Habitação: " + descricao);
            } else {
                // Tenta usar toString() como fallback
                labelSituacaoHabitacional.setText("Habitação: " + situacaoHabitacional.toString());
            }
        } else {
            labelSituacaoHabitacional.setText("Habitação: Não informada");
        }

        // Botões de ação
        btnEditar = new Button("Editar");
        btnEditar.setOnAction(e -> {
            if (onEditAction != null) {
                onEditAction.run();
            }
        });

        btnAprovar = new Button("Aprovar");
        btnAprovar.setOnAction(e -> {
            if (onAprovarAction != null) {
                onAprovarAction.run();
            }
        });

        btnReprovar = new Button("Reprovar");
        btnReprovar.setOnAction(e -> {
            if (onReprovarAction != null) {
                onReprovarAction.run();
            }
        });

        btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> {
            if (onCancelarAction != null) {
                onCancelarAction.run();
            }
        });
    }

    private void setupLayout() {
        // Linha superior - Informações principais
        HBox linhaSuperior = new HBox();
        linhaSuperior.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        linhaSuperior.setSpacing(10.0);

        // Espaço flexível
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        linhaSuperior.getChildren().addAll(labelNome, labelSituacao, region, btnEditar);

        // Linha do meio - Informações adicionais
        HBox linhaMeio = new HBox();
        linhaMeio.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        linhaMeio.setSpacing(20.0);
        linhaMeio.getChildren().addAll(labelData, labelSituacaoHabitacional);

        // Linha inferior - Botões de ação
        HBox linhaBotoes = new HBox();
        linhaBotoes.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        linhaBotoes.setSpacing(10.0);
        linhaBotoes.getChildren().addAll(btnAprovar, btnReprovar, btnCancelar);

        // Adiciona ao card
        this.getChildren().addAll(linhaSuperior, linhaMeio, linhaBotoes);
        this.setSpacing(8.0);
    }

    private void applyStyles() {
        // Estilo do card principal
        this.setStyle("-fx-background-color: white; -fx-border-color: #e8f5e8; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        // Estilo dos labels
        labelNome.setStyle("-fx-text-fill: #2e7d32;");
        labelNome.setFont(Font.font("System Bold", 14.0));

        // Estilo da situação baseado no status
        aplicarEstiloSituacao();

        labelData.setStyle("-fx-text-fill: #666;");
        labelSituacaoHabitacional.setStyle("-fx-text-fill: #666;");

        // Estilo dos botões
        btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 3; -fx-padding: 5 10;");
        btnAprovar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 3; -fx-padding: 5 10;");
        btnReprovar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-background-radius: 3; -fx-padding: 5 10;");
        btnCancelar.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 3; -fx-padding: 5 10;");
    }

    private void aplicarEstiloSituacao() {
        String situacao = preMatricula.getSituacaoPreMatricula().toString();
        switch(situacao) {
            case "APROVADA":
                labelSituacao.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold; -fx-background-color: #e8f5e8; -fx-padding: 2 8; -fx-background-radius: 10;");
                break;
            case "EM_ANALISE":
                labelSituacao.setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold; -fx-background-color: #fff3e0; -fx-padding: 2 8; -fx-background-radius: 10;");
                break;
            case "REPROVADA":
                labelSituacao.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold; -fx-background-color: #ffebee; -fx-padding: 2 8; -fx-background-radius: 10;");
                break;
            case "CANCELADA":
                labelSituacao.setStyle("-fx-text-fill: #9e9e9e; -fx-font-weight: bold; -fx-background-color: #f5f5f5; -fx-padding: 2 8; -fx-background-radius: 10;");
                break;
            default:
                labelSituacao.setStyle("-fx-text-fill: #666; -fx-font-weight: bold;");
        }
    }

    private void configurarBotoesPorSituacao() {
        String situacao = preMatricula.getSituacaoPreMatricula().toString();

        // Por padrão, mostra todos os botões
        btnAprovar.setVisible(true);
        btnReprovar.setVisible(true);
        btnCancelar.setVisible(true);
        btnEditar.setVisible(true);

        switch(situacao) {
            case "APROVADA":
                // Quando já está aprovada, só permite cancelar
                btnAprovar.setVisible(false);
                btnReprovar.setVisible(false);
                break;
            case "REPROVADA":
                // Quando já está reprovada, só permite cancelar
                btnAprovar.setVisible(false);
                btnReprovar.setVisible(false);
                break;
            case "CANCELADA":
                // Quando cancelada, não permite mais ações
                btnAprovar.setVisible(false);
                btnReprovar.setVisible(false);
                btnCancelar.setVisible(false);
                break;
            case "EM_ANALISE":
                // Em análise, permite todas as ações
                break;
        }
    }

    private String traduzirSituacao(String situacao) {
        switch(situacao) {
            case "APROVADA":
                return "Aprovada";
            case "EM_ANALISE":
                return "Em Análise";
            case "REPROVADA":
                return "Reprovada";
            case "CANCELADA":
                return "Cancelada";
            default:
                return situacao;
        }
    }

    // CORREÇÃO: Método para traduzir situação habitacional baseado em códigos numéricos
    private String traduzirSituacaoHabitacional(Integer codigo) {
        if (codigo == null) return "Não informada";

        switch(codigo) {
            case 1: return "Própria";
            case 2: return "Alugada";
            case 3: return "Cedida";
            case 4: return "Outra";
            default: return "Código " + codigo;
        }
    }

    // Getters e Setters
    public PreMatricula getPreMatricula() {
        return preMatricula;
    }

    public void setPreMatricula(PreMatricula preMatricula) {
        this.preMatricula = preMatricula;
        updateContent();
    }

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
        // Atualiza o conteúdo quando a pré-matrícula muda
        if (preMatricula.getCrianca() != null && preMatricula.getCrianca().getNome() != null) {
            labelNome.setText(preMatricula.getCrianca().getNome());
        } else {
            labelNome.setText("Nome não disponível");
        }

        String situacao = preMatricula.getSituacaoPreMatricula().toString();
        labelSituacao.setText(traduzirSituacao(situacao));

        if (preMatricula.getDataPreMatricula() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(preMatricula.getDataPreMatricula());
            labelData.setText("Data da solicitação: " + dataFormatada);
        } else {
            labelData.setText("Data da solicitação: Não informada");
        }

        // CORREÇÃO: Atualização da situação habitacional
        if (preMatricula.getSituacaoHabitacional() != null) {
            Object situacaoHabitacional = preMatricula.getSituacaoHabitacional();
            if (situacaoHabitacional instanceof String) {
                labelSituacaoHabitacional.setText("Habitação: " + situacaoHabitacional);
            } else if (situacaoHabitacional instanceof Integer) {
                String descricao = traduzirSituacaoHabitacional((Integer) situacaoHabitacional);
                labelSituacaoHabitacional.setText("Habitação: " + descricao);
            } else {
                labelSituacaoHabitacional.setText("Habitação: " + situacaoHabitacional.toString());
            }
        } else {
            labelSituacaoHabitacional.setText("Habitação: Não informada");
        }

        aplicarEstiloSituacao();
        configurarBotoesPorSituacao();
    }
}