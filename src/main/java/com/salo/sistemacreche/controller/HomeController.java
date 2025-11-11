package com.salo.sistemacreche.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeController {

    @FXML
    private Label labelPreMatriculas;

    @FXML
    private Label labelMatriculasAtivas;

    @FXML
    private Label labelRelatoriosFeitos;

    @FXML
    public void initialize() {
        System.out.println("🏠 HomeController inicializado!");

        // Você pode inicializar dados aqui se necessário
        // Por exemplo, carregar dados do banco de dados
        inicializarDados();
    }

    private void inicializarDados() {
        // Aqui você pode carregar dados reais do banco de dados
        // Por enquanto, vamos usar os valores fixos do FXML

        System.out.println("📊 Inicializando dados da home...");

        // Exemplo: se você quiser atualizar os valores dinamicamente
        // labelPreMatriculas.setText("75");
        // labelMatriculasAtivas.setText("135");
        // labelRelatoriosFeitos.setText("42");
    }

    // Métodos para os botões "Editar" das pré-matrículas
    @FXML
    private void editarPreMatricula1() {
        System.out.println("✏️ Editando pré-matrícula: Maria Vitória da Silva");
        // TODO: Implementar lógica para editar pré-matrícula
    }

    @FXML
    private void editarPreMatricula2() {
        System.out.println("✏️ Editando pré-matrícula: Jorge Augusto Barros");
        // TODO: Implementar lógica para editar pré-matrícula
    }

    @FXML
    private void editarPreMatricula3() {
        System.out.println("✏️ Editando pré-matrícula: Evesson Ribeiro da Cunha");
        // TODO: Implementar lógica para editar pré-matrícula
    }

    // Métodos para os botões "Editar" das matrículas
    @FXML
    private void editarMatricula1() {
        System.out.println("✏️ Editando matrícula: Edimilson Soares Costa");
        // TODO: Implementar lógica para editar matrícula
    }

    @FXML
    private void editarMatricula2() {
        System.out.println("✏️ Editando matrícula: Edson Costa da Silva");
        // TODO: Implementar lógica para editar matrícula
    }

    @FXML
    private void editarMatricula3() {
        System.out.println("✏️ Editando matrícula: Anne Gabriele Alves");
        // TODO: Implementar lógica para editar matrícula
    }

    // Métodos para os botões do relatório
    @FXML
    private void verRelatorio() {
        System.out.println("👁️ Visualizando relatório: Crianças com Deficiência Auditiva");
        // TODO: Implementar visualização do relatório
    }

    @FXML
    private void imprimirRelatorio() {
        System.out.println("🖨️ Imprimindo relatório: Crianças com Deficiência Auditiva");
        // TODO: Implementar impressão do relatório
    }

    // Método para atualizar os dados da home (pode ser chamado externamente)
    public void atualizarDados(int preMatriculas, int matriculasAtivas, int relatoriosFeitos) {
        if (labelPreMatriculas != null) {
            labelPreMatriculas.setText(String.valueOf(preMatriculas));
        }
        if (labelMatriculasAtivas != null) {
            labelMatriculasAtivas.setText(String.valueOf(matriculasAtivas));
        }
        if (labelRelatoriosFeitos != null) {
            labelRelatoriosFeitos.setText(String.valueOf(relatoriosFeitos));
        }

        System.out.println("🔄 Dados da home atualizados: " +
                "Pré-matrículas=" + preMatriculas +
                ", Matrículas Ativas=" + matriculasAtivas +
                ", Relatórios=" + relatoriosFeitos);
    }
}