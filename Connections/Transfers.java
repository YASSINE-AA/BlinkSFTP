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
package Connections;

import com.jcraft.jsch.SftpException;
import AlertSystem.Alerts;
import java.io.File;

/**
 *
 * @author PolyDev
 */

// TODO: Add Support for other OS (Other than Linux).
public class Transfers {

    public static boolean cutOperation = false;
    public static String source;
    public static File file = new File("temp");
    public static String copyCutSource;

    public static void download(String source, String destination) throws SftpException {
        //INFO: This method handles downloading files. (Update 1)
        if (source != null) {
            Sftp.channelSftp.get(source, destination);
        }
    }

    public static void upload(String source, String destination) throws SftpException {
        //INFO: This method handles uploading files. (Update 1)
        if (destination != null) {
            try {
                Sftp.channelSftp.put(destination, source);
            } catch (SftpException e) {
                Alerts.Danger("Couldn't upload file!", "The file couldn't be uploaded to the server", "Perhaps a permissions issue?\nCheck if you have RW Permissions on your server.");
            }

        }
    }

    public static boolean delete(String source, String fileName) {
        //INFO: This method handles deleting files. (Update 1)

        if (source != null) {
            try {
                if (Sftp.channelSftp.stat(source).isDir()) {
                    Sftp.channelSftp.rmdir(source); // Removes folder with the rmdir method.
                    return true;
                } else {
                    Sftp.channelSftp.rm(source); // Removes file with the rm method.
                    return true;
                }
            } catch (SftpException e) {
                Alerts.Danger("Couldn't delete file or folder.", String.format("%s couldn't be removed!", fileName), "Perhaps a permissions issue?\nCheck if you have RW Pemissions on your server.");
            }

        }
        return false;
    }

    public static void copy(String source, String filename) {
        //INFO: This method handles copying files. (Update 1)
        /* How it works: This copies file from the server and it pastes in in a local
        directory temp (GETS DELETED AFTER PASTING) and assigns copyCutSource the local path.
         */
        cutOperation = false;
        boolean isCreated = false;
        source = source;
        if (!file.isDirectory()) {
            isCreated = file.mkdir();
        } else {
            isCreated = true;
        }
        try {
            if (isCreated) {
                Sftp.channelSftp.get(source, "temp/");
                copyCutSource = "temp/" + filename;
            }
        } catch (SftpException e) {
            Alerts.Danger("Error!", "Error copying files!", "Blink has encountered an error\ntrying to copy the files!");
            copyCutSource = "";
        }
    }

    public static void cut(String source, String filename) {
        //INFO: This method handles cutting files. (Update 1)
        // This going to use the delete method.
        copy(source, filename); // copies file to temp folder.
        cutOperation = true;
    }

    public static String paste(String destination) {
        //INFO: This method handles pasting files. (Update 1)
        try {
            Sftp.channelSftp.put(copyCutSource, destination);
            file.delete(); // deletes the local temp folder after pasting.
            if (cutOperation) {
                if (Sftp.channelSftp.stat(source).isDir()) {
                    Sftp.channelSftp.rmdir(source);

                } else {
                    Sftp.channelSftp.rm(source);
                }
                cutOperation = false;
            }

        } catch (SftpException e) {
            Alerts.Danger("Couldn't paste file", "Couldn't paste file.", "Make sure your server is RW.");
        }
        return copyCutSource.split("/")[copyCutSource.split("/").length - 1];
    }
}
