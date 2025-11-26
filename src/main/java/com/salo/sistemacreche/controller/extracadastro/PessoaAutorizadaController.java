package com.salo.sistemacreche.controller.extracadastro;

import com.salo.sistemacreche.dao.DBConnection;
import com.salo.sistemacreche.entidades.Pessoa;
import com.salo.sistemacreche.entidades.PessoaAutorizada;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class PessoaAutorizadaController {

    @FXML private TextField fieldNome;
    @FXML private ComboBox<String> comboParentesco;
    @FXML private TextField fieldRg;
    @FXML private TextField fieldTelefone;

    private Stage dialogStage;
    private boolean salvo = false;
    private PessoaAutorizada pessoaAutorizadaSalva;

    @FXML
    public void initialize() {
        aplicarMascaraTelefone();
        aplicarMascaraRG();
        aplicarMascaraNome();

        carregarParentescos();
    }

    // Carregar parentescos no ComboBox
    private void carregarParentescos() {
        comboParentesco.getItems().addAll(
                "MAE",
                "PAI",
                "IRMAO",
                "IRMA",
                "AVO",
                "TIO",
                "TIA",
                "RESPONSAVEL_LEGAL",
                "PRIMO",
                "PRIMA",
                "NENHUM"
        );
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSalvo() {
        return salvo;
    }

    public PessoaAutorizada getPessoaAutorizadaSalva() {
        return pessoaAutorizadaSalva;
    }

    @FXML
    private void salvarPessoaAutorizada() {
        if (validarCampos()) {
            EntityManager em = null;
            EntityTransaction transaction = null;

            try {
                em = DBConnection.getEntityManager();
                transaction = em.getTransaction();
                transaction.begin();

                // 1. Criar e salvar a Pessoa
                Pessoa pessoa = new Pessoa();
                pessoa.setNome(getNomeFormatadoParaBanco());
                pessoa.setRg(getRgFormatadoParaBanco());
                pessoa.setTelefone(getTelefoneFormatadoParaBanco());
                pessoa.setCpf(gerarCpfTemporario()); // CPF temporário único
                pessoa.setOutroContato(""); // Outro contato vazio

                em.persist(pessoa);

                // 2. Criar Pessoa Autorizada
                PessoaAutorizada pessoaAutorizada = new PessoaAutorizada();
                pessoaAutorizada.setPessoa(pessoa);

                // 3. Usa o Valor direto da comboBox
                PessoaAutorizada.Parentesco parentescoEnum = converterStringParaParentesco(comboParentesco.getValue());
                pessoaAutorizada.setParentesco(parentescoEnum);
                pessoaAutorizada.setTelefone(getTelefoneFormatadoParaBanco());

                em.persist(pessoaAutorizada);
                this.pessoaAutorizadaSalva = pessoaAutorizada;

                transaction.commit();

                this.salvo = true;
                fecharDialog();

            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                System.err.println(" Erro ao salvar pessoa autorizada: " + e.getMessage());
                e.printStackTrace();
                mostrarErro("Erro ao salvar pessoa autorizada: " + e.getMessage());
            } finally {
                if (em != null && em.isOpen()) {
                    em.close();
                }
            }
        }
    }

    // Gera CPF temporário único baseado no timestamp
    private String gerarCpfTemporario() {
        long timestamp = System.currentTimeMillis() % 1000000000L;
        return String.format("%011d", timestamp);
    }

    @FXML
    private void cancelarPessoaAutorizada() {
        this.salvo = false;
        this.pessoaAutorizadaSalva = null;
        fecharDialog();
    }

    @FXML
    private void fecharDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private boolean validarCampos() {
        // === NOME ===
        if (fieldNome.getText().trim().isEmpty()) {
            mostrarErro("Nome é obrigatório!");
            fieldNome.requestFocus();
            return false;
        }
        if (!validarNome(fieldNome.getText())) {
            mostrarErro("Informe nome e sobrenome completos!\nEx: João Silva Oliveira");
            fieldNome.requestFocus();
            return false;
        }

        // === PARENTESCO (AGORA COMBOBOX) ===
        if (comboParentesco.getValue() == null) {
            mostrarErro("Parentesco é obrigatório!");
            comboParentesco.requestFocus();
            return false;
        }

        // === RG ===
        if (fieldRg.getText().trim().isEmpty()) {
            mostrarErro("RG é obrigatório!");
            fieldRg.requestFocus();
            return false;
        }
        if (!validarRG(fieldRg.getText())) {
            mostrarErro("RG inválido!\nDeve ter entre 8 e 12 dígitos.");
            fieldRg.requestFocus();
            return false;
        }

        // === TELEFONE ===
        if (fieldTelefone.getText().trim().isEmpty()) {
            mostrarErro("Telefone é obrigatório!");
            fieldTelefone.requestFocus();
            return false;
        }
        if (!validarTelefone(fieldTelefone.getText())) {
            mostrarErro("Telefone inválido!\nFormato: (11) 99999-9999");
            fieldTelefone.requestFocus();
            return false;
        }

        return true;
    }

    // === VALIDAÇÃO DE RG ===
    private boolean validarRG(String rg) {
        if (rg == null || rg.trim().isEmpty()) return false;

        // Remove caracteres especiais, mantém números e X
        String rgLimpo = rg.replaceAll("[^0-9Xx]", "").toUpperCase();

        // RG geralmente tem entre 8 e 12 caracteres
        return rgLimpo.length() >= 8 && rgLimpo.length() <= 12;
    }

    // === FORMATAÇÃO DE RG ===
    private String formatarRG(String rg) {
        String rgLimpo = rg.replaceAll("[^0-9Xx]", "").toUpperCase();
        return rgLimpo;
    }

    public String getRgFormatadoParaBanco() {
        return formatarRG(fieldRg.getText());
    }

    // === VALIDAÇÃO DE NOME ===
    private boolean validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        // Remove espaços extras e verifica se tem pelo menos 2 palavras
        String nomeLimpo = nome.trim().replaceAll("\\s+", " ");
        String[] partes = nomeLimpo.split(" ");

        // Deve ter pelo menos nome e sobrenome
        if (partes.length < 2) {
            return false;
        }

        // Cada parte deve ter pelo menos 2 caracteres
        for (String parte : partes) {
            if (parte.length() < 2) {
                return false;
            }
        }

        // Verifica se tem apenas letras, espaços e acentos
        return nomeLimpo.matches("[a-zA-ZÀ-ÿ\\s]+");
    }

    // === FORMATAÇÃO DE NOME ===
    private String formatarNome(String nome) {
        if (nome == null) return "";

        // Remove espaços extras e formata cada palavra
        String nomeLimpo = nome.trim().replaceAll("\\s+", " ");
        String[] palavras = nomeLimpo.split(" ");
        StringBuilder nomeFormatado = new StringBuilder();

        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                // Capitaliza: maria → Maria
                String capitalizada = palavra.substring(0, 1).toUpperCase() +
                        palavra.substring(1).toLowerCase();
                nomeFormatado.append(capitalizada).append(" ");
            }
        }

        return nomeFormatado.toString().trim();
    }

    public String getNomeFormatadoParaBanco() {
        return formatarNome(fieldNome.getText());
    }

    public String getParentescoFormatadoParaBanco() {
        return comboParentesco.getValue();
    }

    // === MÉTODO DE FORMATAÇÃO DE TELEFONE ===
    private String formatarTelefone(String telefone) {
        // Remove TUDO que não é número (parênteses, traços, espaços, etc)
        String numeros = telefone.replaceAll("[^0-9]", "");

        // Verifica quantos dígitos tem e formata adequadamente
        if (numeros.length() == 10) {
            // Formato para telefone fixo: (11) 9999-9999
            return "(" + numeros.substring(0, 2) + ") " +
                    numeros.substring(2, 6) + "-" +
                    numeros.substring(6);
        } else if (numeros.length() == 11) {
            // Formato para celular: (11) 99999-9999
            return "(" + numeros.substring(0, 2) + ") " +
                    numeros.substring(2, 7) + "-" +
                    numeros.substring(7);
        }

        // Se não tiver 10 ou 11 dígitos, retorna os números limpos
        return numeros;
    }

    // === MÉTODO DE VALIDAÇÃO DE TELEFONE ===
    private boolean validarTelefone(String telefone) {
        // Remove tudo que não é número e conta os dígitos
        String numeros = telefone.replaceAll("[^0-9]", "");

        // Telefone válido deve ter 10 ou 11 dígitos (com DDD)
        return numeros.length() == 10 || numeros.length() == 11;
    }

    // === MÉTODOS PARA PEGAR DADOS FORMATADOS PARA O BANCO ===
    public String getTelefoneFormatadoParaBanco() {
        return formatarTelefone(fieldTelefone.getText());
    }

    // === MÉTODO PARA APLICAR MÁSCARA EM TEMPO REAL ===
    private void aplicarMascaraTelefone() {
        // Ouve as mudanças no campo telefone
        fieldTelefone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.equals(oldValue)) {
                String formatado = formatarTelefone(newValue);
                if (!formatado.equals(newValue)) {
                    fieldTelefone.setText(formatado);
                    // Coloca o cursor no final do texto
                    fieldTelefone.positionCaret(formatado.length());
                }
            }
        });
    }

    private void aplicarMascaraRG() {
        fieldRg.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.equals(oldValue)) {
                String formatado = formatarRG(newValue);
                if (!formatado.equals(newValue)) {
                    fieldRg.setText(formatado);
                    fieldRg.positionCaret(formatado.length());
                }
            }
        });
    }

    private void aplicarMascaraNome() {
        fieldNome.textProperty().addListener((observable, oldValue, newValue) -> {
            // Apenas permite letras e espaços
            if (!newValue.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                fieldNome.setText(oldValue);
            }
        });
    }


    // === MÉTODO PARA CONVERTER STRING PARA ENUM PARENTESCO ===
    private PessoaAutorizada.Parentesco converterStringParaParentesco(String parentesco) {
        if (parentesco == null) return PessoaAutorizada.Parentesco.NENHUM;

        try {
            return PessoaAutorizada.Parentesco.valueOf(parentesco);
        } catch (IllegalArgumentException e) {
            System.err.println("Valor de parentesco não encontrado: " + parentesco);
            return PessoaAutorizada.Parentesco.NENHUM;
        }
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de Validação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarMensagemSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // Getters para os dados
    public String getNome() { return fieldNome.getText(); }
    public String getParentesco() { return comboParentesco.getValue(); }
    public String getRg() { return fieldRg.getText(); }
    public String getTelefone() { return fieldTelefone.getText(); }

    // Método para obter todos os dados como objeto
    public DadosPessoaAutorizada getDadosPessoa() {
        return new DadosPessoaAutorizada(
                getNomeFormatadoParaBanco(),
                comboParentesco.getValue(),
                getRgFormatadoParaBanco(),
                getTelefoneFormatadoParaBanco()
        );
    }

    // Método para limpar os campos
    public void limparCampos() {
        fieldNome.clear();
        comboParentesco.setValue(null);
        fieldRg.clear();
        fieldTelefone.clear();
        this.salvo = false;
        this.pessoaAutorizadaSalva = null;
    }

    // Método para preencher os campos se for edição
    public void setDadosPessoa(DadosPessoaAutorizada dados) {
        fieldNome.setText(dados.getNome());
        comboParentesco.setValue(dados.getParentesco());
        fieldRg.setText(dados.getRg());
        fieldTelefone.setText(dados.getTelefone());
    }

    // Classe interna para transportar dados
    public static class DadosPessoaAutorizada {
        private String nome;
        private String parentesco;
        private String rg;
        private String telefone;

        public DadosPessoaAutorizada(String nome, String parentesco, String rg, String telefone) {
            this.nome = nome;
            this.parentesco = parentesco;
            this.rg = rg;
            this.telefone = telefone;
        }

        // Getters
        public String getNome() { return nome; }
        public String getParentesco() { return parentesco; }
        public String getRg() { return rg; }
        public String getTelefone() { return telefone; }

        @Override
        public String toString() {
            return "DadosPessoaAutorizada{" +
                    "nome='" + nome + '\'' +
                    ", parentesco='" + parentesco + '\'' +
                    ", rg='" + rg + '\'' +
                    ", telefone='" + telefone + '\'' +
                    '}';
        }
    }
}