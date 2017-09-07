package clc;

import im.actor.core.Configuration;
import im.actor.core.ConfigurationBuilder;
import im.actor.core.Messenger;
import im.actor.core.modules.ModuleCreateListener;
import im.actor.core.modules.Modules;
import im.actor.core.providers.PhoneBookProvider;
import im.actor.runtime.Log;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.ActorSDKCreateListener;
import im.actor.sdk.ClcMessenger;
import im.actor.sdk.controller.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;




public class FxClcApplication extends Application {

    private static final String TAG = FxClcApplication.class.getName();


    @Override
    public void start(Stage stage) throws Exception {

        System.out.println(Thread.currentThread().getName());

        ActorSDK.sharedActor().setEndpoints(new String[]{"tcp://127.0.0.1:9070"});

        ActorSDK.sharedActor().createActor();

        stage.setTitle("XLoto Mensageiro");

        ActorSDK.sharedActor().waitForReady();

        if (ActorSDK.sharedActor().getMessenger().isLoggedIn()) {
            abrirTelaPrincipal(stage);
        } else {
            abrirTelaLogin(stage);
        }
    }

    private void abrirTelaLogin(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/LoginLayout.fxml"));
            VBox loginLayout = (VBox) loader.load();
            Scene scene = new Scene(loginLayout);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            Log.e(TAG, e);
        }
    }

    private void abrirTelaPrincipal(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/RootLayout.fxml"));
            StackPane loginLayout = (StackPane) loader.load();
            RootController controller = loader.<RootController>getController();
            Scene scene = new Scene(loginLayout);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            Log.e(TAG, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

