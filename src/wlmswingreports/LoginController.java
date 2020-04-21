/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wlmswingreports;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author mdonovan
 */
public class LoginController implements Initializable {

    @FXML 
    public ComboBox locationCombo;
    @FXML
    public Button loginButton;
    @FXML
    public Button cancelButton;
    @FXML 
    public Scene scene;
    private Boolean authenticated = true;
    @FXML
    public TextField userText;
    @FXML 
    PasswordField pwText;
    @FXML Label titleLabel;
    
    ConnectionPool cpds = ConnectionPool.getInstance();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        locationCombo.getItems().addAll(SessionManager.getRestaurants());
        userText.requestFocus();
    }    
    
    public void loginPressed(ActionEvent ae) throws IOException{
        SessionManager.Restaurant selectedRestaurant = (SessionManager.Restaurant)
                locationCombo.getSelectionModel().getSelectedItem();
        // TODO replace with real authentication here if needed
        if ( selectedRestaurant.validateRestaurantLogin(userText.getText().trim(), pwText.getText())){
            SessionManager.setIsAuthenticated(true);
        }else{
            SessionManager.setIsAuthenticated(false);
        }
        //REMOVE FOR PRODUCTION
        SessionManager.setIsAuthenticated(true);
        if (null == locationCombo.getValue()){
            Alert alert = new Alert(AlertType.WARNING, "Select a database");
            alert.show();
            locationCombo.requestFocus();
        }
        else{
            if (SessionManager.getIsAuthenticated()){
                SessionManager.Restaurant r = (SessionManager.Restaurant) locationCombo.getValue();
                SessionManager.setActiveRestaurant(r);                               
                cpds.connect(r);
                //System.out.println("loc" + l.locID);
                Node source = (Node) ae.getSource();        
                Stage stage = (Stage) source.getScene().getWindow();
                //Stage st = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainAppScene.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                stage.setTitle("LHRC Report Module   " + 
                        "                     Current Database " +
                        SessionManager.getActiveRestaurant().getRestaurantName());
                Scene mainScene = new Scene(root1, 1400,800);
                //MainAppSceneController c = (MainAppSceneController) fxmlLoader.getController();
                stage.setScene(mainScene);
                stage.centerOnScreen();
            }
            else{
               Alert alert = new Alert(AlertType.WARNING, "Login Failed. Please try again");
               alert.showAndWait();
            }
        }
    }
    
    public void cancelPressed(ActionEvent ae){
        Node source = (Node) ae.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
        private class Location{
            public String locName;
            public int locID;
            
            public Location(String n, int id){
                locName = n;
                locID = id;
            }
            
            public String toString(){
                return locName;
            }
        }
}
