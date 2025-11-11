package com.salo.sistemacreche.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NavigationController {

    // Constantes de estilo
    private static final String EstiloNormal =
            "-fx-background-color: transparent; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-cursor: hand;" +
                    "-fx-alignment: start;";

    private static final String EstiloAtivo =
            "-fx-background-color: #0f766e; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #0c5a55; " +
                    "-fx-border-width: 2px; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-border-radius: 5px; " +
                    "-fx-cursor: hand;" +
                    "-fx-alignment: start;";


    @FXML private Button btnListaMatriculas;
    @FXML private Button btnCadastrarMatricula;
    @FXML private Button btnRematricula;
    @FXML private Button btnRelatorios;
    @FXML private Button btnDeclaracoes;
    @FXML private Button btnHome;

    private MainController mainController;

    // Este método é chamado automaticamente pelo JavaFX quando o FXML é carregado via <fx:include>
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        configurarBotoesMenu();
        aplicarEstiloInicial();
    }

    private void configurarBotoesMenu() {
        btnHome.setOnAction(e -> navegarParaHome());
        btnListaMatriculas.setOnAction(e -> navegarParaListaMatriculas());
        btnCadastrarMatricula.setOnAction(e -> navegarParaCadastroMatricula());
        btnRematricula.setOnAction(e -> navegarParaRematricula());
        btnRelatorios.setOnAction(e -> navegarParaRelatorios());
        btnDeclaracoes.setOnAction(e -> navegarParaDeclaracoes());
    }

    private void navegarParaHome() {
        if (mainController != null) {
            mainController.mostrarTelaHome();
            atualizarBotaoAtivo(btnHome);
        }
    }

    private void navegarParaListaMatriculas() {
        if (mainController != null) {
            mainController.mostrarTelaListaMatriculas();
            atualizarBotaoAtivo(btnListaMatriculas);
        }
    }

    private void navegarParaCadastroMatricula() {
        if (mainController != null) {
            mainController.mostrarTelaCadastroMatricula();
            atualizarBotaoAtivo(btnCadastrarMatricula);
        }
    }

    private void navegarParaRematricula() {
        if (mainController != null) {
            mainController.mostrarTelaRematricula();
            atualizarBotaoAtivo(btnRematricula);
        }
    }

    private void navegarParaRelatorios() {
        if (mainController != null) {
            mainController.mostrarTelaRelatorios();
            atualizarBotaoAtivo(btnRelatorios);
        }
    }

    private void navegarParaDeclaracoes() {
        if (mainController != null) {
            mainController.mostrarTelaDeclaracoes();
            atualizarBotaoAtivo(btnDeclaracoes);
        }
    }

    private void aplicarEstiloInicial() {
        // Aplica estilo normal a todos os botões inicialmente
        btnHome.setStyle(EstiloNormal);
        btnListaMatriculas.setStyle(EstiloNormal);
        btnCadastrarMatricula.setStyle(EstiloNormal);
        btnRematricula.setStyle(EstiloNormal);
        btnRelatorios.setStyle(EstiloNormal);
        btnDeclaracoes.setStyle(EstiloNormal);

        btnHome.setStyle(EstiloAtivo);
    }

    public void atualizarBotaoAtivo(Button botaoAtivo) {
        btnHome.setStyle(EstiloNormal);
        btnListaMatriculas.setStyle(EstiloNormal);
        btnCadastrarMatricula.setStyle(EstiloNormal);
        btnRematricula.setStyle(EstiloNormal);
        btnRelatorios.setStyle(EstiloNormal);
        btnDeclaracoes.setStyle(EstiloNormal);

        // Adiciona estilo ativo ao botão clicado
        botaoAtivo.setStyle(EstiloAtivo);
    }
}

