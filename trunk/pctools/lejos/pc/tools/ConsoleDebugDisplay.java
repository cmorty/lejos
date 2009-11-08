/**
 * Provide default console debug output processing.
 */
package lejos.pc.tools;
import js.tinyvm.DebugData;
import java.io.*;

/**
 *
 * @author andy
 */
public class ConsoleDebugDisplay implements ConsoleDebug
{
    DebugData debugData = null;
    ConsoleViewerUI viewer;

    public ConsoleDebugDisplay(ConsoleViewerUI view, String debugFile)
    {
        viewer = view;
        viewer.append("Debug attached\n");
        loadDebugData(debugFile);
    }

    void loadDebugData(String name)
    {
        if (name == null || name.length() == 0) return;
        FileInputStream fis = null;
        ObjectInputStream in = null;
        DebugData ret = null;
        try {
            fis = new FileInputStream(name);
            in = new ObjectInputStream(fis);
            ret = (DebugData) in.readObject();
            in.close();
            fis.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        debugData = ret;
    }

    public void exception(int classNo, String msg, int[] stackTrace)
    {
        if (debugData == null)
        {
            viewer.append("Exception: " + classNo + (msg.length() > 0 ? ":" + msg : "") + "\n");
            for(int frame : stackTrace)
            {
                int methodNo = (frame >> 16) & 0xffff;
                int pc = frame & 0xffff;
                viewer.append(" at: " + methodNo + "(" + pc + ")\n");
            }
        }
        else
        {
            viewer.append("Exception: " + debugData.getClassName(classNo) + (msg.length() > 0 ? ":" + msg : "") + "\n");
            for(int frame : stackTrace)
            {
                int methodNo = (frame >> 16) & 0xffff;
                int pc = frame & 0xffff;
                viewer.append(" at: " + debugData.getMethodClass(methodNo) + "." + debugData.getMethodName(methodNo) + "(" + debugData.getMethodFile(methodNo) + ":" + debugData.getLineNumber(methodNo, pc) + ")\n");
            }
        }
    }
}
