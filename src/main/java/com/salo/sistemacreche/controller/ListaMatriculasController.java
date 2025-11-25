package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.components.EmptyCard;
import com.salo.sistemacreche.components.MatriculaCard;
import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Matricula;
import com.salo.sistemacreche.entidades.Matricula.SituacaoMatricula;
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

public class ListaMatriculasController {

    @FXML private TextField fieldPesquisarMatricula;
    @FXML private ComboBox<String> comboSituacao;
    @FXML private VBox cardsContainer;

    @FXML
    public void initialize() {
        configurarComboBox();
        carregarMatriculas();
        configurarListeners();
    }

    private void configurarComboBox() {
        comboSituacao.getItems().addAll("Todas", "Matriculado", "Concluída", "Cancelada", "Vencida");
        comboSituacao.setValue("Todas");
    }

    private void configurarListeners() {
        // Buscar automaticamente ao pressionar Enter no campo de pesquisa
        fieldPesquisarMatricula.setOnAction(event -> buscarMatriculas());

        // Buscar automaticamente ao alterar a situação
        comboSituacao.setOnAction(event -> buscarMatriculas());
    }

    private SituacaoMatricula converterStringParaSituacaoEnum(String situacao) {
        return switch (situacao) {
            case "Concluída" -> SituacaoMatricula.CONCLUIDA;
            case "Cancelada" -> SituacaoMatricula.CANCELADA;
            case "Vencida" -> SituacaoMatricula.VENCIDA;
            case "Matriculado" -> SituacaoMatricula.ATIVA;
            default -> null;
        };
    }

    @FXML
    public void buscarMatriculas() {
        carregarMatriculasComFiltro();
    }

    @FXML
    public void limparFiltros() {
        fieldPesquisarMatricula.clear();
        comboSituacao.setValue("Todas");
        carregarMatriculas();
    }

    private void carregarMatriculas() {
        carregarMatriculasComFiltro();
    }

    private void carregarMatriculasComFiltro() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            if (em == null || !em.isOpen()) {
                System.err.println("❌ Erro de conexão com o banco");
                mostrarMensagemErro("Erro de conexão com o banco de dados");
                return;
            }

            // CORREÇÃO: Query JPQL simplificada e corrigida
            StringBuilder jpql = new StringBuilder(
                    "SELECT DISTINCT m FROM Matricula m " +
                            "LEFT JOIN FETCH m.crianca c " +
                            "LEFT JOIN FETCH c.mae mae " +
                            "LEFT JOIN FETCH c.pai pai " +
                            "WHERE m.situacaoMatricula != com.salo.sistemacreche.entidades.Matricula$SituacaoMatricula.VENCIDA"
            );

            List<Object> parametros = new ArrayList<>();
            int paramIndex = 1;

            // FILTRO 1: Pesquisa por texto (nome da criança, mãe, pai ou ID)
            String termoPesquisa = fieldPesquisarMatricula.getText();
            if (termoPesquisa != null && !termoPesquisa.trim().isEmpty()) {
                jpql.append(" AND (LOWER(c.nome) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(mae.nome) LIKE ?").append(paramIndex);
                jpql.append(" OR LOWER(pai.nome) LIKE ?").append(paramIndex);
                jpql.append(" OR CAST(m.id AS string) LIKE ?").append(paramIndex).append(")");
                parametros.add("%" + termoPesquisa.toLowerCase() + "%");
                paramIndex++;
            }

            // FILTRO 2: Situação da matrícula
            String situacaoSelecionada = comboSituacao.getValue();
            if (situacaoSelecionada != null && !situacaoSelecionada.equals("Todas")) {
                SituacaoMatricula situacaoEnum = converterStringParaSituacaoEnum(situacaoSelecionada);
                if (situacaoEnum != null) {
                    jpql.append(" AND m.situacaoMatricula = ?").append(paramIndex);
                    parametros.add(situacaoEnum);
                    paramIndex++;
                }
            }

            // Ordenação por data mais recente primeiro
            jpql.append(" ORDER BY m.dataMatricula DESC");

