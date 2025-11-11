package com.salo.sistemacreche.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NavigationController {


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
        }
    }

    private void navegarParaListaMatriculas() {
        if (mainController != null) {
            mainController.mostrarTelaListaMatriculas();
        }
    }

    private void navegarParaCadastroMatricula() {
        if (mainController != null) {
            mainController.mostrarTelaCadastroMatricula();
            System.out.println("cadastrar matricula");
        }
    }

    private void navegarParaRematricula() {
        if (mainController != null) {
            mainController.mostrarTelaRematricula();
        }
    }

    private void navegarParaRelatorios() {
        if (mainController != null) {
            mainController.mostrarTelaRelatorios();
        }
    }

    private void navegarParaDeclaracoes() {
        if (mainController != null) {
            mainController.mostrarTelaDeclaracoes();
        }
    }

    // Método para atualizar o estilo do botão ativo
    public void atualizarBotaoAtivo(Button botaoAtivo) {
        // Remove estilo ativo de todos os botões
        btnHome.getStyleClass().remove("botao-ativo");
        btnListaMatriculas.getStyleClass().remove("botao-ativo");
        btnCadastrarMatricula.getStyleClass().remove("botao-ativo");
        btnRematricula.getStyleClass().remove("botao-ativo");
        btnRelatorios.getStyleClass().remove("botao-ativo");
        btnDeclaracoes.getStyleClass().remove("botao-ativo");

        // Adiciona estilo ativo ao botão clicado
        botaoAtivo.getStyleClass().add("botao-ativo");
    }
}