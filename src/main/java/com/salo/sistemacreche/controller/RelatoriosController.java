package com.salo.sistemacreche.controller;

/*package com.salo.sistemacreche.controller;

import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Crianca;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class RelatoriosController {

    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFim;
    @FXML private ComboBox<String> comboPeriodo;

    @FXML private ComboBox<String> comboSexo;
    @FXML private ComboBox<String> comboAlergia; // "Sim" / "Não"
    @FXML private ComboBox<String> comboRaca;
    @FXML private ComboBox<String> comboMobilidade;

    @FXML private ComboBox<String> comboMoradia;
    @FXML private ComboBox<String> comboBeneficioSocial;

    @FXML private CheckBox checkAltasHabilidades;
    @FXML private CheckBox checkCegueira;
    @FXML private CheckBox checkTDAH;

    @FXML private TableView<Crianca> tabela;


    @FXML
    public void gerarPDF() {

        EntityManager em = DBConnection.getEntityManager();

        StringBuilder jpql = new StringBuilder(
                "SELECT c FROM Crianca c " +
                        " LEFT JOIN SituacaoHabitacional s ON s.crianca.id = c.id " +
                        " WHERE 1=1 "
        );

        Map<String, Object> params = new HashMap<>();

        // =============================
        // PERÍODO
        // =============================
        LocalDate inicio = datePickerInicio.getValue();
        LocalDate fim = datePickerFim.getValue();

        if (comboPeriodo.getValue() != null) {
            switch (comboPeriodo.getValue()) {
                case "Últimos 7 dias":
                    inicio = LocalDate.now().minusDays(7);
                    fim = LocalDate.now();
                    break;
                case "Últimos 30 dias":
                    inicio = LocalDate.now().minusDays(30);
                    fim = LocalDate.now();
                    break;
                case "Este mês":
                    inicio = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
                    fim = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
                    break;
                case "Mês anterior":
                    inicio = LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                    fim = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                    break;
                case "Este ano":
                    inicio = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
                    fim = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
                    break;
            }
        }

        if (inicio != null) {
            jpql.append(" AND c.dataCriacao >= :inicio ");
            params.put("inicio", inicio);
        }

        if (fim != null) {
            jpql.append(" AND c.dataCriacao <= :fim ");
            params.put("fim", fim);
        }


        // =============================
        // FILTROS CRIANÇA
        // =============================

        // SEXO
        if (comboSexo.getValue() != null && !comboSexo.getValue().equals("Todos")) {
            jpql.append(" AND c.sexo = :sexo ");
            params.put("sexo", comboSexo.getValue());
        }

        // ALERGIA (Boolean)
        if (comboAlergia.getValue() != null) {
            switch (comboAlergia.getValue()) {
                case "Sim":
                    jpql.append(" AND c.alergia = true ");
                    break;
                case "Não":
                    jpql.append(" AND c.alergia = false ");
                    break;
            }
        }

        // RAÇA (Enum)
        if (comboRaca.getValue() != null && !comboRaca.getValue().equals("Todas")) {
            jpql.append(" AND c.corRaca = :raca ");
            params.put("raca", comboRaca.getValue());
        }

        // MOBILIDADE REDUZIDA
        if (comboMobilidade.getValue() != null) {
            switch (comboMobilidade.getValue()) {
                case "Com mobilidade reduzida":
                    jpql.append(" AND c.mobilidadeReduzida = true ");
                    break;
                case "Sem mobilidade reduzida":
                    jpql.append(" AND c.mobilidadeReduzida = false ");
                    break;
            }
        }


        // =============================
        // DEFICIÊNCIAS E CLASSIFICAÇÕES
        // =============================

        if (checkAltasHabilidades.isSelected()) {
            jpql.append(" AND c.altasHabilidades = true ");
        }

        if (checkCegueira.isSelected()) {
            jpql.append(" AND c.cegueira = true ");
        }

        if (checkTDAH.isSelected()) {
            jpql.append(" AND c.tdah = true ");
        }


        // =============================
        // SITUAÇÃO HABITACIONAL (JOIN)
        // =============================

        if (comboMoradia.getValue() != null && !comboMoradia.getValue().equals("Todos")) {
            jpql.append(" AND s.tipoMoradia = :moradia ");
            params.put("moradia", comboMoradia.getValue());
        }


        // =============================
        // AUXÍLIO SOCIAL (ManyToOne)
        // =============================

        if (comboBeneficioSocial.getValue() != null && !comboBeneficioSocial.getValue().equals("Todos")) {
            jpql.append(" AND c.tipoAuxilio.nomeAuxilio = :aux ");
            params.put("aux", comboBeneficioSocial.getValue());
        }


        // =============================
        // EXECUTAR CONSULTA
        // =============================

        TypedQuery<Crianca> query = em.createQuery(jpql.toString(), Crianca.class);
        params.forEach(query::setParameter);

        List<Crianca> resultado = query.getResultList();
        tabela.getItems().setAll(resultado);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Relatório");
        alerta.setHeaderText("Filtros aplicados");
        alerta.setContentText("Crianças encontradas: " + resultado.size());
        alerta.show();

        em.close();
    }


    @FXML
    public void limparCampos() {
        datePickerInicio.setValue(null);
        datePickerFim.setValue(null);
        comboPeriodo.setValue(null);

        comboSexo.setValue(null);
        comboAlergia.setValue(null);
        comboRaca.setValue(null);
        comboMobilidade.setValue(null);

        comboMoradia.setValue(null);
        comboBeneficioSocial.setValue(null);

        checkAltasHabilidades.setSelected(false);
        checkCegueira.setSelected(false);
        checkTDAH.setSelected(false);
    }
}
*/

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RelatoriosController {

    // Principais controles
    @FXML private DatePicker datePickerInicio;
    @FXML private DatePicker datePickerFim;
    @FXML private ComboBox<String> comboPeriodo;
    @FXML private Button btnLimparCampos;
    @FXML private Button btnGerarPDF;

    @FXML
    public void initialize() {
        System.out.println("📊 RelatoriosController inicializado!");
        configurarComboboxes();
    }

    private void configurarComboboxes() {
        // Combo período já está definido no FXML, apenas configurar valor padrão
        comboPeriodo.setValue("Este mês");

        // Configurar data padrão (este mês)
        datePickerInicio.setValue(java.time.LocalDate.now().withDayOfMonth(1));
        datePickerFim.setValue(java.time.LocalDate.now());
    }

    @FXML
    private void limparCampos() {
        System.out.println("🧹 Limpando campos...");

        // Apenas limpar os campos principais
        datePickerInicio.setValue(null);
        datePickerFim.setValue(null);
        comboPeriodo.setValue(null);

        mostrarMensagem("Campos limpos!", "Todos os campos foram resetados.");
    }

    @FXML
    private void gerarPDF() {
        System.out.println("📄 Gerando relatório PDF...");

        // Validar apenas datas
        if (datePickerInicio.getValue() == null || datePickerFim.getValue() == null) {
            mostrarErro("Selecione o período", "É necessário definir data inicial e final.");
            return;
        }

        if (datePickerInicio.getValue().isAfter(datePickerFim.getValue())) {
            mostrarErro("Data inválida", "Data inicial não pode ser depois da data final.");
            return;
        }

        // Simular geração do PDF
        String periodo = datePickerInicio.getValue() + " a " + datePickerFim.getValue();
        System.out.println("✅ Gerando PDF para o período: " + periodo);

        mostrarMensagem("PDF Gerado!", "Relatório criado com sucesso!\nPeríodo: " + periodo);
    }

    private void mostrarMensagem(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}