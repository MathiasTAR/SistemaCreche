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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Optional;

public class RematriculaCard extends VBox {

    private Matricula matricula;
    private Runnable onRenovarAction;
    private Runnable onVisualizarAction;

    // Componentes do card
    private Label labelNome;
    private Label labelSituacao;
    private Label labelDataVencimento;
    private Label labelDiasVencidos;
    private Label labelSerieAno;
    private Button btnAcoes;
    private Button btnRenovar;
    private Button btnVisualizar;
    private VBox boxAcoes;

    public RematriculaCard(Matricula matricula) {
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
        labelNome.setPrefWidth(300.0);

        labelSituacao = new Label("Vencida");

        // Data de vencimento
        labelDataVencimento = new Label();
        labelDiasVencidos = new Label();

        if (matricula.getDataVencimento() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(matricula.getDataVencimento());

            // calcular dias vencidos
            long diasVencidos = calcularDiasVencidos();

            labelDataVencimento.setText("Vencida em: " + dataFormatada);
            labelDiasVencidos.setText("(" + diasVencidos + " dias vencidos)");
        } else {
            labelDataVencimento.setText("Data de vencimento: Não informada");
            labelDiasVencidos.setText("");
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

        // Botão Ações
        btnAcoes = new Button("Ações");
        btnAcoes.setOnAction(e -> toggleAcoesVisiveis());

        btnRenovar = new Button("Renovar");
        btnRenovar.setOnAction(e -> confirmarRenovacao());

        btnVisualizar = new Button("Visualizar");
        btnVisualizar.setOnAction(e -> {
            if (onVisualizarAction != null) onVisualizarAction.run();
            toggleAcoesVisiveis();
        });

        // Caixa de botões (oculta inicialmente)
        boxAcoes = new VBox(8, btnRenovar, btnVisualizar);
        boxAcoes.setVisible(false);
        boxAcoes.setManaged(false);
        boxAcoes.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
    }

    private long calcularDiasVencidos() {
        if (matricula.getDataVencimento() == null) return 0;

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(matricula.getDataVencimento());
            LocalDate dataVencimento = LocalDate.of(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)
            );

            LocalDate hoje = LocalDate.now();

            // Dias que passaram desde o vencimento
            return ChronoUnit.DAYS.between(dataVencimento, hoje);

        } catch (Exception e) {
            System.err.println("❌ Erro ao calcular dias vencidos: " + e.getMessage());
            return 0;
        }
    }

    private void confirmarRenovacao() {
        if (onRenovarAction == null) return;

        String nomeCrianca = matricula.getCrianca() != null ?
                matricula.getCrianca().getNome() : "esta matrícula";

        long diasVencidos = calcularDiasVencidos();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Renovação");
        alert.setHeaderText(null);
        alert.setContentText("Tem certeza que deseja RENOVAR a matrícula de " + nomeCrianca + "?\n\n" +
                "Esta ação irá:\n" +
                "Renovar a matrícula atual\n" +
                "Avançar série\n\n" +
                "Matrícula vencida há " + diasVencidos + " dias");

        ButtonType btnSim = new ButtonType("Sim, Renovar");
        ButtonType btnNao = new ButtonType("Não, Cancelar");
        alert.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == btnSim) {
            System.out.println("✅ Confirmação recebida para renovação: " + nomeCrianca);
            onRenovarAction.run();
            toggleAcoesVisiveis();
        } else {
            System.out.println("❌ Renovação cancelada pelo usuário");
        }
    }

    private void setupLayout() {
        // Linha superior
        HBox linhaSuperior = new HBox();
        linhaSuperior.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        linhaSuperior.setSpacing(10.0);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        linhaSuperior.getChildren().addAll(labelNome, region, labelSituacao, btnAcoes);

        // Linha do meio (data de vencimento e dias vencidos)
        HBox linhaMeio = new HBox();
        linhaMeio.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        linhaMeio.setSpacing(10.0);
        linhaMeio.getChildren().addAll(labelDataVencimento, labelDiasVencidos);

        // Linha inferior (série e ano)
        HBox linhaInferior = new HBox();
        linhaInferior.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        linhaInferior.setSpacing(20.0);
        linhaInferior.getChildren().add(labelSerieAno);

        // Adiciona tudo ao card
        this.getChildren().addAll(linhaSuperior, linhaMeio, linhaInferior, boxAcoes);
        this.setSpacing(8.0);
    }

    private void toggleAcoesVisiveis() {
        boolean novaVisibilidade = !boxAcoes.isVisible();

        if (novaVisibilidade) {
            boxAcoes.setVisible(true);
            boxAcoes.setManaged(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), boxAcoes);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            TranslateTransition slideDown = new TranslateTransition(Duration.millis(250), boxAcoes);
            slideDown.setFromY(-5);
            slideDown.setToY(0);

            fadeIn.play();
            slideDown.play();
        } else {
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
        // Card principal
        this.setStyle("-fx-background-color: white; " +
                "-fx-border-color: #e8f5e8; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 5; " +
                "-fx-padding: 15; " +
                "-fx-cursor: hand;");

        // Nome
        labelNome.setStyle("-fx-text-fill: #2e7d32;");
        labelNome.setFont(Font.font("System Bold", 14.0));

        // Informações secundárias
        labelDataVencimento.setStyle("-fx-text-fill: #666;");
        labelDiasVencidos.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
        labelSerieAno.setStyle("-fx-text-fill: #666;");

        // Situação - SEMPRE VENCIDA (laranja para destaque)
        labelSituacao.setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold; " +
                "-fx-background-color: #fff3e0; -fx-padding: 2 8; -fx-background-radius: 10;");

        // Botão Ações
        btnAcoes.setStyle("-fx-background-color: #0f766e; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 5 10; " +
                "-fx-cursor: hand;");

        // Botões de ação
        btnRenovar.setStyle("-fx-background-color: #0f766e; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 6 12; " +
                "-fx-cursor: hand;");

        btnVisualizar.setStyle("-fx-background-color: #0f766e; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 6 12; " +
                "-fx-cursor: hand;");
    }

    // Getters e Setters
    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
        updateContent();
    }

    public void setOnRenovarAction(Runnable onRenovarAction) {
        this.onRenovarAction = onRenovarAction;
    }

    public void setOnVisualizarAction(Runnable onVisualizarAction) {
        this.onVisualizarAction = onVisualizarAction;
    }

    private void updateContent() {
        // Atualiza o conteúdo quando a matrícula muda
        if (matricula.getCrianca() != null && matricula.getCrianca().getNome() != null) {
            labelNome.setText(matricula.getCrianca().getNome());
        } else {
            labelNome.setText("Nome não disponível");
        }

        // Atualizar data de vencimento e dias vencidos
        if (matricula.getDataVencimento() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(matricula.getDataVencimento());

            long diasVencidos = calcularDiasVencidos();

            labelDataVencimento.setText("Vencida em: " + dataFormatada);
            labelDiasVencidos.setText("(" + diasVencidos + " dias vencidos)");
        } else {
            labelDataVencimento.setText("Data de vencimento: Não informada");
            labelDiasVencidos.setText("");
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
    }
}