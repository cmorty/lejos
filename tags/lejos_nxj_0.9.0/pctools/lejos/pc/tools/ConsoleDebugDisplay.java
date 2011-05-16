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
    DebugData debugData;
    ConsoleViewerUI viewer;
    
    public ConsoleDebugDisplay(ConsoleViewerUI view)
    {
    	this(view, (DebugData)null);
    }    

    public ConsoleDebugDisplay(ConsoleViewerUI view, String debugFile) throws IOException
    {
    	this(view, loadDebugData(debugFile));
    }

    public ConsoleDebugDisplay(ConsoleViewerUI view, DebugData debugData)
    {
        this.viewer = view;
        this.debugData = debugData;
        
        if (debugData != null)
            this.viewer.append("Debug attached\n");
    }
    
    static DebugData loadDebugData(String name) throws IOException
    {
        if (name == null)
        	return null;
        
        return DebugData.load(new File(name));
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
                viewer.append(" at: " + debugData.getMethodClass(methodNo) + "." + debugData.getMethodName(methodNo) + "(" + debugData.getMethodFilename(methodNo) + ":" + debugData.getLineNumber(methodNo, pc) + ")\n");
            }
        }
    }
}
