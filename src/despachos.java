import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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

public class despachos extends Application {

    private Stage primaryStage; // Referencia a la ventana principal
    private Text statusLabel; // Texto para mostrar el estado de la conexión
    private Text fechaText, diaText, horaText; // Textos para la fecha, el día y la hora
    private String currentDate, dayOfWeek; // Fecha y día actual
    private Label menuDiaLabel; // Label para mostrar el menú del día

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Asignar la ventana principal
        primaryStage.setTitle("Ades Software");

        Image closeImage = new Image("file:///C:/PROYECTO/images/close.png");  // Cargar la imagen desde la ruta local
        ImageView imageView = new ImageView(closeImage);
        imageView.setFitWidth(80); // Ajustar el tamaño de la imagen
        imageView.setFitHeight(80); // Ajustar el tamaño de la imagen

        // Crear los botones
        Button btnFacturar = createModernButton("Elaborar Factura");
        Button btnMontarpedido = createModernButton("Montar Pedido");
        Button btnGestionClientes = createModernButton("Gestión de Clientes");
        Button btnGeneracionInformes = createModernButton("Cuadre de Caja");
        Button btnCrearProducto = createModernButton("Crear Producto");
        Button btnSeguimientoPedidos = createModernButton("Seguimiento de Pedidos");
        Button btnDomiciliarios = createModernButton("Domiciliarios");
        Button btnMantenimiento = createModernButton("Catálogo Productos");
        Button btnEntrada = createModernButton("Entrada de Inventario");
        Button btnSalidas = createModernButton("Salida de Inventario");
        Button btnCatalogoEntradas = createModernButton("Catálogo Entradas");
        Button btnCatalogoSalidas = createModernButton("Catálogo Salidas");

        Button btnSalir = new Button();
        btnSalir.setGraphic(imageView);  // Asignamos la imagen al botón
        btnSalir.setStyle("-fx-background-color: transparent;"); // Sin fondo

        btnSalir.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER); // Centrar la imagen en el botón
        btnSalir.setAlignment(Pos.CENTER); // Asegurarse de que el contenido esté centrado

        // Manejo de eventos de los botones
        btnFacturar.setOnAction(e -> openFacturarWindow());
        btnMontarpedido.setOnAction(e -> openMontarpedidoWindow());
        btnGestionClientes.setOnAction(e -> openClientesWindow());
        btnGeneracionInformes.setOnAction(e -> openCuadreWindow());
        btnCrearProducto.setOnAction(e -> openCrearProductoWindow());
        btnSeguimientoPedidos.setOnAction(e -> openCatalogoPedidosWindow());
        btnDomiciliarios.setOnAction(e -> openDomiciliariosWindow());
        btnSalir.setOnAction(e -> primaryStage.close());
        btnMantenimiento.setOnAction(e -> openCatalogoProductosWindow());
        btnEntrada.setOnAction(e -> openEntradaWindow());
        btnSalidas.setOnAction(e -> openSalidasWindow());
        btnCatalogoEntradas.setOnAction(e -> openCatalogoEntradasWindow());
        btnCatalogoSalidas.setOnAction(e -> openCatalogoSalidasWindow());

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

        // Crear un layout para los botones (2 VBox dentro de un HBox)
        VBox buttonLayoutLeft = new VBox(10);
        VBox buttonLayoutRight = new VBox(10);
        buttonLayoutLeft.setAlignment(Pos.TOP_LEFT);
        buttonLayoutRight.setAlignment(Pos.TOP_RIGHT);

        // Añadir los botones en los VBox
        buttonLayoutLeft.getChildren().addAll(
                btnFacturar,
                btnMontarpedido,
                btnGestionClientes,
                btnGeneracionInformes,
                btnSeguimientoPedidos,
                btnDomiciliarios
        );

        buttonLayoutRight.getChildren().addAll(
                btnCrearProducto,
                btnMantenimiento,
                btnEntrada,
                btnSalidas,
                btnCatalogoEntradas,
                btnCatalogoSalidas,
                btnSalir
        );

        // Crear un HBox para contener los dos VBox
        HBox mainButtonsLayout = new HBox(40, buttonLayoutLeft, buttonLayoutRight);
        mainButtonsLayout.setAlignment(Pos.CENTER);

        // Crear un layout para el mensaje de estado en la esquina derecha
        VBox statusLayout = new VBox(5);
        Text userLabel = new Text("RESTAURANTE EL BUEN SABOR");
        userLabel.setFill(Color.BLACK);
        statusLayout.setAlignment(Pos.CENTER);
        statusLayout.getChildren().addAll(userLabel, statusLabel);

        // Crear un layout vertical principal
        VBox mainLayout = new VBox(40); // Aumento el espaciado entre los elementos principales
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.getChildren().addAll(mainButtonsLayout, statusLayout);

        // Establecer el fondo de pantalla
        mainLayout.setStyle("-fx-background-image: url('file:///C:/PROYECTO/images/fondo.png'); "
                + "-fx-background-size: cover;");

        // Añadir fecha, día y hora en la parte inferior y centrado
        VBox dateVBox = new VBox(10); // Aumento el espaciado para separar un poco más los textos
        dateVBox.getChildren().addAll(fechaText, diaText, horaText);
        dateVBox.setAlignment(Pos.CENTER); // Alinear a la parte inferior y centrado
        mainLayout.getChildren().add(dateVBox); // Añadir al layout principal

        // Crear la escena y mostrarla
        Scene scene = new Scene(mainLayout, 1200, 800); // Ajustar tamaño de la ventana

        // Vincular el tamaño de los botones y el layout a la ventana
        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double scale = newWidth.doubleValue() / 980;
            mainButtonsLayout.setStyle("-fx-font-size: " + (scale * 16) + "px;"); // Ajuste dinámico del tamaño de los botones
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        // Actualizar la hora cada segundo
        startClock();
    }

    private void openEntradaWindow() {
        // Crear una nueva instancia de Entradas sin pasarle el primaryStage
        Entrada entradasWindow = new Entrada();  // Solo creamos el objeto, no es necesario pasar el primaryStage
        entradasWindow.start(new Stage());  // Iniciar Entradas con un nuevo Stage

        primaryStage.hide();  // Ocultar la ventana principal
    }

    private void openSalidasWindow() {
        // Crear una nueva instancia de Entradas sin pasarle el primaryStage
        Salidas SalidasWindow = new Salidas();  // Solo creamos el objeto, no es necesario pasar el primaryStage
        SalidasWindow.start(new Stage());  // Iniciar Entradas con un nuevo Stage

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