package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.components.EmptyCard;
import com.salo.sistemacreche.components.MatriculaCard;
import com.salo.sistemacreche.components.PreMatriculaCard;
import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Matricula;
import com.salo.sistemacreche.entidades.PreMatricula;
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

    // === 📊 INDICADORES ===
    private void carregarIndicadores() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            Long totalPreMatriculas = em.createQuery(
                    "SELECT COUNT(p) FROM PreMatricula p", Long.class
            ).getSingleResult();

            Long totalMatriculas = em.createQuery(
                    "SELECT COUNT(m) FROM Matricula m", Long.class
            ).getSingleResult();

            labelPreMatriculas.setText(String.valueOf(totalPreMatriculas));
            labelMatriculas.setText(String.valueOf(totalMatriculas));

        } catch (Exception e) {
            System.err.println("Erro ao carregar indicadores: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // === LISTAS RECENTES ===
    private void carregarListasRecentes() {
        EntityManager em = null;
        try {
            em = DBConnection.getEntityManager();

            // Últimas 5 pré-matrículas
            List<PreMatricula> ultimasPreMatriculas = em.createQuery(
                    "SELECT p FROM PreMatricula p ORDER BY p.id DESC", PreMatricula.class
            ).setMaxResults(5).getResultList();

            // Últimas 5 matrículas
            List<Matricula> ultimasMatriculas = em.createQuery(
                    "SELECT m FROM Matricula m ORDER BY m.id DESC", Matricula.class
            ).setMaxResults(5).getResultList();

            atualizarCardsMatriculas(ultimasMatriculas);
            atualizarCardsPreMatriculas(ultimasPreMatriculas);

        } catch (Exception e) {
            System.err.println("Erro ao carregar listas recentes: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // === Atualiza a matrículas ===
    private void atualizarCardsMatriculas(List<Matricula> matriculas) {
        cardsContainerMatriculas.getChildren().clear();

        if (matriculas.isEmpty()) {
            EmptyCard vazio = new EmptyCard("Nenhuma matrícula recente");
            cardsContainerMatriculas.getChildren().add(vazio);
        } else {
            for (Matricula m : matriculas) {
                MatriculaCard card = new MatriculaCard(m);
                card.setOnEditAction(() -> editarMatricula(m));
                cardsContainerMatriculas.getChildren().add(card);
            }
        }
    }

    // === Atualiza a pré-matrículas ===
    private void atualizarCardsPreMatriculas(List<PreMatricula> preMatriculas) {
        cardsContainerPreMatriculas.getChildren().clear();

        if (preMatriculas.isEmpty()) {
            EmptyCard vazio = new EmptyCard("Nenhuma pré-matrícula recente");
            cardsContainerPreMatriculas.getChildren().add(vazio);
        } else {
            for (PreMatricula pm : preMatriculas) {
                PreMatriculaCard card = new PreMatriculaCard(pm);
                cardsContainerPreMatriculas.getChildren().add(card);
            }
        }
    }

    private void editarMatricula(Matricula matricula) {
        System.out.println("📝 Editar clicado na matrícula: " + matricula.getId());
        // Aqui você pode abrir a tela de edição
    }
}
