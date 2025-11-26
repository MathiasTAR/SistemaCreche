package com.salo.sistemacreche.components;

import com.salo.sistemacreche.entidades.Matricula;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Optional;

public class MatriculaCard extends VBox {

    private Matricula matricula;
    private Runnable onEditAction;
    private Runnable onConcluirAction;
    private Runnable onCancelarAction;

    // Componentes do card
    private Label labelNome;
    private Label labelSituacao;
    private Label labelData;
    private Label labelSerieAno;
    private Button btnEditar;
    private Button btnConcluir;
    private Button btnCancelar;
    private VBox boxAcoes;

    public MatriculaCard(Matricula matricula) {
        this.matricula = matricula;
        initializeComponents();
        setupLayout();
        applyStyles();
        configurarBotoesPorSituacao();
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

        // Série e Ano
        labelSerieAno = new Label();
        StringBuilder serieAnoText = new StringBuilder();
        if (matricula.getSerie() != null && !matricula.getSerie().isEmpty()) {
            serieAnoText.append("Série: ").append(matricula.getSerie());
        }
        if (matricula.getAnoLetivo() != null) {
            if (serieAnoText.length() > 0) serieAnoText.append(" | ");
            serieAnoText.append("Ano: ").append(matricula.getAnoLetivo());
        }
        if (serieAnoText.length() == 0) {
            labelSerieAno.setText("Série/Ano: Não informado");
        } else {
            labelSerieAno.setText(serieAnoText.toString());
        }

        // Botão Editar
        btnEditar = new Button("Editar");
        btnEditar.setOnAction(e -> toggleAcoesVisiveis());

        // Botões de ação COM CONFIRMAÇÃO
        btnConcluir = new Button("Concluir");
        btnConcluir.setOnAction(e -> confirmarConclusao());

        btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> confirmarCancelamento());

