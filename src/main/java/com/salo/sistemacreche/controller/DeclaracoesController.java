package com.salo.sistemacreche.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DeclaracoesController {

    @FXML
    public static void abrirPDF() {
        try {
            // Carregar o PDF dos resources
            InputStream inputStream = DeclaracoesController.class.getResourceAsStream("/reports/declaração.pdf");

            if (inputStream == null) {
                mostrarErro("Arquivo PDF não encontrado!\nColoque o arquivo 'declaracao_matricula.pdf' na pasta resources/pdf/");
                return;
            }

            // Criar arquivo temporário
            File tempFile = File.createTempFile("declaracao_", ".pdf");
            tempFile.deleteOnExit();

            // Copiar o PDF para o arquivo temporário
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();

            // Abrir com o programa padrão do sistema
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (tempFile.exists()) {
                    desktop.open(tempFile);
                    System.out.println("✅ PDF aberto com sucesso!");
                }


            }

        } catch (IOException e) {
            mostrarErro("Erro ao abrir PDF: " + e.getMessage());
        }
    }

    private static void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}