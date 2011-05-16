package lejos.utest.util;

/**
 * Unit Test utility for "classes" project.
 *
 * @author Dan Rollo
 *         Date: Aug 22, 2010
 *         Time: 11:38:50 PM
 */
public class UnitTestUtilClasses extends UnitTestUtil {

    static final String PROJECT_ROOT_DIR_NAME = "classes";
    /** {@inheritDoc} */
    @Override
    protected String getProjectDirName() { return PROJECT_ROOT_DIR_NAME; }


    private static final String[] FILENAMES_IN_ROOT_DIR = new String[] {
                    ".classpath",
                    ".project",
                    "build.properties",
                    "build.xml",
                    "TODO.txt",
    };
    /** {@inheritDoc} */
    @Override
    protected String[] getFileNamesInProjectDir() { return FILENAMES_IN_ROOT_DIR; }
}
