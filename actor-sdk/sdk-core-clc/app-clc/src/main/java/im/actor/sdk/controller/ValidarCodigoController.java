package im.actor.sdk.controller;


import im.actor.core.entity.AuthRes;
import im.actor.runtime.Log;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static im.actor.sdk.ClcMessenger.messenger;
import static javafx.application.Platform.runLater;

/**
 * Created by diego on 15/06/17.
 */
public class ValidarCodigoController implements Initializable {

    private static final String TAG = ValidarCodigoController.class.getName();

    @FXML
    private Button btnValidar;
    @FXML
    private Button btnCancelar;
    @FXML
    private TextField txCodigo;

    private String transactionHash;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnValidar.setOnAction(ev -> {
            messenger().doValidateCode(txCodigo.getText(), transactionHash)
                    .then(authRes -> {
                        if (!authRes.isNeedToSignup()) {
                            exibirTelaPrincipal(authRes.getResult());
                        }
                    }).failure(ex -> {
                Log.e(TAG, ex);
            });
        });
    }

    public void exibirTelaPrincipal(AuthRes authRes) {
        runLater(() -> {
            messenger().doCompleteAuth(authRes)
                    .then(v -> {
                        runLater(()->{
                            try {
                                Log.d(TAG, "Entrou");

                                Stage stage = (Stage) btnValidar.getScene().getWindow();

                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(getClass().getResource("/fxml/RootLayout.fxml"));
                                StackPane loginLayout = (StackPane) loader.load();
                                RootController controller = loader.<RootController>getController();
                                controller.setAuthRes(authRes);
                                controller.completarAutenticacao();

                                Scene scene = new Scene(loginLayout);
                                stage.setScene(scene);
                                stage.show();

                            } catch (Exception e) {
                                Log.e(TAG, e);
                            }
                        });
                    })
                    .failure(ex -> {
                        Log.e(TAG, ex);
                    });
        });
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
}
