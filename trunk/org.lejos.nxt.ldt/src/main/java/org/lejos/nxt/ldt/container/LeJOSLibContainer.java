package org.lejos.nxt.ldt.container;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.lejos.nxt.ldt.util.LeJOSNXJException;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;


public class LeJOSLibContainer implements IClasspathContainer {
	public static final String ID = "org.lejos.nxt.ldt.LEJOS_LIBRARY_CONTAINER";
    
    private static final int DEFAULT_OPTION = 0;
    
    private static final String[][] options = {
    		{ LeJOSNXJUtil.LIBSUBDIR_NXT, "NXT Runtime" },
    		{ LeJOSNXJUtil.LIBSUBDIR_PC, "PC Libraries" },
    	};
    
    static int getOptionCount()
    {
    	return options.length;
    }
    
    static String getOptionKey(int i)
    {
    	return options[i][0];
    }
    
    static String getOptionName(int i)
    {
    	return options[i][1];
    }
    
    static int getOptionIndex(String s)
    {
    	for (int i=0; i<options.length; i++)
    		if (options[i][0].equals(s))
    			return i;
    	
    	return -1;
    }
    
    static int getOptionFromPath(IPath p)
    {
        if(p != null && p.segmentCount() > 1 ) {
            int i= LeJOSLibContainer.getOptionIndex(p.segment(1));
            if (i >= 0)
            	return i;
        }
        
        return DEFAULT_OPTION;
    }
    
    
    // path string that uniquiely identifies this container instance
    private final IPath path;
    private final String name;
    private final IClasspathEntry[] cp;
  
    public LeJOSLibContainer(IPath path) throws LeJOSNXJException {
        int option = getOptionFromPath(path);
        
        this.path = path;
        this.name = "LeJOS "+getOptionName(option);
        this.cp = createClasspath(option);
    }
    
    private IClasspathEntry[] createClasspath(int option) throws LeJOSNXJException {
        ArrayList<File> entryList = new ArrayList<File>();
        
    	File nxjHome = LeJOSNXJUtil.getNXJHome();
    	String subdir = getOptionKey(option);
    	LeJOSNXJUtil.buildClasspath(nxjHome, subdir, entryList);
    	
        int len = entryList.size();
        IClasspathEntry[] entryArray = new IClasspathEntry[entryList.size()];
        for (int i=0; i<len; i++)
        {
        	File lib = entryList.get(i);
        	File src = guessSource(lib);
        	IPath lib2 = Path.fromOSString(lib.getAbsolutePath());
        	IPath src2 = (src == null) ? null : Path.fromOSString(src.getAbsolutePath()); 
        	entryArray[i] = JavaCore.newLibraryEntry(lib2, src2, null);
        }
        return entryArray;
	}

	private File guessSource(File lib) throws LeJOSNXJException {
		File parent = lib.getParentFile();
		File project = new File(LeJOSNXJUtil.getNXJHome(), "projects");
		String basename = lib.getName();
		int i = basename.lastIndexOf('.');
		if (i >= 0)
			basename = basename.substring(0, i);
		
		File[] test = new File[] {
				new File(parent, basename+".zip"),
				new File(parent, basename+"-src.zip"),
				new File(project, basename),
				new File(project, basename+".zip"),
				new File(project, basename+"-src.zip"),
			};
		
		for (File f : test)
			if (f.exists())
				return f;
		
		return null;
	}

	public IClasspathEntry[] getClasspathEntries() {
    	return cp;
    }
    
    public String getDescription() {
        return name;
    }

    public int getKind() {
        return IClasspathContainer.K_APPLICATION;
    }    
    
    public IPath getPath() {
        return path;
    }
    
}
