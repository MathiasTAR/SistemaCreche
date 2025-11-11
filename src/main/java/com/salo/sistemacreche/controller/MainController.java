package com.salo.sistemacreche.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;

    // Referência ao CONTROLLER do elemento incluído
    @FXML public NavigationController navigationController;

    private Parent telaHome;
    private Parent telaListaMatriculas;
    private Parent telaCadastroMatricula;
    private Parent telaRematricula;
    private Parent telaRelatorios;
    private Parent telaDeclaracoes;

    @FXML
    public void initialize() {
        System.out.println("🚀 MainController inicializado!");
        System.out.println("🔗 NavigationController injetado: " + (navigationController != null));

        // Agora o navigation está disponível!
        if (navigationController != null) {
            navigationController.setMainController(this);
        }

        carregarTelas();
        mostrarTelaHome();
    }

    private void carregarTelas() {
        try {
            // Carregar tela Home
            FXMLLoader loaderHome = new FXMLLoader(
                    getClass().getResource("/com/salo/sistemacreche/home.fxml")
            );
            telaHome = loaderHome.load();

            // Carregar tela de lista de matrículas
            FXMLLoader loaderLista = new FXMLLoader(
                    getClass().getResource("/com/salo/sistemacreche/lista-matriculas.fxml")
            );
            telaListaMatriculas = loaderLista.load();

            // Carregar tela de cadastro de matrícula
            FXMLLoader loaderCadastro = new FXMLLoader(
                    getClass().getResource("/com/salo/sistemacreche/cadastro-matricula.fxml")
            );
            telaCadastroMatricula = loaderCadastro.load();

            // Carregar tela de re-matrícula
            FXMLLoader loaderRematricula = new FXMLLoader(
                    getClass().getResource("/com/salo/sistemacreche/rematricula.fxml")
            );
            telaRematricula = loaderRematricula.load();

            // Carregar tela de relatórios
            FXMLLoader loaderRelatorios = new FXMLLoader(
                    getClass().getResource("/com/salo/sistemacreche/relatorios.fxml")
            );
            telaRelatorios = loaderRelatorios.load();

            // Carregar tela de declarações
            FXMLLoader loaderDeclaracoes = new FXMLLoader(
                    getClass().getResource("/com/salo/sistemacreche/declaracoes.fxml")
            );
            telaDeclaracoes = loaderDeclaracoes.load();

            System.out.println("✅ Todas as telas carregadas!");

        } catch (IOException e) {
            System.err.println("❌ Erro ao carregar telas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Métodos públicos para navegação
    public void mostrarTelaHome() {
        contentArea.getChildren().setAll(telaHome);
        System.out.println("📋 Mostrando Home");
    }

    // Métodos públicos para navegação
    public void mostrarTelaListaMatriculas() {
        contentArea.getChildren().setAll(telaListaMatriculas);
        System.out.println("📋 Mostrando lista de matrículas");
    }

    public void mostrarTelaCadastroMatricula() {
        contentArea.getChildren().setAll(telaCadastroMatricula);
        System.out.println("➕ Mostrando cadastro de matrícula");
    }

    public void mostrarTelaRematricula() {
        contentArea.getChildren().setAll(telaRematricula);
        System.out.println("🔄 Mostrando re-matrícula");
    }

    public void mostrarTelaRelatorios() {
        contentArea.getChildren().setAll(telaRelatorios);
        System.out.println("📊 Mostrando relatórios");
    }

    public void mostrarTelaDeclaracoes() {
        contentArea.getChildren().setAll(telaDeclaracoes);
        System.out.println("📄 Mostrando declarações");
    }
}