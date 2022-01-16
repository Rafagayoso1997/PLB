package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXProgressBar;
import eu.mihosoft.scaledfx.ScalableContentPane;
import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.util.Duration;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import main.Controller;
import model.Empleado;
import model.Empresa;
import services.ServicesLocator;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    private final String LEAP_YEAR = "config_files" + File.separator + "Schedule Model Leap Year.xlsx";
    private final String REGULAR = "config_files" + File.separator + "Schedule Model Regular Year.xlsx";
    private final String HELP = "config_files" + File.separator + "Control de horarios Palobiofarma S,L & Medibiofarma.pdf";
    private final String PALOBIOFARMA = "config_files" + File.separator + "palobiofarma.png";
    private final String MEDIBIOFARMA = "config_files" + File.separator + "medibiofarma.png";

    private TrayNotification notification;

    @FXML
    private JFXButton fileMenu;

    @FXML
    private Label resultLabel;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private AnchorPane root;

    private ArrayList<File> listFiles;

    ImageView[] slides;

    private File file;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
        //File button
        fileMenu.requestFocus();
        AnchorPane popupPane = new AnchorPane();
        VBox vBox = new VBox();
        JFXButton btnLoadCalendar =createMainPopUpBtn("/resources/images/calendar.png","Cargar Calendario" );
        
        JFXButton btnSchedule =createMainPopUpBtn("/resources/images/excel.png", "Generar Horarios");

        JFXButton close = createMainPopUpBtn("/resources/images/logout.png", "Cerrar");

        vBox.getChildren().add(btnLoadCalendar);
        vBox.getChildren().add(btnSchedule);
        vBox.getChildren().add(close);
        popupPane.getChildren().add(vBox);
        JFXPopup popup = new JFXPopup(popupPane);

        JFXPopup popupEnterprise = new JFXPopup(enterprisesPopUp());

        //file button
        fileMenu.setOnAction(event -> popup.show(fileMenu, JFXPopup.PopupVPosition.TOP,
                JFXPopup.PopupHPosition.LEFT, fileMenu.getLayoutX(), fileMenu.getLayoutY() + 50));

        btnLoadCalendar.setOnAction(event -> loadSchedule(popup));

        btnSchedule.setOnAction(event -> popupEnterprise.show(btnSchedule,JFXPopup.PopupVPosition.TOP,
                JFXPopup.PopupHPosition.LEFT, btnSchedule.getLayoutX()+140,
                btnSchedule.getLayoutY()-30));

        close.setOnAction(event -> closeApplication());

        notification = new TrayNotification();
        listFiles = new ArrayList<>();
        getSlides();
        createSlideShow();
        resultLabel.setText("");

        progressBar.setProgress(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private AnchorPane enterprisesPopUp() throws FileNotFoundException {
        AnchorPane popupPane = new AnchorPane();
        VBox vBox = new VBox();

        JFXButton btnPaloMataro = createEnterprisePopUpBtn(PALOBIOFARMA, "Palobiofarma Mataró S.L", 1);
        JFXButton btnPaloPamplona  = createEnterprisePopUpBtn(PALOBIOFARMA, "Palobiofarma Pamplona S.L", 2);
        JFXButton btnMedi = createEnterprisePopUpBtn(MEDIBIOFARMA, "Medibiofarma", 3);

        vBox.getChildren().add(btnPaloMataro);
        vBox.getChildren().add(btnPaloPamplona);
        vBox.getChildren().add(btnMedi);

        popupPane.getChildren().add(vBox);

        return popupPane;
    }
    
    private ImageView createImageViewBtn(String enterprise) throws FileNotFoundException {
        ImageView view = new ImageView(new Image(new FileInputStream(enterprise)));
        view.setFitWidth(25);
        view.setFitHeight(25);
        return view;
    }

    private ImageView createMainImageView(String logo){
        ImageView view = new ImageView(new Image(getClass().getResourceAsStream(logo)));
        view.setFitWidth(25);
        view.setFitHeight(25);
        return view;
    }

    private JFXButton createMainPopUpBtn(String logo, String text){
        JFXButton btn = new JFXButton(text);
        ImageView view = createMainImageView(logo);
        btn.setGraphic(view);
        btn.setCursor(Cursor.HAND);
        return btn;
    }


    private JFXButton createEnterprisePopUpBtn(String enterprise, String text, int cod_enterprise) throws FileNotFoundException {
        JFXButton btn = new JFXButton(text);
        ImageView view = createImageViewBtn(enterprise);
        btn.setGraphic(view);
        btn.setCursor(Cursor.HAND);
        btn.setOnAction(event -> {
            DirectoryChooser fc = new DirectoryChooser();
            File f = fc.showDialog(new Stage());
            mergeExcel(cod_enterprise, f.getAbsolutePath());
        });
        return btn;
    }

    private void createSlideShow() {

        SequentialTransition slideshow = new SequentialTransition();
        for (ImageView slide : slides) {

            SequentialTransition sequentialTransition = new SequentialTransition();

            FadeTransition fadeIn = getFadeTransition(slide, 0.0, 1.0, 2000);
            PauseTransition stayOn = new PauseTransition(Duration.millis(2000));
            FadeTransition fadeOut = getFadeTransition(slide, 1.0, 0.0, 2000);

            sequentialTransition.getChildren().addAll(fadeIn, stayOn, fadeOut);
            slide.setOpacity(0);
            this.root.getChildren().add(slide);
            slideshow.getChildren().add(sequentialTransition);

        }

        slideshow.play();
    }
    
    public FadeTransition getFadeTransition(ImageView imageView, double fromValue, double toValue, int durationInMilliseconds) {

        FadeTransition ft = new FadeTransition(Duration.millis(durationInMilliseconds), imageView);
        ft.setFromValue(fromValue);
        ft.setToValue(toValue);
        return ft;
    }

    @FXML
    void loadSchedule(JFXPopup popup){
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();

        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Documento Excel", "*xlsx"));
        file = fc.showOpenDialog(stage);

        if (file == null) {
            notification.setMessage("Falló la importación del calendario");
            notification.setTitle("Importación de calendario");
            notification.setNotificationType(NotificationType.ERROR);
        }
        else{
            listFiles.add(file);
            notification.setMessage("Se ha importado el calendario");
            notification.setTitle("Importacion de calendario");
            notification.setNotificationType(NotificationType.SUCCESS);
        }
        notification.showAndDismiss(Duration.millis(5000));
        notification.setAnimationType(AnimationType.POPUP);

        popup.hide();
    }

    public void mergeExcel(int cod_empresa, String ruta) {

        try {

            Empresa empresa = ServicesLocator.getEnterprise().getEmpresaByCod(cod_empresa);
            ArrayList<Empleado> lista = ServicesLocator.getEmployee().listadoEmpleadosXEmpresa(empresa.getNombre());
            System.out.println(lista.size());

            String [] nombre = this.file.getName().split(" ");
            int year = Integer.parseInt(nombre[0]);
            File file;
            if(year%4 !=0 ){
                file = new File(REGULAR);
            }else{
                file = new File(LEAP_YEAR);
            }

            listFiles.add(file);
            if (listFiles.size() < 2) {
                System.out.println("ERROR");
            }

            File foto1 = new File(PALOBIOFARMA);
            listFiles.add(foto1);

            File foto2 = new File(MEDIBIOFARMA);
            listFiles.add(foto2);


            Task<Void> longTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Controller controller = new Controller();
                    controller.mergeExcelFiles(lista, empresa, listFiles, ruta);
                    return null;
                }
            };

            longTask.setOnSucceeded(event -> {
                progressBar.setProgress(100);
                resultLabel.setText("   DONE!!!");
                notification.setMessage("Modelos de horarios creados");
                notification.setTitle("Control de horario");
                notification.setNotificationType(NotificationType.SUCCESS);
                notification.showAndDismiss(Duration.millis(5000));
                notification.setAnimationType(AnimationType.POPUP);

            });

            longTask.setOnRunning(event -> {
                resultLabel.setText("Working on it");
                progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS); //el progressbar este esta al berro
            });

            new Thread(longTask).start();


            //listFiles.remove(listFiles.size()-1);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void closeApplication() {
        System.exit(0);
    }

    private void getSlides() {
        slides = new ImageView[100];
        Image image1 = null;
        Image image2 = null;
        try{
            image1 = new Image(new FileInputStream(PALOBIOFARMA));
            image2 = new Image(new FileInputStream(MEDIBIOFARMA));
        }catch(Exception e){
            e.printStackTrace();
        }
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                slides[i] = new ImageView(image1);
            } else {
                slides[i] = new ImageView(image2);
            }

            slides[i].setFitHeight(200);
            slides[i].setFitWidth(794);
        }
    }

    @FXML
    void showEmployeesData() {
        try {
            System.out.println("Panel de edicion de empleados" + "\n" + "-------------------------");
            ScalableContentPane scale = new ScalableContentPane();
            FXMLLoader loader = new FXMLLoader();
            Parent root = FXMLLoader.load(getClass().getResource("/view/EmployesManagement.fxml"));
            loader.setLocation(MainMenuController.class.getResource("/view/EmployesManagement.fxml"));
            AnchorPane page = loader.load();
            scale.setContent(root);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Gestionar Empleados");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));
            dialogStage.setAlwaysOnTop(true);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showHelp() throws IOException {
        File file = new File(HELP);

        //first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        //let's try to open PDF file
        if(file.exists()) {
            desktop.open(file);
        }
    }


}