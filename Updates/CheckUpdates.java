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
package Updates;

import AlertSystem.Alerts;
import java.net.*;
import java.io.*;
import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import Product.Info;

/**
 *
 * @author PolyDev
 */
public class CheckUpdates {

    public static int clientVersion;
    public static int currentVersion;
    public static String updatePath;

    public static void updateClient() {
        // This method downloads the latest executable file.
    	System.out.println(updatePath);
    }
  
    // This gets the newest version number.
    public static boolean getClientVersion() {
    	// This method gets the current newest version of Blink Client 
    	// It fetches the data from the JSON file mentioned in the URL bellow.
    	
    		 try {
    	        	String url ="https://pastebin.com/raw/qXJrNmx6"; // This URL contains the JSON update file (update.json);
    	            URL obj = new URL(url);
    	            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
    	            // optional default is GET
    	            con.setRequestMethod("GET");
    	            //add request header
    	            con.setRequestProperty("User-Agent", "Mozilla/5.0");
    	            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    	            con.setRequestProperty("Accept", "text/html");
    	            int responseCode = con.getResponseCode();
    	            System.out.println("\nSending 'GET' request to URL : " + url);
    	            System.out.println("Response Code : " + responseCode);
    	            BufferedReader in = new BufferedReader(
    	                    new InputStreamReader(con.getInputStream()));
    	            String inputLine;
    	            StringBuffer response = new StringBuffer();
    	            while ((inputLine = in.readLine()) != null) {
    	                response.append(inputLine);
    	            }
    	            JSONObject jobj = new JSONObject(response.toString());
    	            currentVersion = jobj.getInt("version_number"); // This gives us the new version.
    	            updatePath = "https://polydev.me/" + jobj.getString("path"); // This gives us the update files URL.
    	        	return isUpdatable();
    	        } catch (Exception e) {
    	            Alerts.Danger("Couldn't establish connection to the server.", "Couldn't connect to server.", "Check if you're connected to the Internet.\nFor now, you can carry on using blink normally.");
    	        }
			return false;
    
    }

    public static int getCurrentClientVersion() {
        return Info.version;
    }
  
    public static boolean isUpdatable() {
    	// This method returns a boolean, if true then the client would update.
    	return currentVersion>clientVersion;
    }

    public static void checkForUpdates() {
        if(getClientVersion()) {
        	Alerts.Informational("WooHoo!", "New version of blink is ready for download!", String.format("Version %d is available for download!", currentVersion), "Download Update", "update");
        }
        
    }
}