            System.out.println("📝 JPQL: " + jpql.toString());

            // Criar e executar a query
            TypedQuery<Matricula> query = em.createQuery(jpql.toString(), Matricula.class);

            // Aplicar parâmetros
            for (int i = 0; i < parametros.size(); i++) {
                query.setParameter(i + 1, parametros.get(i));
                System.out.println("📌 Parâmetro " + (i + 1) + ": " + parametros.get(i));
            }

            List<Matricula> matriculas = query.getResultList();
            System.out.println("✅ " + matriculas.size() + " matrícula(s) encontrada(s)");

            atualizarInterface(matriculas);

        } catch (Exception e) {
            String errorMsg = "💥 Erro ao buscar matrículas: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            mostrarMensagemErro("Erro ao buscar matrículas: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void atualizarInterface(List<Matricula> matriculas) {
        cardsContainer.getChildren().clear();

        if (matriculas.isEmpty()) {
            EmptyCard cardVazio = new EmptyCard("Nenhuma matrícula encontrada");
            cardsContainer.getChildren().add(cardVazio);
        } else {
            for (Matricula matricula : matriculas) {
                MatriculaCard card = new MatriculaCard(matricula);

                // CORREÇÃO: Configurar TODAS as ações
                card.setOnEditAction(() -> editarMatricula(matricula));
                card.setOnConcluirAction(() -> concluirMatricula(matricula));
                card.setOnCancelarAction(() -> cancelarMatricula(matricula));

                cardsContainer.getChildren().add(card);
            }
        }
    }

    // ADICIONE ESTES MÉTODOS NO CONTROLLER
    private void concluirMatricula(Matricula matricula) {
        System.out.println("🎓 Concluindo matrícula: " + matricula.getId());

        try {
            EntityManager em = DBConnection.getEntityManager();
            em.getTransaction().begin();

            // Buscar a matrícula gerenciada
            Matricula matriculaManaged = em.find(Matricula.class, matricula.getId());
            matriculaManaged.setSituacaoMatricula(Matricula.SituacaoMatricula.CONCLUIDA);
            matriculaManaged.setDataDesligamento(new java.sql.Date(System.currentTimeMillis()));

            em.merge(matriculaManaged);
            em.getTransaction().commit();
            em.close();

            System.out.println("✅ Matrícula concluída com sucesso!");
            buscarMatriculas(); // Recarrega a lista

        } catch (Exception e) {
            System.err.println("❌ Erro ao concluir matrícula: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cancelarMatricula(Matricula matricula) {
        System.out.println("❌ Cancelando matrícula: " + matricula.getId());

        try {
            EntityManager em = DBConnection.getEntityManager();
            em.getTransaction().begin();

            // Buscar a matrícula gerenciada
            Matricula matriculaManaged = em.find(Matricula.class, matricula.getId());
            matriculaManaged.setSituacaoMatricula(Matricula.SituacaoMatricula.CANCELADA);
            matriculaManaged.setDataDesligamento(new java.sql.Date(System.currentTimeMillis()));

            em.merge(matriculaManaged);
            em.getTransaction().commit();
            em.close();

            System.out.println("✅ Matrícula cancelada com sucesso!");
            buscarMatriculas(); // Recarrega a lista

        } catch (Exception e) {
            System.err.println("❌ Erro ao cancelar matrícula: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarMensagemErro(String mensagem) {
        cardsContainer.getChildren().clear();
        EmptyCard cardErro = new EmptyCard(mensagem);
        cardsContainer.getChildren().add(cardErro);
    }

    private void editarMatricula(Matricula matricula) {
        System.out.println("📝 Editando matrícula: " + matricula.getId());
        // Aqui você pode implementar a lógica para abrir a tela de edição
        // Por exemplo:
        // MainApplication.abrirTelaEdicaoMatricula(matricula);

        // Mensagem temporária
        String nomeCrianca = matricula.getCrianca() != null ?
                matricula.getCrianca().getNome() : "Matrícula " + matricula.getId();
        System.out.println("Editando matrícula: " + nomeCrianca);
    }
}