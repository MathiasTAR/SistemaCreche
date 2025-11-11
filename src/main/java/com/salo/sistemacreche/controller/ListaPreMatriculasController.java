package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.components.EmptyCard;
import com.salo.sistemacreche.components.PreMatriculaCard;
import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.PreMatricula;
import com.salo.sistemacreche.entidades.PreMatricula.SituacaoPreMatricula;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ListaPreMatriculasController {

    @FXML private TextField fieldPesquisarPreMatricula;
    @FXML private ComboBox<String> comboSituacao;
    @FXML private Button buscarButton;
    @FXML private Button limparButton;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox cardsContainer;
    @FXML private HBox filtrosHBox;

    @FXML
    public void initialize() {
        configurarComboBox();
        carregarPreMatriculas();
        configurarListeners();
    }

    private void configurarComboBox() {
        comboSituacao.getItems().addAll("Todas", "Em Analise", "Aprovada", "Reprovada", "Cancelada");
        comboSituacao.setValue("Todas");
    }

    private void configurarListeners() {
        // Buscar automaticamente ao pressionar Enter no campo de pesquisa
        fieldPesquisarPreMatricula.setOnAction(event -> buscarPreMatriculas());

        // Buscar automaticamente ao alterar a situação
        comboSituacao.setOnAction(event -> buscarPreMatriculas());
    }

    private SituacaoPreMatricula converterStringParaSituacaoEnum(String situacao) {
        switch(situacao) {
            case "Aprovada": return SituacaoPreMatricula.APROVADA;
            case "Em Analise": return SituacaoPreMatricula.EM_ANALISE;
            case "Cancelada": return SituacaoPreMatricula.CANCELADA;
            case "Reprovada": return SituacaoPreMatricula.REPROVADA;
            default: return null; // Para "Todas"
        }
    }

    @FXML
    public void buscarPreMatriculas() {
        carregarPreMatriculasComFiltro();
    }

    @FXML
    public void limparFiltros() {
        fieldPesquisarPreMatricula.clear();
        comboSituacao.setValue("Todas");
        carregarPreMatriculas();
    }

    private void carregarPreMatriculas() {
        carregarPreMatriculasComFiltro();
    }

    private void carregarPreMatriculasComFiltro() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            if (em == null) {
                System.err.println("❌ Erro de conexão com o banco");
                mostrarMensagemErro("Erro de conexão com o banco de dados");
                return;
            }

            if (!em.isOpen()) {
                System.err.println("❌ EntityManager está fechado");
                mostrarMensagemErro("Conexão com o banco de dados está fechada");
                return;
            }

            System.out.println("🔍 Executando busca de pré-matrículas com filtros...");
            System.out.println("📊 Situação selecionada: " + comboSituacao.getValue());

            // Usando JPQL para buscar pré-matrículas
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT pm FROM PreMatricula pm " +
                            "LEFT JOIN FETCH pm.crianca c " +
                            "LEFT JOIN FETCH pm.situacaoHabitacional sh " +
                            "WHERE 1 = 1"
            );

            List<Object> parametros = new ArrayList<>();
            int paramIndex = 1;

            // FILTRO 1: Pesquisa por texto (nome da criança)
            String termoPesquisa = fieldPesquisarPreMatricula.getText();
            if (termoPesquisa != null && !termoPesquisa.trim().isEmpty()) {
                jpql.append(" AND (LOWER(c.nome) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(c.nomeMae) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(c.nomePai) LIKE ?").append(paramIndex);
                jpql.append(" OR CAST(pm.id AS string) LIKE ?").append(paramIndex).append(")");
                parametros.add("%" + termoPesquisa.toLowerCase() + "%");
                paramIndex++;
            }

            // FILTRO 2: Situação da pré-matrícula
            String situacaoSelecionada = comboSituacao.getValue();
            if (situacaoSelecionada != null && !situacaoSelecionada.equals("Todas")) {
                SituacaoPreMatricula situacaoEnum = converterStringParaSituacaoEnum(situacaoSelecionada);
                if (situacaoEnum != null) {
                    jpql.append(" AND pm.situacaoPreMatricula = ?").append(paramIndex);
                    parametros.add(situacaoEnum);
                    paramIndex++;
                }
            }

            // Ordenação por data de pré-matrícula mais recente primeiro
            jpql.append(" ORDER BY pm.dataPreMatricula DESC");

            System.out.println("📝 JPQL: " + jpql.toString());

            // Criar e executar a query para PreMatricula
            TypedQuery<PreMatricula> query = em.createQuery(jpql.toString(), PreMatricula.class);

            // Aplicar parâmetros
            for (int i = 0; i < parametros.size(); i++) {
                query.setParameter(i + 1, parametros.get(i));
                System.out.println("📌 Parâmetro " + (i + 1) + ": " + parametros.get(i));
            }

            List<PreMatricula> preMatriculas = query.getResultList();
            System.out.println("✅ " + preMatriculas.size() + " pré-matrícula(s) encontrada(s)");

            atualizarInterface(preMatriculas);

        } catch (Exception e) {
            String errorMsg = "💥 Erro ao buscar pré-matrículas: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            mostrarMensagemErro("Erro ao buscar pré-matrículas: " + e.getMessage());

            if (e.getCause() != null) {
                System.err.println("Causa: " + e.getCause().getMessage());
            }
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void atualizarInterface(List<PreMatricula> preMatriculas) {
        cardsContainer.getChildren().clear();

        if (preMatriculas.isEmpty()) {
            EmptyCard cardVazio = new EmptyCard("Nenhuma pré-matrícula encontrada");
            cardsContainer.getChildren().add(cardVazio);
        } else {
            for (PreMatricula preMatricula : preMatriculas) {
                // Usar PreMatriculaCard específico para pré-matrículas
                PreMatriculaCard card = new PreMatriculaCard(preMatricula);
                card.setOnEditAction(() -> editarPreMatricula(preMatricula));
                card.setOnAprovarAction(() -> aprovarPreMatricula(preMatricula));
                card.setOnReprovarAction(() -> reprovarPreMatricula(preMatricula));
                card.setOnCancelarAction(() -> cancelarPreMatricula(preMatricula));
                cardsContainer.getChildren().add(card);
            }
        }
    }

    private void mostrarMensagemErro(String mensagem) {
        cardsContainer.getChildren().clear();
        EmptyCard cardErro = new EmptyCard(mensagem);
        cardsContainer.getChildren().add(cardErro);
    }

    private void editarPreMatricula(PreMatricula preMatricula) {
        System.out.println("📝 Editando pré-matrícula: " + preMatricula.getId());
        // Implemente a lógica para abrir a tela de edição de pré-matrícula
        String nomeCrianca = preMatricula.getCrianca() != null ?
                preMatricula.getCrianca().getNome() : "Pré-matrícula " + preMatricula.getId();
        System.out.println("Editando pré-matrícula: " + nomeCrianca);

        // Exemplo de implementação:
        // MainApplication.abrirTelaEdicaoPreMatricula(preMatricula);
    }

    private void aprovarPreMatricula(PreMatricula preMatricula) {
        System.out.println("✅ Aprovando pré-matrícula: " + preMatricula.getId());
        // Implemente a lógica para aprovar a pré-matrícula
        try {
            EntityManager em = DBConnection.getEntityManager();
            em.getTransaction().begin();
            preMatricula.setSituacaoPreMatricula(SituacaoPreMatricula.APROVADA);
            em.merge(preMatricula);
            em.getTransaction().commit();
            em.close();

            System.out.println("✅ Pré-matrícula aprovada com sucesso!");
            buscarPreMatriculas(); // Recarrega a lista
        } catch (Exception e) {
            System.err.println("❌ Erro ao aprovar pré-matrícula: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void reprovarPreMatricula(PreMatricula preMatricula) {
        System.out.println("❌ Reprovando pré-matrícula: " + preMatricula.getId());
        // Implemente a lógica para reprovar a pré-matrícula
        try {
            EntityManager em = DBConnection.getEntityManager();
            em.getTransaction().begin();
            preMatricula.setSituacaoPreMatricula(SituacaoPreMatricula.REPROVADA);
            em.merge(preMatricula);
            em.getTransaction().commit();
            em.close();

            System.out.println("✅ Pré-matrícula reprovada com sucesso!");
            buscarPreMatriculas(); // Recarrega a lista
        } catch (Exception e) {
            System.err.println("❌ Erro ao reprovar pré-matrícula: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cancelarPreMatricula(PreMatricula preMatricula) {
        System.out.println("🚫 Cancelando pré-matrícula: " + preMatricula.getId());
        // Implemente a lógica para cancelar a pré-matrícula
        try {
            EntityManager em = DBConnection.getEntityManager();
            em.getTransaction().begin();
            preMatricula.setSituacaoPreMatricula(SituacaoPreMatricula.CANCELADA);
            em.merge(preMatricula);
            em.getTransaction().commit();
            em.close();

            System.out.println("✅ Pré-matrícula cancelada com sucesso!");
            buscarPreMatriculas(); // Recarrega a lista
        } catch (Exception e) {
            System.err.println("❌ Erro ao cancelar pré-matrícula: " + e.getMessage());
            e.printStackTrace();
        }
    }
}