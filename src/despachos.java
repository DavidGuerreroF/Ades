import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.geometry.Insets;

public class despachos extends Application {

    private Stage primaryStage; // Referencia a la ventana principal
    private Text statusLabel; // Texto para mostrar el estado de la conexión
    private Text fechaText, diaText, horaText; // Textos para la fecha, el día y la hora
    private String currentDate, dayOfWeek; // Fecha y día actual
    private VBox mainLayout; // Layout principal

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Asignar la ventana principal
        primaryStage.setTitle("Ades Software Version 2025.1");

        Image closeImage = new Image("file:///C:/PROYECTO/images/close.png");  // Cargar la imagen desde la ruta local
        ImageView imageView = new ImageView(closeImage);
        imageView.setFitWidth(80); // Ajustar el tamaño de la imagen
        imageView.setFitHeight(80); // Ajustar el tamaño de la imagen

        // Crear los botones
        Button btnFacturar = createModernButton("Facturacion");
        Button btnMontarpedido = createModernButton("Montar Pedido");
        Button btnMenu = createModernButton("Menu del dia");
        Button btnGestionClientes = createModernButton("Clientes");
        Button btnGeneracionInformes = createModernButton("Cuadre de Caja");
        Button btnCrearProducto = createModernButton("Crear insumos");
        Button btnSeguimientoPedidos = createModernButton("Catalogo de Pedidos");
        Button btnDomiciliarios = createModernButton("Domiciliarios");
        Button btnCatalogoInsumos = createModernButton("Catalogo Insumos");
        Button btnEntrada = createModernButton("Entrada de Insumos");
        Button btnSalidas = createModernButton("Salida de Insumos");
        Button btnCatalogoEntradas = createModernButton("Catálogo Entradas");
        Button btnCatalogoSalidas = createModernButton("Catálogo Salidas");

        Button btnSalir = new Button();
        btnSalir.setGraphic(imageView);  // Asignamos la imagen al botón
        btnSalir.setStyle("-fx-background-color: transparent;"); // Sin fondo
        btnSalir.setCursor(Cursor.HAND);

