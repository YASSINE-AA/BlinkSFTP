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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import AlertSystem.Alerts;

/**
 *
 * @author PolyDev
 */
public class Sftp {

    private static JSch jsch = new JSch();
    private static Session jschsession;
    public static ChannelSftp channelSftp;
    private static List<String> directories = new ArrayList<>();
    private static List<String> files = new ArrayList<>();
    private static Vector<ChannelSftp.LsEntry> fileList;



    public static ChannelSftp connectToServer(ArrayList<String> cred, String connectionType) {
        // This method handles all connections to the server and returns an array
        try {
            if (connectionType.equals("SFTP")) {
                // SFTP Connection
                jschsession = jsch.getSession(cred.get(1), cred.get(0), Integer.parseInt(cred.get(3)));
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                jschsession.setConfig(config);
                jschsession.setPassword(cred.get(2));
                jschsession.connect(3000);
                Channel channel = jschsession.openChannel("sftp");
                channel.connect();
                channelSftp = (ChannelSftp) channel;
                fileList = channelSftp.ls("/");

                // TODO: Add TreeView population
            } else if (connectionType.equals("FTP")) {
                System.out.println("Connecting to FTP Server..");
                Ftp.connect(cred);
            }

        } catch (JSchException | SftpException e) {
            Alerts.Danger("Oops", String.format("There was an error trying to connect to %s server!", connectionType), "Check your Internet and/or If your server is up and running!");
        }
        return null;
    }

    public static List<String> getDirectoryContent(String path, boolean isRecursive, boolean isDirectory, List<String> list_) throws SftpException {
        // This method returns all server files in vector! 
        if (!isRecursive) {
            Vector<ChannelSftp.LsEntry> entries = channelSftp.ls(path);
            if (isDirectory) {
                for (ChannelSftp.LsEntry entry : entries) {
                    if (entry.getAttrs().isDir()) {
                        directories.add(entry.getFilename());
                    }
                }
                return directories;
            } else {
                // if it's files we need.
                for (ChannelSftp.LsEntry entry : entries) {
                    if (!entry.getAttrs().isDir()) {
                        files.add(entry.getFilename());
                    }
                }
                return files;
            }

        } else {
            if (isDirectory) {
                Vector<ChannelSftp.LsEntry> files = channelSftp.ls(path);
                List<String> list = new ArrayList<>();
                for (ChannelSftp.LsEntry entry : files) {
                    if (!entry.getAttrs().isDir()) {
                        list.add(path + "/" + entry.getFilename());
                    } else {
                        if (!entry.getFilename().equals(".") && !entry.getFilename().equals("..")) {
                            getDirectoryContent(path + "/" + entry.getFilename(), true, true, list);
                        }
                    }
                }
                return list;
            }
        }
        return null;
    }

    public void disconnect() {
        channelSftp.disconnect();
        jschsession.disconnect();
    }
}
