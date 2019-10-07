package tfcvesselalloycalculator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class ResourceHelper {

	public URL getResource(String file) throws MalformedURLException {
		File basePath = new File("." + File.separator + file);
		if(basePath.exists()) {
			return basePath.toURI().toURL();
		} else {
			return getClass().getClassLoader().getResource(file);
		}
	}
}
