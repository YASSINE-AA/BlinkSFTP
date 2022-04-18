package Plugins;

// Plug-in developers need to implement this interface.
import javafx.scene.control.*;


public interface plugins {
	public String[] info(); // the developer has to retrun an array that contains the plugin name, description and version.
	public void run(TreeView fileList, Menu menu, Menu contextMenu); // This allows the developer to access the MenuBar, file List and ContextMenu.
}
