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
            switch(situacao) {
                case "Concluída": return SituacaoMatricula.CONCLUIDA;
                case "Cancelada": return SituacaoMatricula.CANCELADA;
                case "Vencida": return  SituacaoMatricula.VENCIDA;
                case "Matriculado": return SituacaoMatricula.ATIVA;
                default: return null;
            }
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

                System.out.println("🔍 Executando busca com filtros...");
                System.out.println("📊 Situação selecionada: " + comboSituacao.getValue());

                // Usando JPQL para maior simplicidade e controle
                StringBuilder jpql = new StringBuilder(
                        "SELECT DISTINCT m FROM Matricula m " +
                                "LEFT JOIN FETCH m.crianca c " +
                                "WHERE m.situacaoMatricula != com.salo.sistemacreche.entidades.Matricula$SituacaoMatricula.VENCIDA"
                );

                List<Object> parametros = new ArrayList<>();
                int paramIndex = 1;

                // FILTRO 1: Pesquisa por texto (nome da criança)
                String termoPesquisa = fieldPesquisarMatricula.getText();
                if (termoPesquisa != null && !termoPesquisa.trim().isEmpty()) {
                    jpql.append(" AND (LOWER(c.nome) LIKE ?").append(paramIndex);
                    jpql.append(" OR LOWER(c.nomeMae) LIKE ?").append(paramIndex);
                    jpql.append(" OR LOWER(c.nomePai) LIKE ?").append(paramIndex);
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

                if (e.getCause() != null) {
                    System.err.println("Causa: " + e.getCause().getMessage());
                }
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
                    card.setOnEditAction(() -> editarMatricula(matricula));
                    cardsContainer.getChildren().add(card);
                }
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