package org.lejos.nxt.ldt.ant;

import java.io.File;

import org.eclipse.ant.core.IAntPropertyValueProvider;
import org.lejos.nxt.ldt.util.LeJOSNXJException;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class NXJHomeProvider implements IAntPropertyValueProvider {

	public String getAntPropertyValue(String antPropertyName)
	{
		File nxjHome;
		try {
			nxjHome = LeJOSNXJUtil.getNXJHome();
		} catch (LeJOSNXJException e) {
			LeJOSNXJUtil.log(e);
			return null;
		}
		
		return nxjHome.getAbsolutePath();
	}

}