        // Caixa de botões (oculta inicialmente)
        boxAcoes = new VBox(8, btnConcluir, btnCancelar);
        boxAcoes.setVisible(false);
        boxAcoes.setManaged(false);
        boxAcoes.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
    }

    // MÉTODOS ESPECÍFICOS PARA CADA AÇÃO (CORREÇÃO)
    private void confirmarConclusao() {
        if (onConcluirAction == null) {
            System.err.println("❌ onConcluirAction não está configurado");
            return;
        }

        String nomeCrianca = matricula.getCrianca() != null ?
                matricula.getCrianca().getNome() : "esta matrícula";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Conclusão");
        alert.setHeaderText(null);
        alert.setContentText("Tem certeza que deseja CONCLUIR a matrícula de " + nomeCrianca + "?\n\n" +
                "Esta ação irá:\n" +
                "• Marcar a matrícula como 'Concluída'\n" +
                "• Finalizar o vínculo com a creche");

        ButtonType btnSim = new ButtonType("Sim, Confirmar");
        ButtonType btnNao = new ButtonType("Não, Cancelar");
        alert.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == btnSim) {
            System.out.println("✅ Confirmação recebida para CONCLUSÃO da matrícula: " + nomeCrianca);
            onConcluirAction.run();
            toggleAcoesVisiveis();
        } else {
            System.out.println("❌ Conclusão cancelada pelo usuário");
        }
    }

    private void confirmarCancelamento() {
        if (onCancelarAction == null) {
            System.err.println("❌ onCancelarAction não está configurado");
            return;
        }

        String nomeCrianca = matricula.getCrianca() != null ?
                matricula.getCrianca().getNome() : "esta matrícula";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cancelamento");
        alert.setHeaderText(null);
        alert.setContentText("Tem certeza que deseja CANCELAR a matrícula de " + nomeCrianca + "?\n\n" +
                "Esta ação irá:\n" +
                "• Marcar a matrícula como 'Cancelada'\n" +
                "• Encerrar o vínculo com a creche");

        ButtonType btnSim = new ButtonType("Sim, Confirmar");
        ButtonType btnNao = new ButtonType("Não, Cancelar");
        alert.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == btnSim) {
            System.out.println("✅ Confirmação recebida para CANCELAMENTO da matrícula: " + nomeCrianca);
            onCancelarAction.run();
            toggleAcoesVisiveis();
        } else {
            System.out.println("❌ Cancelamento cancelado pelo usuário");
        }
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
        linhaMeio.getChildren().addAll(labelData, labelSerieAno);

        // Adiciona tudo ao card
        this.getChildren().addAll(linhaSuperior, linhaMeio, boxAcoes);
        this.setSpacing(10.0);
    }

    private void toggleAcoesVisiveis() {
        boolean novaVisibilidade = !boxAcoes.isVisible();

        if (novaVisibilidade) {
            // Torna visível antes da animação
            boxAcoes.setVisible(true);
            boxAcoes.setManaged(true);

            // Transição de aparecimento (fade + deslizar)
            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), boxAcoes);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            TranslateTransition slideDown = new TranslateTransition(Duration.millis(250), boxAcoes);
            slideDown.setFromY(-5);
            slideDown.setToY(0);

            fadeIn.play();
            slideDown.play();
        } else {
            // Transição de saída (fade + deslizar)
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), boxAcoes);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            TranslateTransition slideUp = new TranslateTransition(Duration.millis(200), boxAcoes);
            slideUp.setFromY(0);
            slideUp.setToY(-5);

            fadeOut.setOnFinished(e -> {
                boxAcoes.setVisible(false);
                boxAcoes.setManaged(false);
            });

            fadeOut.play();
            slideUp.play();
        }
    }

    private void applyStyles() {
        // Estilo do card principal
        this.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e8f5e8; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 15; " +
                "-fx-cursor: hand;");

        // Estilo dos labels
        labelNome.setStyle("-fx-text-fill: #2e7d32;");
        labelNome.setFont(Font.font("System Bold", 14.0));

        labelData.setStyle("-fx-text-fill: #666;");
        labelSerieAno.setStyle("-fx-text-fill: #666;");

        aplicarEstiloSituacao();

        // Estilo do botão editar
        btnEditar.setStyle("-fx-background-color: #0f766e; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 5 10; " +
                "-fx-cursor: hand;");

        // Estilos específicos para cada botão de ação
        btnConcluir.setStyle("-fx-background-color: #0f766e; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 6 12; " +
                "-fx-cursor: hand;");

        btnCancelar.setStyle("-fx-background-color: #0f766e; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 6 12; " +
                "-fx-cursor: hand;");
    }

    private void aplicarEstiloSituacao() {
        String situacao = matricula.getSituacaoMatricula().toString();
        switch (situacao) {
            case "ATIVA" -> labelSituacao.setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold; "
                    + "-fx-background-color: #e8f5e8; -fx-padding: 2 8; -fx-background-radius: 10;");
            case "CONCLUIDA" -> labelSituacao.setStyle("-fx-text-fill: #2196f3; -fx-font-weight: bold; "
                    + "-fx-background-color: #e3f2fd; -fx-padding: 2 8; -fx-background-radius: 10;");
            case "CANCELADA" -> labelSituacao.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold; "
                    + "-fx-background-color: #ffebee; -fx-padding: 2 8; -fx-background-radius: 10;");
            case "VENCIDA" -> labelSituacao.setStyle("-fx-text-fill: #9e9e9e; -fx-font-weight: bold; "
                    + "-fx-background-color: #f5f5f5; -fx-padding: 2 8; -fx-background-radius: 10;");
            default -> labelSituacao.setStyle("-fx-text-fill: #666; -fx-font-weight: bold;");
        }
    }

    private void configurarBotoesPorSituacao() {
        String situacao = matricula.getSituacaoMatricula().toString();

        // Por padrão, mostrar todos os botões
        btnConcluir.setVisible(true);
        btnCancelar.setVisible(true);

        switch (situacao) {
            case "CONCLUIDA" -> {
                btnConcluir.setVisible(false);
                btnConcluir.setDisable(true);
            }
            case "CANCELADA" -> {
                btnCancelar.setVisible(false);
                btnCancelar.setDisable(true);
            }
            case "VENCIDA" -> {
                btnConcluir.setVisible(false);
                btnCancelar.setVisible(false);
                btnConcluir.setDisable(true);
                btnCancelar.setDisable(true);
            }
        }
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

    public void setOnConcluirAction(Runnable onConcluirAction) {
        this.onConcluirAction = onConcluirAction;
        System.out.println("✅ onConcluirAction configurado para: " +
                (matricula.getCrianca() != null ? matricula.getCrianca().getNome() : "matrícula"));
    }

    public void setOnCancelarAction(Runnable onCancelarAction) {
        this.onCancelarAction = onCancelarAction;
        System.out.println("✅ onCancelarAction configurado para: " +
                (matricula.getCrianca() != null ? matricula.getCrianca().getNome() : "matrícula"));
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

        // Atualizar série e ano
        StringBuilder serieAnoText = new StringBuilder();
        if (matricula.getSerie() != null && !matricula.getSerie().isEmpty()) {
            serieAnoText.append("Série: ").append(matricula.getSerie());
        }
        if (matricula.getAnoLetivo() != null) {
            if (serieAnoText.length() > 0) serieAnoText.append(" | ");
            serieAnoText.append("Ano: ").append(matricula.getAnoLetivo());
        }
        if (serieAnoText.length() == 0) {
            labelSerieAno.setText("Série/Ano: Não informado");
        } else {
            labelSerieAno.setText(serieAnoText.toString());
        }

        aplicarEstiloSituacao();
        configurarBotoesPorSituacao();
    }
}