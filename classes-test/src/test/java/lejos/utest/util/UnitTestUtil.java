package lejos.utest.util;

import java.io.IOException;

/**
 * Utility to find project root dir, independent of IDE/current dir, etc.
 *
 * @author Dan Rollo
 *         Date: Jun 24, 2010
 *         Time: 8:35:38 PM
 */
public abstract class UnitTestUtil {

    /** @return Directory name of project root directory. */
    protected abstract String getProjectDirName();
    
    /** @return File names we expect to exist in the root directory of this project. */
    protected abstract String[] getFileNamesInProjectDir();

    
    private static UnitTestUtil instance;
    
    public static void setProject(final UnitTestUtil utilSubclassInstance) {
        instance = utilSubclassInstance;
    }

    static final String MSG_NULL_INSTANCE
            = "Call UnitTestUtil.setProject(<UnitTestUtilSubclass>) before calling other methods.";
    /**
     * Package visible for unit test restore operations.
     * @return the instance with this project's Name and root Files.
     */
    static UnitTestUtil getInstance() {
        if (instance == null) {
            throw new IllegalStateException(MSG_NULL_INSTANCE);
        }
        return instance;
    }

    public static String getProjectDirectoryName() { return getInstance().getProjectDirName(); }
    public static String[] getFileNamesInProjectDirectory() { return getInstance().getFileNamesInProjectDir(); }

    private static String arrayToString(final String[] input) {
        final StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < input.length; i++) {
            sb.append(input[i]);
            if (i < (input.length - 1)) {
                sb.append(",");
            }
        }

        sb.append("]");
        return sb.toString();
    }
    /**
     * @return project root dir.
     * @throws IOException if an IO Error occurs
     * @throws IllegalStateException if root dir not found.
     */
    /*
    public static File getProjectRootDir() throws IOException {
        final File currentDir = new File(".").getCanonicalFile();
        File dir = currentDir;
        
        while (dir != null && !dirIsRoot(dir)) {
            dir = dir.getParentFile();
        }

        if (dir == null) {
            // see if project dir is a sub-dir of current (eg: we are in the top level 'lejos' dir).
            final File subdir = new File(currentDir, getProjectDirectoryName()).getCanonicalFile();
            if (dirIsRoot(subdir)) {
                dir = subdir;
            }
        }

        if (dir == null) {
            throw new IllegalStateException("Root dir for project name: [" + getProjectDirectoryName()
                    + "], containing the files: " + arrayToString(getFileNamesInProjectDirectory())
                    + " not found. Current directory: " + currentDir.getName());
        }
        return dir;
    }

    private static boolean dirIsRoot(final File dir) {
        return getProjectDirectoryName().equals(dir.getName())
                && dirContains(dir, getFileNamesInProjectDirectory());
    }

    static interface MyFileFilter {
        boolean accept(File pathname);
    }

    private static final MyFileFilter FILES_ONLY = new MyFileFilter() {
        public boolean accept(final File pathname) {
            return pathname.isFile();
        }
    };


    private static File[] doFileFilter(final File dir, final MyFileFilter filter) {
        final File[] allFiles = dir.listFiles();
        final ArrayList<File> v = new ArrayList<File>();
        for (int i = 0; i < allFiles.length; i++) {
            final File f = allFiles[i];
            if (filter == null || filter.accept(f)) {
                v.add(f);
            }
        }
        return v.toArray(new File[v.size()]);
    }

    private static boolean dirContains(final File dir, final String[] requiredFilenames) {
        final File[] files = doFileFilter(dir, FILES_ONLY);
        final List<String> actualFilenames = new ArrayList<String>();
        for (final File file : files) {
            actualFilenames.add(file.getName());
        }

        for (final String requiredFilename : requiredFilenames) {
            if (!actualFilenames.contains(requiredFilename)) {
                return false;
            }
        }
        return true;
    }
    //*/

}
