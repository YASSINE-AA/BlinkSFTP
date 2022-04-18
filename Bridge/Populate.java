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
package Bridge;

import Connections.Sftp;
import static Connections.Sftp.channelSftp;
import com.jcraft.jsch.ChannelSftp;
import java.util.ArrayList;
import javafx.scene.control.*;
import javafx.scene.image.*;
import java.util.Vector;
import com.jcraft.jsch.SftpException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author PolyDev
 */
// This class populates the TreeView in the UserInterface.ui class
// TODO: make images match extensions (Update 2)
public class Populate {

    public static ArrayList < TreeItem > treeItems = new ArrayList < > ();
    public static TreeItem < ? > file;
    public static Image folderImg;
    public static Image fileImg;
    public static String fileExt = "res/file.png";
    public static String[][] fileExtList = {
        {
            "png", 
            "jpg", 
            "jpeg", 
            "gif", 
            "svg"
        }, // Image file extensions.
        {
            "exe",
            "deb",
            "dmg",
            "msi"
        }, // Executable extensions. 
        {
            "doc",
            "docx",
            "txt",
            "pdf"
        } // Document file extensions.
    };


    @SuppressWarnings("unchecked")
    public static void populateTree(String remotePath, TreeItem < String > root) throws SftpException, FileNotFoundException {
        //TODO: change "/" for remote file.separator
        Vector < ChannelSftp.LsEntry > list = channelSftp.ls(remotePath); // List source directory structure.
        for (ChannelSftp.LsEntry oListItem: list) { // Iterate objects in the list to get file/folder names.       
            TreeItem < String > node = null;
            if (!oListItem.getAttrs().isDir()) { // If it is a file (not a directory).
                String[] file_split = oListItem.getFilename().split("\\.");
                if (file_split[file_split.length - 1].equals(fileExtList[0][0])) {
                    fileExt = "res/images/png.png";
                } else if (file_split[file_split.length - 1].equals(fileExtList[0][1]) || file_split[file_split.length - 1].equals(fileExtList[0][2])) {
                    fileExt = "res/images/jpg.png";
                } else if (file_split[file_split.length - 1].equals(fileExtList[2][2])) // .txt file.
                {
                    fileExt = "res/documents/text.png";
                } else if (file_split[file_split.length - 1].equals(fileExtList[0][3])) // .gif file.
                {
                    fileExt = "res/images/gif.png";
                } else if (file_split[file_split.length - 1].equals(fileExtList[0][4])) // .svg file.
                {
                    fileExt = "res/images/svg.png";
                } else // Uncommon/Unknown extension. 
                {
                    fileExt = "res/file.png";
                }
                node = new TreeItem < String > (oListItem.getFilename(), new ImageView(new Image(new FileInputStream(fileExt))));
                root.getChildren().add(node); // add as a child node
            } else {
                if (!".".equals(oListItem.getFilename()) && !"..".equals(oListItem.getFilename())) {
                    fileExt = "res/folder.png";
                    node = new TreeItem < String > (oListItem.getFilename(), new ImageView(fileExt));
                    root.getChildren().add(node); // add as a child node
                    populateTree(remotePath + "/" + oListItem.getFilename(), node); // call again for the sub-directory
                }
            }
        }
    }


    public static void populateSingle(String treeItemName, String source, TreeItem selectedItem) throws SftpException, FileNotFoundException {
        folderImg = new Image(new FileInputStream("res/folder.png"));
        fileImg = new Image(new FileInputStream("res/file.png"));
        if (Sftp.channelSftp.stat(source).isDir()) {
            selectedItem.getParent().getChildren().add(new TreeItem < String > (treeItemName, new ImageView(folderImg)));
        } else {
            selectedItem.getParent().getChildren().add(new TreeItem < String > (treeItemName, new ImageView(fileImg)));
        }
    }
}