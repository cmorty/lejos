package lejos.internal.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
	public static final String CONFIG_BTCACHE = "nxj.cache";
	public static final String CONFIG_NAVPANEL = "nav.props";
	
	public static File getPropFile(String name) {
		String userHome = System.getProperty("user.home");
		if (userHome == null)
			return null;
		
		return new File(userHome+File.separator+".config"+File.separator+"leJOS NXJ", name);
	}
	
	public static boolean loadPropFile(String name, Properties dst) throws IOException {
		File propsFile = getPropFile(name);
		if (propsFile == null || !propsFile.exists())
			return false;
		
		FileInputStream fis =new FileInputStream(propsFile);
		try {
			dst.load(fis);
		} finally {
			fis.close();
		}
		return true;
	}

	public static boolean savePropFile(String name, Properties src) throws IOException {
		File propsFile = getPropFile(name);
		if (propsFile == null)
			return false;
		
		propsFile.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(propsFile);
		try {
			src.store(out, "Automatic save");					
		} finally {
			out.close();
		}
		return true;
	}
}
