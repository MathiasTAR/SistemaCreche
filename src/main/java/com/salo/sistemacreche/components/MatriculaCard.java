package com.salo.sistemacreche.components;

import com.salo.sistemacreche.entidades.Matricula;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.text.SimpleDateFormat;

public class MatriculaCard extends VBox {

    private Matricula matricula;
    private Runnable onEditAction;

    // Componentes do card
    private Label labelNome;
    private Label labelSituacao;
    private Label labelData;
    private Button btnEditar;

    public MatriculaCard(Matricula matricula) {
        this.matricula = matricula;
        initializeComponents();
        setupLayout();
        applyStyles();
    }

    private void initializeComponents() {
        // Nome da criança
        labelNome = new Label();
        if (matricula.getCrianca() != null && matricula.getCrianca().getNome() != null) {
            labelNome.setText(matricula.getCrianca().getNome());
        } else {
            labelNome.setText("Nome não disponível");
        }
        labelNome.setPrefWidth(400.0);

        // Situação da matrícula
        labelSituacao = new Label();
        String situacao = matricula.getSituacaoMatricula().toString();
        String situacaoTraduzida = traduzirSituacao(situacao);
        labelSituacao.setText(situacaoTraduzida);

        // Data do registro
        labelData = new Label();
        if (matricula.getDataMatricula() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(matricula.getDataMatricula());
            labelData.setText("Data do registro: " + dataFormatada);
        } else {
            labelData.setText("Data do registro: Não informada");
        }

        // Botão Editar
        btnEditar = new Button("Editar");
        btnEditar.setOnAction(e -> {
            if (onEditAction != null) {
                onEditAction.run();
            }
        });
    }

    private void setupLayout() {
        // Linha superior
        HBox linhaSuperior = new HBox();
        linhaSuperior.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        linhaSuperior.setSpacing(10.0);

        // Espaço flexível
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        linhaSuperior.getChildren().addAll(labelNome, labelSituacao, region, btnEditar);

        // Adiciona ao card
        this.getChildren().addAll(linhaSuperior, labelData);
        this.setSpacing(5.0);
    }

    private void applyStyles() {
        // Estilo do card principal
        this.setStyle("-fx-background-color: white; -fx-border-color: #e8f5e8; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 15;");

        // Estilo dos labels
        labelNome.setStyle("-fx-text-fill: #2e7d32;");
        labelNome.setFont(Font.font("System Bold", 14.0));

        labelSituacao.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");

        labelData.setStyle("-fx-text-fill: #666; -fx-padding: 5 0 0 0;");

        // Estilo do botão
        btnEditar.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-background-radius: 3;");
    }

    private String traduzirSituacao(String situacao) {
        switch(situacao) {
            case "ATIVA":
                return "Matriculado";
            case "TRANSFERIDA":
                return "Transferido";
            case "CONCLUIDA":
                return "Concluído";
            case "CANCELADA":
                return "Cancelado";
            case "VENCIDA":
                return "Vencida";
            default:
                return situacao;
        }
    }

    // Getters e Setters
    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
        updateContent();
    }

    public void setOnEditAction(Runnable onEditAction) {
        this.onEditAction = onEditAction;
    }

    private void updateContent() {
        // Atualiza o conteúdo quando a matrícula muda
        if (matricula.getCrianca() != null && matricula.getCrianca().getNome() != null) {
            labelNome.setText(matricula.getCrianca().getNome());
        } else {
            labelNome.setText("Nome não disponível");
        }

        String situacao = matricula.getSituacaoMatricula().toString();
        labelSituacao.setText(traduzirSituacao(situacao));

        if (matricula.getDataMatricula() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(matricula.getDataMatricula());
            labelData.setText("Data do registro: " + dataFormatada);
        } else {
            labelData.setText("Data do registro: Não informada");
        }
    }
}