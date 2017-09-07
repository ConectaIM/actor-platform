package im.actor.sdk.controller;

import im.actor.core.entity.AuthRes;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by diego on 15/06/17.
 */
public class RootController implements Initializable {


    private static final String TAG = RootController.class.getName();

    private AuthRes authRes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void completarAutenticacao(){

    }

    public void setAuthRes(AuthRes authRes) {
        this.authRes = authRes;
    }
}
