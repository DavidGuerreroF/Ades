import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Menudia extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Título de la ventana
        primaryStage.setTitle("Menú del Restaurante");

        // Cargar contenido del archivo 'menu.txt'
        String menuContent = loadMenuFromFile("C:\\PROYECTO\\menu.txt");

        // Crear un área de texto para mostrar el contenido del menú
        TextArea menuTextArea = new TextArea(menuContent);
        menuTextArea.setEditable(false); // No se puede editar el texto inicialmente
        menuTextArea.setWrapText(true); // Ajusta el texto dentro del área
        menuTextArea.setFont(Font.font("Arial", 18)); // Fuente más grande para una mejor visibilidad
        menuTextArea.setStyle("-fx-background-color: #f9f9f9; -fx-text-fill: #2e2e2e; -fx-font-weight: bold; -fx-font-size: 16px;"); // Estilo moderno

        // Personalizar la barra de desplazamiento
        ScrollPane scrollPane = new ScrollPane(menuTextArea);
        scrollPane.setFitToWidth(true); // Ajusta el contenido al ancho de la ventana
        scrollPane.setFitToHeight(true); // Ajusta el contenido al alto de la ventana
        scrollPane.setStyle("-fx-background-color: transparent;"); // Fondo transparente para ScrollPane
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Desactivar barra horizontal si no es necesaria

        // Crear los botones
        Button printButton = new Button("Imprimir Menú");
        Button despachosButton = new Button("Volver a Despachos");
        Button modifyButton = new Button("Modificar Menú");

        // Estilo de los botones
        printButton.setStyle("-fx-font-size: 16px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px 20px;");
        despachosButton.setStyle("-fx-font-size: 16px; -fx-background-color: #008CBA; -fx-text-fill: white; -fx-padding: 10px 20px;");
        modifyButton.setStyle("-fx-font-size: 16px; -fx-background-color: #FFA500; -fx-text-fill: white; -fx-padding: 10px 20px;");

        // Acción del botón de impresión
        printButton.setOnAction(event -> {
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null && printerJob.showPrintDialog(primaryStage)) {
                boolean success = printerJob.printPage(menuTextArea);
                if (success) {
                    printerJob.endJob();
                }
            }
        });

        // Acción del botón de Despachos
        despachosButton.setOnAction(e -> {
            despachos despachosWindow = new despachos();
            despachosWindow.start(new Stage());
            primaryStage.close();
        });

        // Acción del botón de Modificar
        modifyButton.setOnAction(event -> {
            if (menuTextArea.isEditable()) {
                // Guardar cambios en el archivo
                saveMenuToFile("C:\\PROYECTO\\menu.txt", menuTextArea.getText());
                menuTextArea.setEditable(false);
                modifyButton.setText("Modificar Menú");
            } else {
                menuTextArea.setEditable(true);
                modifyButton.setText("Guardar Menú");
            }
        });

        // Crear un HBox para organizar los botones horizontalmente
        HBox buttonBox = new HBox(20); // Espacio entre los botones
        buttonBox.setAlignment(Pos.CENTER); // Alinear los botones al centro
        buttonBox.getChildren().addAll(printButton, despachosButton, modifyButton);

        // Crear un VBox para organizar el ScrollPane y el HBox de botones
        VBox vbox = new VBox(20); // Espacio entre los elementos
        vbox.setPadding(new Insets(20)); // Añadir margen alrededor
        vbox.getChildren().addAll(scrollPane, buttonBox);

        // Crear la escena con dimensiones grandes para que se vea el contenido completo
        Scene scene = new Scene(vbox, 800, 400); // Aumentamos el tamaño de la ventana
        scene.setFill(Color.WHITE); // Fondo blanco para la escena
        primaryStage.setScene(scene);

        // Mostrar la ventana
        primaryStage.show();
    }

    // Método para cargar el contenido del archivo 'menu.txt'
    private String loadMenuFromFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            content.append("No se pudo cargar el menú. Error: ").append(e.getMessage());
        }
        return content.toString();
    }

    // Método para guardar el contenido en el archivo 'menu.txt'
    private void saveMenuToFile(String filePath, String content) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el menú. Error: " + e.getMessage());
        }
    }

    // Método para abrir la ventana de la clase Despachos
    private void openDespachosWindow() {
        Stage despachosStage = new Stage();
        despachosStage.setTitle("Ventana de Despachos");

        // Aquí creamos la interfaz de la clase Despachos
        // Puedes cambiar este contenido por el de tu clase Despachos.java
        VBox despachosVBox = new VBox();
        despachosVBox.setPadding(new Insets(20));
        despachosVBox.getChildren().add(new Button("Este es el botón de la ventana Despachos"));

        Scene despachosScene = new Scene(despachosVBox, 400, 300);
        despachosStage.setScene(despachosScene);
        despachosStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Llamar al lanzamiento de la aplicación
    }
}