        btnSalir.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER); // Centrar la imagen en el botón
        btnSalir.setAlignment(Pos.CENTER); // Asegurarse de que el contenido esté centrado

        // Manejo de eventos de los botones
        btnFacturar.setOnAction(e -> openFacturarWindow());
        btnMenu.setOnAction(e -> openMenudiaWindow());
        btnMontarpedido.setOnAction(e -> openMontarpedidoWindow());
        btnGestionClientes.setOnAction(e -> openClientesWindow());
        btnGeneracionInformes.setOnAction(e -> openCuadreWindow());
        btnCrearProducto.setOnAction(e -> openCrearProductoWindow());
        btnSeguimientoPedidos.setOnAction(e -> openCatalogoPedidosWindow());
        btnDomiciliarios.setOnAction(e -> openDomiciliariosWindow());
        btnCatalogoInsumos.setOnAction(e -> openCatalogoProductosWindow());
        btnEntrada.setOnAction(e -> openEntradaWindow());
        btnSalidas.setOnAction(e -> openSalidasWindow());
        btnCatalogoEntradas.setOnAction(e -> openCatalogoEntradasWindow());
        btnCatalogoSalidas.setOnAction(e -> openCatalogoSalidasWindow());
        btnSalir.setOnAction(e -> {
            // Cierra la aplicación
            System.exit(0);
        });


        // Cargar la fecha y el día de la semana
        LocalDate today = LocalDate.now();
        currentDate = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        dayOfWeek = today.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("es", "ES"));

        // Crear Text para la fecha y el día con borde y sombra
        fechaText = new Text(currentDate);
        fechaText.setFont(Font.font("Arial", 35));  // Usé una fuente estándar más grande
        fechaText.setFill(Color.WHITE); // Color de texto
        fechaText.setStroke(Color.BLACK); // Color del borde
        fechaText.setStrokeWidth(1.5); // Ancho del borde

        diaText = new Text(dayOfWeek);
        diaText.setFont(Font.font("Arial", 32));  // Fuente más pequeña para el día
        diaText.setFill(Color.WHITE); // Color de texto
        diaText.setStroke(Color.BLACK); // Color del borde
        diaText.setStrokeWidth(1.5); // Ancho del borde

        // Crear el Text para mostrar la hora con segundos
        horaText = new Text();
        horaText.setFont(Font.font("Arial", 35)); // Usar el mismo tamaño de fuente que la fecha
        horaText.setFill(Color.WHITE); // Color de texto
        horaText.setStroke(Color.BLACK); // Color del borde
        horaText.setStrokeWidth(1.5); // Ancho del borde

        // Crear el Text para mostrar el estado de la conexión a la base de datos
        statusLabel = new Text("Estado: Desconectado");
        statusLabel.setFont(Font.font(12));
        statusLabel.setFill(Color.RED); // Estado por defecto en rojo (offline)

        // Verificar la conexión a la base de datos en un hilo separado
        new Thread(this::checkDatabaseConnection).start();

        // Crear un layout para los botones en un VBox (menú desplegable)
        VBox insumosLayout = new VBox(10);
        insumosLayout.setAlignment(Pos.TOP_LEFT);
        insumosLayout.setPadding(new Insets(10));
        insumosLayout.setPrefWidth(300); // Establecer el ancho del VBox
        insumosLayout.setVisible(false); // Iniciar oculto

        VBox despachosLayout = new VBox(10);
        despachosLayout.setAlignment(Pos.TOP_LEFT);
        despachosLayout.setPadding(new Insets(10));
        despachosLayout.setPrefWidth(300); // Establecer el ancho del VBox
        despachosLayout.setVisible(false); // Iniciar oculto


        // Añadir botones a los VBox correspondientes
        insumosLayout.getChildren().addAll(
                btnCrearProducto,
                btnEntrada,
                btnSalidas,
                btnCatalogoInsumos,
                btnCatalogoEntradas,
                btnCatalogoSalidas
        );

        despachosLayout.getChildren().addAll(
                btnFacturar,
                btnMontarpedido,
                btnSeguimientoPedidos,
                btnDomiciliarios,
                btnGestionClientes,
                btnGeneracionInformes,
                btnMenu
        );

        // Crear un botón para los menús desplegables
        Button btnInsumos = createModernButton("Insumos");
        btnInsumos.setOnAction(e -> toggleVisibility(insumosLayout));

        Button btnDespachos = createModernButton("Despachos");
        btnDespachos.setOnAction(e -> toggleVisibility(despachosLayout));


        // Crear el VBox principal para los botones de menú desplegable
        VBox mainButtonLayout = new VBox(10);
        mainButtonLayout.setAlignment(Pos.TOP_LEFT);
        mainButtonLayout.setPadding(new Insets(10));
        mainButtonLayout.setPrefWidth(300); // Establecer el ancho del VBox

        mainButtonLayout.getChildren().addAll(
                btnInsumos,
                insumosLayout,
                btnDespachos,
                despachosLayout
        );

        // Crear un ScrollPane para el menú desplegable
        ScrollPane scrollPane = new ScrollPane(mainButtonLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Desactivar barra horizontal si no es necesaria

        // Crear un layout vertical principal
        mainLayout = new VBox(40); // Aumento el espaciado entre los elementos principales
        mainLayout.setAlignment(Pos.TOP_CENTER);

        // Crear un HBox para contener el ScrollPane del menú y otros elementos
        HBox contentLayout = new HBox(20, scrollPane, mainLayout);
        contentLayout.setAlignment(Pos.TOP_LEFT);

        // Crear un layout para el mensaje de estado en la esquina derecha
        VBox statusLayout = new VBox(5);
        Text userLabel = new Text("RESTAURANTE EL BUEN SABOR");
        userLabel.setFill(Color.BLACK);
        statusLayout.setAlignment(Pos.CENTER);
        statusLayout.getChildren().addAll(userLabel, statusLabel, btnSalir);

        mainLayout.getChildren().add(statusLayout);

        // Establecer el fondo de pantalla
        contentLayout.setStyle("-fx-background-image: url('file:///C:/PROYECTO/images/fondo.png'); "
                + "-fx-background-size: cover;");

        // Añadir fecha, día y hora en la parte inferior y centrado
        VBox dateVBox = new VBox(10); // Aumento el espaciado para separar un poco más los textos
        dateVBox.getChildren().addAll(fechaText, diaText, horaText);
        dateVBox.setAlignment(Pos.CENTER); // Alinear a la parte inferior y centrado
        mainLayout.getChildren().add(dateVBox); // Añadir al layout principal

        // Crear la escena y mostrarla
        Scene scene = new Scene(contentLayout, 700, 700); // Ajustar tamaño de la ventana
        primaryStage.setScene(scene);  // Utiliza el primaryStage existente
        primaryStage.setResizable(false); // Desactiva la opción de redimensionar
        primaryStage.show();

        // Vincular el tamaño de los botones y el layout a la ventana
        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double scale = newWidth.doubleValue() / 800;
            mainButtonLayout.setStyle("-fx-font-size: " + (scale * 16) + "px;"); // Ajuste dinámico del tamaño de los botones
        });

        // Actualizar la hora cada segundo
        startClock();

        // Mostrar el menú del día en una esquina
        showMenuDia();
    }

    private void toggleVisibility(VBox layout) {
        layout.setVisible(!layout.isVisible());
    }

    private void showMenuDia() {
        // Cargar contenido del archivo 'menu.txt'
        String menuContent = loadMenuFromFile("C:\\PROYECTO\\menu.txt");

        // Crear un área de texto para mostrar el contenido del menú
        TextArea menuTextArea = new TextArea(menuContent);
        menuTextArea.setEditable(false); // No se puede editar el texto
        menuTextArea.setWrapText(true); // Ajusta el texto dentro del área
        menuTextArea.setFont(Font.font("Arial", 12)); // Fuente más pequeña para mejor ajuste
        menuTextArea.setStyle("-fx-background-color: #f9f9f9; -fx-text-fill: #2e2e2e; -fx-font-weight: bold; -fx-font-size: 14px;"); // Estilo moderno

        // Personalizar la barra de desplazamiento
        ScrollPane scrollPane = new ScrollPane(menuTextArea);
        scrollPane.setFitToWidth(true); // Ajusta el contenido al ancho de la ventana
        scrollPane.setFitToHeight(true); // Ajusta el contenido al alto de la ventana
        scrollPane.setStyle("-fx-background-color: transparent;"); // Fondo transparente para ScrollPane
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Desactivar barra horizontal si no es necesaria

        // Crear un VBox para organizar el menú del día
        VBox menuVBox = new VBox();
        menuVBox.setMaxWidth(300); // Ancho máximo para el área del menú
        menuVBox.setMaxHeight(600); // Alto máximo para el área del menú
        menuVBox.setPadding(new Insets(10));
        menuVBox.getChildren().add(scrollPane);

        // Añadir el VBox del menú al layout principal en la esquina superior derecha
        mainLayout.getChildren().add(0, menuVBox);
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

    private void openEntradaWindow() {
        // Crear una nueva instancia de Entradas sin pasarle el primaryStage
        Entrada entradasWindow = new Entrada();  // Solo creamos el objeto, no es necesario pasar el primaryStage
        entradasWindow.start(new Stage());  // Iniciar Entradas con un nuevo Stage

        primaryStage.hide();  // Ocultar la ventana principal
    }

    private void openSalidasWindow() {
        // Crear una nueva instancia de Entradas sin pasarle el primaryStage
        Salidas salidasWindow = new Salidas();  // Solo creamos el objeto, no es necesario pasar el primaryStage
        salidasWindow.start(new Stage());  // Iniciar Entradas con un nuevo Stage

        primaryStage.hide();  // Ocultar la ventana principal
    }

    private void openFacturarWindow() {
        // Crear una nueva instancia de Facturar sin pasarle el primaryStage
        Facturar facturarWindow = new Facturar();  // Crear el objeto Facturar
        facturarWindow.start(new Stage());  // Iniciar Facturar con un nuevo Stage

        primaryStage.hide();  // Ocultar la ventana principal
    }

    private void openCatalogoEntradasWindow() {
        // Crear una nueva instancia de CatalogoEntradas sin pasarle el primaryStage
        CatalogoEntradas catalogoEntradasWindow = new CatalogoEntradas();
        catalogoEntradasWindow.start(new Stage());  // Iniciar CatalogoEntradas con un nuevo Stage

        primaryStage.hide();  // Ocultar la ventana principal
    }

    private void openCatalogoSalidasWindow() {
        // Crear una nueva instancia de CatalogoSalidas sin pasarle el primaryStage
        CatalogoSalidas catalogoSalidasWindow = new CatalogoSalidas();
        catalogoSalidasWindow.start(new Stage());  // Iniciar CatalogoSalidas con un nuevo Stage

        primaryStage.hide();  // Ocultar la ventana principal
    }

    private void checkDatabaseConnection() {
        try {
            // Intentar conectar a la base de datos
            Connection connection = conexionDB.getConnection();
            // Si la conexión es exitosa, actualizamos el estado a "Online"
            updateConnectionStatus("Online", Color.GREEN);
            connection.close(); // Cerrar la conexión
        } catch (SQLException e) {
            // Si no se puede conectar, actualizar el estado a "Offline"
            updateConnectionStatus("Offline", Color.RED);
        }
    }

    private void updateConnectionStatus(String status, Color color) {
        // Actualizamos el estado de la conexión en el UI (debe hacerse en el hilo principal)
        Platform.runLater(() -> {
            statusLabel.setText("Estado: " + status);
            statusLabel.setFill(color); // Cambiar color a verde (online) o rojo (offline)
        });
    }

    private void startClock() {
        // Usamos Timeline para actualizar la hora cada segundo
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> updateClock())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateClock() {
        LocalTime now = LocalTime.now();
        String formattedTime = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        horaText.setText(formattedTime);
    }

    private void openCatalogoPedidosWindow() {
        // Crear una nueva instancia de la clase CatalogoPedidos
        CatalogoPedidos catalogoPedidosWindow = new CatalogoPedidos();

        // Llamar al método start de la nueva instancia de CatalogoPedidos
        catalogoPedidosWindow.start(new Stage()); // Usar un nuevo Stage aquí

        // Ocultar la ventana principal
        primaryStage.hide();
    }

    private void openCatalogoProductosWindow() {
        // Crear una nueva instancia de la clase CatalogoProductos
        CatalogoProductos catalogoProductosWindow = new CatalogoProductos();

        // Llamar al método start de la nueva instancia de CatalogoProductos
        catalogoProductosWindow.start(new Stage()); // Usar un nuevo Stage aquí

        // Ocultar la ventana principal
        primaryStage.hide();
    }

    private void openClientesWindow() {
        Stage clientesStage = new Stage();
        clientes clientesWindow = new clientes(primaryStage);
        clientesWindow.start(clientesStage);
        primaryStage.hide();
    }

    private void openCuadreWindow() {
        Stage cuadreCajaStage = new Stage();
        Cuadre cuadreCajaWindow = new Cuadre(); // Crear una instancia de la ventana CuadreCaja
        cuadreCajaWindow.start(cuadreCajaStage); // Iniciar la ventana
        primaryStage.hide(); // Ocultar la ventana principal (o la ventana actual)
    }

    private void openMenudiaWindow() {
        Stage menudiaStage = new Stage();
        Menudia menudiaWindow = new Menudia(); // Crear una instancia de la ventana CuadreCaja
        menudiaWindow.start(menudiaStage); // Iniciar la ventana
        primaryStage.hide(); // Ocultar la ventana principal (o la ventana actual)
    }

    private void openMontarpedidoWindow() {
        // Crear un nuevo Stage para la ventana de Montarpedido
        Stage montarpedidoStage = new Stage();

        // Crear una instancia de Montarpedido, pero NO pasarle el primaryStage
        Montarpedido montarpedido = new Montarpedido();

        // Establecer la escena y mostrar la ventana
        montarpedido.start(montarpedidoStage);

        // Ocultar el Stage principal (primaryStage) si es necesario
        primaryStage.hide();
    }

    private void openDomiciliariosWindow() {
        Stage domiciliariosStage = new Stage();
        domiciliarios domiciliariosWindow = new domiciliarios(primaryStage);
        domiciliariosWindow.start(domiciliariosStage);
        primaryStage.hide();
    }

    private void openCrearProductoWindow() {
        Stage crearProductoStage = new Stage();
        CrearProducto crearProductoWindow = new CrearProducto();
        crearProductoWindow.start(crearProductoStage);
        primaryStage.hide();
    }


    // Crea un botón moderno con efectos
    private Button createModernButton(String text) {
        Button button = new Button(text);
        button.setMinSize(250, 60); // Tamaño del botón
        button.setStyle("-fx-background-color: white; "
                + "-fx-text-fill: black; "
                + "-fx-font-size: 16px; "
                + "-fx-border-color: #cccccc; "
                + "-fx-border-width: 2px; "
                + "-fx-border-radius: 3px; "
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0, 2);");

        // Cambiar el cursor a "mano" cuando el puntero esté sobre el botón
        button.setCursor(Cursor.HAND);

        // Efecto al pasar el ratón por encima
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: white; "
                + "-fx-text-fill: black; "
                + "-fx-font-size: 16px; "
                + "-fx-border-color: #40E0D0; " // Azul Aguamarina
                + "-fx-border-width: 2px; "
                + "-fx-border-radius: 3px; "
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0, 2);"));

        // Restaurar estilo al sacar el ratón
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: white; "
                + "-fx-text-fill: black; "
                + "-fx-font-size: 16px; "
                + "-fx-border-color: #cccccc; "
                + "-fx-border-width: 2px; "
                + "-fx-border-radius: 3px; "
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0.5, 0, 2);"));

        return button;
    }


    public static void main(String[] args) {
        launch(args);
    }
}