package im.actor.sdk.controller;


import im.actor.core.AuthState;
import im.actor.runtime.Log;
import im.actor.sdk.ActorSDK;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Platform.runLater;

/**
 * Created by diego on 15/06/17.
 */
public class LoginController implements Initializable {

    private static final String TAG = LoginController.class.getName();

    @FXML
    private Button btnEntrar;

    @FXML
    private Button btnCancelar;

    @FXML
    private TextField txTelefone;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnEntrar.setOnAction(ev -> {
            ActorSDK.sharedActor().getMessenger().doStartPhoneAuth(Long.parseLong(txTelefone.getText()))
                    .then(authRes -> {
                        if (AuthState.LOGGED_IN == authRes.getAuthMode()) {
                            Log.d(TAG, "Usuario logado");
                        } else if (AuthState.AUTH_START == authRes.getAuthMode()) {
                            exibirTelaValidacaoCodigo(authRes.getTransactionHash());
                        }
                    }).failure(ex -> {
                Log.e(TAG, ex);
            });
        });
    }

    public void exibirTelaValidacaoCodigo(String transactionHash) {

        runLater(()->{
            try {
                Stage stage = (Stage) btnEntrar.getScene().getWindow();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/fxml/ValidarCodigoLayout.fxml"));
                VBox loginLayout = (VBox) loader.load();
                ValidarCodigoController controller = loader.<ValidarCodigoController>getController();
                controller.setTransactionHash(transactionHash);

                Scene scene = new Scene(loginLayout);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                Log.e(TAG, e);
            }
        });


    }
}
