import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.SQLException;

public class Main extends Application {

    private Label errorMessage; // Definir el Label del mensaje de error como atributo

    @Override
    public void start(Stage primaryStage) {
        // Crear los controles de la interfaz
        Label welcomeMessage = new Label("Bienvenido, usuario");
        welcomeMessage.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Campo de Usuario (Username)
        Label usernameLabel = new Label("Usuario:");
        usernameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        TextField usernameField = new TextField();
        usernameField.setStyle("-fx-font-size: 14px; -fx-prompt-text-fill: #888888;");
        usernameField.setPromptText("Ingrese su usuario");

        // Campo de Clave (Password)
        Label passwordLabel = new Label("Clave:");
        passwordLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-font-size: 14px; -fx-prompt-text-fill: #888888;");
        passwordField.setPromptText("Ingrese su clave");

        // Botón Iniciar Sesión
        Button loginButton = createStyledButton("Iniciar Sesión");
        // Botón Gestionar Clave
        Button managePasswordButton = createStyledButton("Registrar Usuario");

        // Botón de Ayuda
        Button helpButton = createStyledSmallButton("?");
        helpButton.setOnAction(e -> showHelpMessage());

        errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Hipervínculo ¿Olvidaste tu usuario o contraseña?
        Hyperlink forgotLink = new Hyperlink("¿Olvidaste tu usuario o contraseña?");
        forgotLink.setStyle("-fx-font-size: 12px; -fx-text-fill: #2469e0;");
        forgotLink.setOnAction(e -> showForgotMessage());

        // Manejar el evento de clic en el botón de login
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(), primaryStage));

        // Manejar el evento de tecla (Enter) en el campo de la contraseña
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin(usernameField.getText(), passwordField.getText(), primaryStage);
            }
        });

        // Manejar el evento de clic en el botón "Gestionar Clave"
        managePasswordButton.setOnAction(e -> handleSuperUserAuthentication());

        // Logo de la empresa
        Image logo = new Image("file:///C:/PROYECTO/images/logo.png");
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(200);
        logoView.setFitHeight(200);

        // Crear el diseño de la interfaz
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        // Agregar los controles al diseño
        gridPane.add(logoView, 0, 0, 2, 1);
        gridPane.add(helpButton, 1, 1); // Botón de ayuda en la parte superior derecha
        gridPane.add(welcomeMessage, 0, 1, 2, 1);
        gridPane.add(usernameLabel, 0, 2);
        gridPane.add(usernameField, 1, 2);
        gridPane.add(passwordLabel, 0, 3);
        gridPane.add(passwordField, 1, 3);
        gridPane.add(loginButton, 0, 4, 2, 1);
        gridPane.add(managePasswordButton, 0, 5, 2, 1);
        gridPane.add(forgotLink, 0, 6, 2, 1); // <-- Nuevo hipervínculo
        gridPane.add(errorMessage, 0, 7, 2, 1);

        // Centrar los elementos en la columna
        GridPane.setHalignment(logoView, HPos.CENTER);
        GridPane.setHalignment(helpButton, HPos.RIGHT);
        GridPane.setHalignment(welcomeMessage, HPos.CENTER);
        GridPane.setHalignment(usernameLabel, HPos.LEFT);
        GridPane.setHalignment(usernameField, HPos.CENTER);
        GridPane.setHalignment(passwordLabel, HPos.LEFT);
        GridPane.setHalignment(passwordField, HPos.CENTER);
        GridPane.setHalignment(loginButton, HPos.CENTER);
        GridPane.setHalignment(managePasswordButton, HPos.CENTER);
        GridPane.setHalignment(forgotLink, HPos.CENTER);
        GridPane.setHalignment(errorMessage, HPos.CENTER);

        // Crear la escena y el escenario
        Scene scene = new Scene(gridPane, 350, 570);
        primaryStage.setTitle("Iniciar Sesion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para crear botones con un estilo moderno
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-pref-width: 180px; " +
                        "-fx-background-color: #2469e0; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-border-radius: 20px; " +
                        "-fx-background-radius: 20px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0.5, 0, 2);"
        );
        return button;
    }

    // Método para crear botones pequeños con estilo
    private Button createStyledSmallButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-font-size: 12px; " +
                        "-fx-pref-width: 30px; " +
                        "-fx-pref-height: 30px; " +
                        "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-border-radius: 15px; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-padding: 5px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 5, 0.5, 0, 1);"
        );
        return button;
    }

    private void handleLogin(String username, String password, Stage primaryStage) {
        // Validar credenciales
        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Debe completar todos los campos.");
            return;
        }

        try {
            // Lógica de validación con la base de datos
            boolean isAuthenticated = usuarioDAO.validarCredenciales(username, password);
            if (isAuthenticated) {
                primaryStage.close();
                showDespachosApp(); // Cambiar esta línea para abrir la clase Despachos
            } else {
                showErrorMessage("Usuario o clave incorrectos.");
            }
        } catch (SQLException ex) {
            showErrorMessage("Error al conectarse a la base de datos: " + ex.getMessage());
        }
    }

    private void showDespachosApp() {
        // Mostrar ventana de despachos
        despachos despachosApp = new despachos(); // Asegúrate de que la clase Despachos esté implementada
        despachosApp.start(new Stage()); // Abre la ventana de despachos
    }

    private void handleSuperUserAuthentication() {
        // Ventana para solicitar la clave de superusuario
        Stage superUserStage = new Stage();

        Label promptLabel = new Label("Ingrese clave Administrador:");
        PasswordField passwordField = new PasswordField();
        Label feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-text-fill: red;");

        Button submitButton = new Button("Ingresar");
        submitButton.setOnAction(e -> {
            if ("123456".equals(passwordField.getText())) {
                superUserStage.close();
                handleManagePassword(); // Abrir la ventana de gestionar clave
            } else {
                feedbackLabel.setText("Debe ingresar clave Administrador");
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        gridPane.add(promptLabel, 0, 0, 2, 1);
        gridPane.add(passwordField, 0, 1, 2, 1);
        gridPane.add(submitButton, 0, 2, 2, 1);
        gridPane.add(feedbackLabel, 0, 3, 2, 1);

        Scene scene = new Scene(gridPane, 300, 200);
        superUserStage.setTitle("Autenticación de Superusuario");
        superUserStage.setScene(scene);
        superUserStage.show();
    }

    private void showErrorMessage(String message) {
        errorMessage.setText(message);
    }

    private void handleManagePassword() {
        // Abrir la ventana de gestionar claves
        GestionarClave gestionarClaveApp = new GestionarClave();
        gestionarClaveApp.start(new Stage());
    }

    private void showHelpMessage() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Acerca del Software");
        helpAlert.setHeaderText("Bienvendio al boton de ayuda de Ades");
        helpAlert.setContentText("Este es un software que facilitara el progreso de su negocio manejando modulos tales como: \n" +
                "insumos, pedidos, cobros, clientes, vendedores e información de su establecimiento. \n\n Si tiene dudas, problemas o sugerencias comuniquese al 3107864885 via Whastapp o llamada telefonica.");
        helpAlert.showAndWait();
    }

    private void showForgotMessage() {
        Alert forgotAlert = new Alert(Alert.AlertType.INFORMATION);
        forgotAlert.setTitle("Recuperación de usuario o contraseña");
        forgotAlert.setHeaderText("¿Olvidaste tu usuario o contraseña?");
        forgotAlert.setContentText("Si olvidaste tu usuario o contraseña debes contactar directamente con el administrador del programa para crear una nueva y comunicarte con desarrollo para la eliminacion de la anterior");
        forgotAlert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}