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

package Plugins;

import java.io.IOException;
import AlertSystem.Alerts;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import UserInterface.UI;

/*
 * @author overflow
 * @version 1.0
*/

public class Operations {
		
	public static void LoadPlugin(String pluginJarPath, boolean activate) {
		File pathToJar = new File(pluginJarPath);
	
		if(activate) {
			// Activate plug-in (run it).
			File authorizedJarFile = new File("authorized.jar");
			ClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { authorizedJarFile.toURL() });
			plugins authorizedPlugin = (plugins) authorizedLoader.loadClass("plugins.authorized.Authorized").newInstance();
			authorizedPlugin.run();
		} else {
			// Add it to the menu but don't run it.
		
		}
		
	}
 
}
