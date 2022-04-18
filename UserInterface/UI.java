package UserInterface;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import AlertSystem.Alerts;
import Plugins.Operations;
import javafx.stage.DirectoryChooser;
import com.jcraft.jsch.JSchException;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.jcraft.jsch.SftpException;
import java.util.ArrayList;
import Bridge.Populate;
import Connections.Sftp;
import Product.Info;
import Updates.CheckUpdates;
import Connections.Transfers;
import static javafx.application.Platform.exit;
import java.io.FileNotFoundException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author PolyDev
 */

class Launcher {
    public static void main(String[] args) {
        UI.main(args);
    }
}

public class UI extends Application {

    public static boolean isConnected = false;
    public static ObservableList list1;
    public static HBox hbox;
    public static VBox noConnectionVBox;
    //public TreeMap<String, List<String>> = new TreeMap<>();
    public static MenuBar menu;
    private TextField hostEdit;
    private ContextMenu treeViewContext = new ContextMenu();
    private TextField usernameEdit;
    private PasswordField passwordEdit;
    private TextField portEdit;
    private TreeView<String> treeView;
    private Button connectBtn;
    private final ComboBox<String> connectionTypes = new ComboBox<>();
    public String o_new_;

    @Override
    public void start(Stage primaryStage) {
        VBox vbox = new VBox();
        // Available Connection Methods
        connectionTypes.getItems().addAll("SFTP", "FTP");
        //connectionTypes.setPromptText("Connection Method");
        connectionTypes.getSelectionModel().selectFirst();
        // MenuBar
        menu = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu pluginsMenu = new Menu("Plugins");
        Menu allPluginsMenu = new Menu("Installed Plugins");
        Menu helpMenu = new Menu("Help");
        Menu serverMenu = new Menu("Server");
        serverMenu.setVisible(false);
        MenuItem loadPluginItem = new MenuItem("Load Plugin from file");
        MenuItem pluginMarketplace = new MenuItem("Download Plugins");
        MenuItem exit_ = new MenuItem("Exit");
        MenuItem disconnect = new MenuItem("Server Disconnect");
        MenuItem connect = new MenuItem("Server Connect");
        MenuItem checkForUpdates = new MenuItem("Check for Updates");
        MenuItem about = new MenuItem("About Blink");
        about.setOnAction(event -> Alerts.Informational("About", "Blink is an FTP/SFTP Client", "BlinkClient is developed and maintained\nby Polydev.", "Visit website", "website"));
        MenuItem uploadAction = new MenuItem("Upload Files");
        MenuItem downloadAction = new MenuItem("Download Files");
        uploadAction.setVisible(false);
        downloadAction.setVisible(false);
       
        fileMenu.getItems().addAll(uploadAction, downloadAction, exit_);
        helpMenu.getItems().addAll(checkForUpdates, about);
        pluginsMenu.getItems().addAll(loadPluginItem, pluginMarketplace, allPluginsMenu);
        serverMenu.getItems().addAll(disconnect, connect);
        checkForUpdates.setOnAction(update -> CheckUpdates.checkForUpdates());
        menu.getMenus().addAll(fileMenu, serverMenu, pluginsMenu, helpMenu);
        treeView = new TreeView();
        
        
        // MenuBar Action
        exit_.setOnAction(exitAction -> {
            exit();
        });
    	
    	// Loads plug-ins
    	
    	File pluginFiles = new File("blinkPlugins/"); 
    	File[] pluginFilesList = pluginFiles.listFiles();
    	for(File pluginFile: pluginFilesList) {
    	    	String[] pluginInfo = Operations.LoadPlugin(pluginFile.getAbsolutePath().toString(), true);
            	MenuItem plugin = new MenuItem(pluginInfo[0] + " | " + pluginInfo[1]);
                File pluginJar = new File(pluginFile.getAbsolutePath().toString()); // gets plugin.jar file

            	allPluginsMenu.getItems().add(plugin);
    	}
    	
        loadPluginItem.setOnAction(loadPluginFile -> {
        	// Opens File Dialog 
        	FileChooser fileChooser = new FileChooser();
        	File pluginFile = fileChooser.showOpenDialog(primaryStage);
        	String[] pluginInfo = Operations.LoadPlugin(pluginFile.getAbsolutePath().toString(), true);
        	MenuItem plugin = new MenuItem(pluginInfo[0] + " | " + pluginInfo[1]);
        	allPluginsMenu.getItems().add(plugin);
        });
        
        // Input bar
        connectBtn = new Button();
        hostEdit = new TextField();
        hostEdit.setPromptText("Host");
        usernameEdit = new TextField();
        usernameEdit.setPromptText("Username");
        passwordEdit = new PasswordField();
        passwordEdit.setPromptText("Password");
        portEdit = new TextField();
        portEdit.setPromptText("Port");
        connectBtn.setText("Connect");
        connectBtn.setOnAction(actionEvent -> {
            try {
                getCredential(actionEvent);
            } catch (JSchException | IOException e) {
                e.printStackTrace();
            }
        });

        // Layouts
        hbox = new HBox();
        hbox.setStyle("-fx-background-color: #4b7bec");
        hbox.setSpacing(10);
        HBox.setMargin(connectBtn, new Insets(20, 20, 20, 20));
        HBox.setMargin(connectionTypes, new Insets(20, 20, 20, 20));
        HBox.setMargin(hostEdit, new Insets(20, 20, 20, 20));
        HBox.setMargin(usernameEdit, new Insets(20, 20, 20, 20));
        HBox.setMargin(passwordEdit, new Insets(20, 20, 20, 20));
        HBox.setMargin(portEdit, new Insets(20, 20, 20, 20));
        ObservableList<javafx.scene.Node> list = hbox.getChildren();
        list.addAll(hostEdit, usernameEdit, passwordEdit, portEdit, connectionTypes, connectBtn);
        list1 = vbox.getChildren();
        try {
            // Shows No Connections if isConnected is false
            if (!isConnected) {
                System.out.println(System.getProperty("user.dir"));
                Image noConnectionImg = new Image(new FileInputStream("res/server.png"));
                ImageView img = new ImageView(noConnectionImg);
                noConnectionVBox = new VBox(img, new Label("Connect to a server first!"));
                noConnectionVBox.setAlignment(Pos.CENTER);
                list1.setAll(menu, hbox, noConnectionVBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(vbox, 1000, 600);
        vbox.setVgrow(treeView, Priority.ALWAYS);

        
        // Context Menu
        MenuItem uploadItem = new MenuItem("Upload");
        MenuItem downloadItem = new MenuItem("Download");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem cutItem = new MenuItem("Cut");
        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setDisable(true);
        treeView.setShowRoot(false);
        treeViewContext.getItems().addAll(uploadItem, downloadItem, copyItem, cutItem, pasteItem, deleteItem);
        treeView.setOnContextMenuRequested(event -> treeViewContext.show(treeView, event.getScreenX(), event.getScreenY()));
        treeView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                treeViewContext.hide();
            }
        });

        // This is where all the action events of the Context menu are set.
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observableValue, Object o, Object t1) {
                TreeItem<String> selectedItem = (TreeItem) treeView.getSelectionModel().getSelectedItem();

                StringBuilder pathBuilder = new StringBuilder();
                for (TreeItem<String> item = (TreeItem) treeView.getSelectionModel().getSelectedItem(); item != null; item = item.getParent()) {
                    pathBuilder.insert(0, item.getValue());
                    pathBuilder.insert(0, "/");
                }
                String path = pathBuilder.toString();
                System.out.println(path);
                String new_path = path.replace("/Server content", "");
                System.out.println(new_path); // TODO: find a better solution to get rid of the root item.

                // Download Action. (Update 1)
                downloadItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        DirectoryChooser directoryChooser = new DirectoryChooser();
                        File selectedDirectory = directoryChooser.showDialog(primaryStage);
                        try {
                            Transfers.download(new_path, selectedDirectory.getAbsolutePath());
                        } catch (SftpException e) {
                            e.printStackTrace();
                        }

                    }
                });

                // This checks if the following selection is a directory
                try {
                    if (Sftp.channelSftp.stat(new_path).isDir()) {
                        downloadItem.setDisable(true);
                    } else {
                        downloadItem.setDisable(false);
                    }
                } catch (SftpException e) {
                    e.printStackTrace();
                }

                // Copy Action
                copyItem.setOnAction(eh -> {
                    String filename = new_path.split("/")[new_path.split("/").length - 1];
                    Transfers.copy(new_path, filename);
                    pasteItem.setDisable(false);
                });

                // Cut Action
                cutItem.setOnAction(eh -> {
                    String filename = new_path.split("/")[new_path.split("/").length - 1];
                    Transfers.cut(new_path, filename);
                    pasteItem.setDisable(false);
                });

                // Paste Action 
                pasteItem.setOnAction(eh -> {
                    String filename = Transfers.paste(new_path);
                    pasteItem.setDisable(true);
                    TreeItem item = (TreeItem) treeView.getSelectionModel().getSelectedItem();
                    try {
                        Populate.populateSingle(filename, new_path, item);
                    } catch (SftpException | FileNotFoundException e) {
                        e.printStackTrace();
                    }

                });

                // Delete Action
                deleteItem.setOnAction(eh -> {
                    if (Connections.Transfers.delete(new_path, new_path.split("/")[new_path.split("/").length - 1])) {
                        TreeItem item = (TreeItem) treeView.getSelectionModel().getSelectedItem();
                        item.getParent().getChildren().remove(item);
                    }

                    // Upload Action.
                    uploadItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            Stage uploadStage = new Stage();
                            StackPane Pane = new StackPane();
                            FileChooser fileChooser = new FileChooser();
                            Button submit = new Button("Ok");
                            Button select_file_button = new Button("Select File");
                            submit.setOnAction(event -> Alerts.Warning("No files have been selected!", "Select files first!", "You have to select the files you want to upload first and then click 'ok'."));
                            select_file_button.setOnAction(e -> {
                                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                                submit.setOnAction(event -> {
                                    if (selectedFile != null) {
                                        try {
                                            Transfers.upload(new_path + selectedFile.getName(), selectedFile.getAbsolutePath());
                                        } catch (SftpException c) {
                                            c.printStackTrace();
                                        }

                                        uploadStage.hide();
                                    } else {
                                        Alerts.Warning("No files have been selected!", "Select files first!", "You have to select the directory you want to upload your file to.");
                                    }

                                });
                            });
                            Button cancel = new Button("Cancel");
                            cancel.setOnAction(event_ -> uploadStage.hide());
                            VBox mainVBox = new VBox();
                            HBox hBox = new HBox();
                            VBox vBox = new VBox();
                            Label uploadLabel = new Label("Select files to upload..");
                            hBox.setSpacing(10);
                            mainVBox.setSpacing(20);
                            ObservableList<javafx.scene.Node> list_mainVBox = mainVBox.getChildren();
                            ObservableList list_hBox = hBox.getChildren();
                            ObservableList<javafx.scene.Node> list_ = vBox.getChildren();
                            list_.addAll(select_file_button);
                            list_mainVBox.addAll(uploadLabel, vBox, hBox);
                            list_hBox.addAll(submit, cancel);
                            Pane.getChildren().addAll(mainVBox);
                            Pane.setPrefSize(160, 100);
                            StackPane.setAlignment(mainVBox, Pos.CENTER_RIGHT);
                            uploadStage.setScene(new Scene(Pane));
                            uploadStage.setResizable(false);
                            uploadStage.initModality(Modality.WINDOW_MODAL);
                            uploadStage.initOwner(primaryStage);
                            uploadStage.show();
                        }
                    });

                });
            }
        });

        // Stage
        primaryStage.setTitle(String.format("PolyDev | %s %d", Info.name, Info.version));
        primaryStage.setHeight(670);
        primaryStage.setWidth(1050);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void getSelectedItem(ReadOnlyObjectProperty selected) {
        Object value = selected.getValue();
    }

    private void getCredential(ActionEvent event) throws JSchException, IOException {
        String host = hostEdit.getText();
        String username = usernameEdit.getText();
        String password = passwordEdit.getText();
        String port = portEdit.getText();
        if ((username.isEmpty()) || (host.isEmpty()) || (password.isEmpty()) || (port.isEmpty())) {
            Alert empty = new Alert(Alert.AlertType.INFORMATION);
            empty.setTitle("Empty text fields!");
            empty.setContentText("Please fill out all of the required text fields.");
            empty.setHeaderText("Empty text fields!");
            empty.show();
        } else {
            ArrayList< String> credentials = new ArrayList< String>();
            credentials.add(host);
            credentials.add(username);
            credentials.add(password);
            credentials.add(port);

            if (!isConnected) {
                if (connectionTypes.getSelectionModel().getSelectedItem() == "SFTP") {
                    try {
                        TreeItem root = new TreeItem("Server content");
                        treeView.setRoot(root);
                        Connections.Sftp.connectToServer(credentials, "SFTP");
                        Populate.populateTree("/", root);
                    } catch (SftpException e) {
                        e.printStackTrace();
                    }

                    list1.setAll(menu, hbox, treeView);
                    connectBtn.setText("Disconnect");
                    isConnected = true;
                } else {
                    Alerts.Warning("NOT SUPPORTED", "FTP IS NOT SUPPORTED FOR NOW", "Note: FTP Connections will be supported in Update 2");
                }
            } else {
                connectBtn.setText("Connect");
                list1.setAll(menu, hbox, noConnectionVBox);
                isConnected = false;
                connectBtn.setOnAction(eh -> {
                    try {
                        getCredential(eh);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
