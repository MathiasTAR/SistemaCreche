package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.components.EmptyCard;
import com.salo.sistemacreche.components.MatriculaCard;
import com.salo.sistemacreche.components.PreMatriculaCard;
import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Matricula;
import com.salo.sistemacreche.entidades.PreMatricula;
import com.salo.sistemacreche.entidades.Matricula.SituacaoMatricula;
import com.salo.sistemacreche.entidades.PreMatricula.SituacaoPreMatricula;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class HomeController {

    @FXML private Label labelPreMatriculas;
    @FXML private Label labelMatriculas;

    @FXML private VBox cardsContainerMatriculas;
    @FXML private VBox cardsContainerPreMatriculas;

    @FXML
    public void initialize() {
        carregarIndicadores();
        carregarListasRecentes();
    }

    private void carregarIndicadores() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            // PRÉ-MATRÍCULAS: Conta apenas as que estão EM_ANALISE
            Long preMatriculasEmAnalise = em.createQuery(
                            "SELECT COUNT(p) FROM PreMatricula p WHERE p.situacaoPreMatricula = :situacao", Long.class
                    ).setParameter("situacao", SituacaoPreMatricula.EM_ANALISE)
                    .getSingleResult();

            // MATRÍCULAS: Conta apenas as que estão ATIVAS (matriculadas)
            Long matriculasAtivas = em.createQuery(
                            "SELECT COUNT(m) FROM Matricula m WHERE m.situacaoMatricula = :situacao", Long.class
                    ).setParameter("situacao", SituacaoMatricula.ATIVA)
                    .getSingleResult();

            labelPreMatriculas.setText(String.valueOf(preMatriculasEmAnalise));
            labelMatriculas.setText(String.valueOf(matriculasAtivas));

        } catch (Exception e) {
            System.err.println("Erro ao carregar indicadores: " + e.getMessage());
            e.printStackTrace();

            // Valores padrão em caso de erro
            labelPreMatriculas.setText("0");
            labelMatriculas.setText("0");
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // === LISTAS RECENTES MELHORADAS ===
    private void carregarListasRecentes() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            // Últimas 5 PRÉ-MATRÍCULAS EM ANÁLISE
            List<PreMatricula> ultimasPreMatriculas = em.createQuery(
                            "SELECT p FROM PreMatricula p " +
                                    "WHERE p.situacaoPreMatricula = :situacao " +
                                    "ORDER BY p.dataPreMatricula DESC, p.id DESC", PreMatricula.class
                    ).setParameter("situacao", SituacaoPreMatricula.EM_ANALISE)
                    .setMaxResults(5)
                    .getResultList();

            // Últimas 5 MATRÍCULAS ATIVAS
            List<Matricula> ultimasMatriculas = em.createQuery(
                            "SELECT m FROM Matricula m " +
                                    "WHERE m.situacaoMatricula = :situacao " +
                                    "ORDER BY m.dataMatricula DESC, m.id DESC", Matricula.class
                    ).setParameter("situacao", SituacaoMatricula.ATIVA)
                    .setMaxResults(5)
                    .getResultList();

            atualizarCardsMatriculas(ultimasMatriculas);
            atualizarCardsPreMatriculas(ultimasPreMatriculas);

        } catch (Exception e) {
            System.err.println("Erro ao carregar listas recentes: " + e.getMessage());
            e.printStackTrace();

            // Exibir mensagens de erro nas listas
            mostrarErroListas();
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // === Atualiza as MATRÍCULAS ATIVAS ===
    private void atualizarCardsMatriculas(List<Matricula> matriculas) {
        cardsContainerMatriculas.getChildren().clear();

        if (matriculas.isEmpty()) {
            EmptyCard vazio = new EmptyCard("Nenhuma matrícula ativa recente");
            cardsContainerMatriculas.getChildren().add(vazio);
        } else {
            for (Matricula m : matriculas) {
                MatriculaCard card = new MatriculaCard(m);
                card.setOnEditAction(() -> editarMatricula(m));
                cardsContainerMatriculas.getChildren().add(card);
            }
        }
    }

    // === Atualiza as PRÉ-MATRÍCULAS EM ANÁLISE ===
    private void atualizarCardsPreMatriculas(List<PreMatricula> preMatriculas) {
        cardsContainerPreMatriculas.getChildren().clear();

        if (preMatriculas.isEmpty()) {
            EmptyCard vazio = new EmptyCard("Nenhuma pré-matrícula em análise");
            cardsContainerPreMatriculas.getChildren().add(vazio);
        } else {
            for (PreMatricula pm : preMatriculas) {
                PreMatriculaCard card = new PreMatriculaCard(pm);

                // Adicionar ações específicas para pré-matrículas em análise
                card.setOnAprovarAction(() -> aprovarPreMatricula(pm));
                card.setOnReprovarAction(() -> reprovarPreMatricula(pm));
                card.setOnCancelarAction(() -> cancelarPreMatricula(pm));

                cardsContainerPreMatriculas.getChildren().add(card);
            }
        }
    }

    // === MÉTODOS PARA AÇÕES DAS PRÉ-MATRÍCULAS ===
    private void aprovarPreMatricula(PreMatricula preMatricula) {
        atualizarSituacaoPreMatricula(preMatricula, SituacaoPreMatricula.APROVADA);
    }

    private void reprovarPreMatricula(PreMatricula preMatricula) {
        atualizarSituacaoPreMatricula(preMatricula, SituacaoPreMatricula.REPROVADA);
    }

    private void cancelarPreMatricula(PreMatricula preMatricula) {
        atualizarSituacaoPreMatricula(preMatricula, SituacaoPreMatricula.CANCELADA);
    }

    private void atualizarSituacaoPreMatricula(PreMatricula preMatricula, SituacaoPreMatricula novaSituacao) {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();
            em.getTransaction().begin();

            PreMatricula preMatriculaManaged = em.merge(preMatricula);
            preMatriculaManaged.setSituacaoPreMatricula(novaSituacao);

            em.getTransaction().commit();

            // Recarregar os dados após alteração
            carregarIndicadores();
            carregarListasRecentes();

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao atualizar pré-matrícula: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void editarMatricula(Matricula matricula) {
        System.out.println("Editar clicado na matrícula: " + matricula.getId());
    }

    // === MÉTODO PARA MOSTRAR ERRO NAS LISTAS ===
    private void mostrarErroListas() {
        cardsContainerMatriculas.getChildren().clear();
        cardsContainerPreMatriculas.getChildren().clear();

        EmptyCard erroMatriculas = new EmptyCard("Erro ao carregar matrículas");
        EmptyCard erroPreMatriculas = new EmptyCard("Erro ao carregar pré-matrículas");

        cardsContainerMatriculas.getChildren().add(erroMatriculas);
        cardsContainerPreMatriculas.getChildren().add(erroPreMatriculas);
    }
}