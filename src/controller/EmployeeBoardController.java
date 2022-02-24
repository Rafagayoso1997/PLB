package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Empleado;
import services.GetVacationsService;
import utils.FillProgressIndicator;
import utils.FormatEmployeeName;
import utils.SMBUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class EmployeeBoardController implements Initializable {

    @FXML
    private ImageView profilePic;


    @FXML
    private JFXButton vacationsBtn;

    @FXML
    private JFXButton scheduleBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Empleado employee = LoginController.getEmpleado();

        vacationsBtn.setOnAction(action->{
            ArrayList<Empleado> employees = new ArrayList<Empleado>();
            employees.add(employee);
            GetVacationsService services = new GetVacationsService(employees);
            services.start();

            FillProgressIndicator indicator = new FillProgressIndicator();
            Slider slider = new Slider(0, 100, 50);

            slider.valueProperty().addListener((o, oldVal, newVal) -> indicator.setProgress(newVal.intValue()));
            VBox main = new VBox(1, indicator, slider);
            indicator.setProgress(Double.valueOf(slider.getValue()).intValue());
            Stage primaryStage = new Stage();
            Scene scene = new Scene(main);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Test fill progress");
            primaryStage.show();

        });

        scheduleBtn.setOnAction(action->{
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/EmployeeSchedulePane.fxml"));
                Parent parent = fxmlLoader.load();
                EmployeeSchedulePaneController test  = fxmlLoader.getController();
                String employeeFileName = FormatEmployeeName.getEmployeesFileName(employee);
                SMBUtils.downloadSmbFile(employee.getNombre_empresa(),employeeFileName, employee.getDireccionCronograma());
                test.setData(employee);
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Control de Horarios Palobiofarma S.L & Medibiofarma");
                dialogStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/resources/images/palobiofarma.png")));

                Scene scene = new Scene(parent);
                dialogStage.setScene(scene);
                dialogStage.show();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
