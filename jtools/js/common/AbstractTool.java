package js.common;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract tool.
 */
public class AbstractTool
{
   //private ToolProgressMonitor _progress;
	protected Collection<ToolProgressMonitor> _monitors;


   /**
    * Constructor.
    * 
    * @param listener tool progress listener
    */
   public AbstractTool ()
   {
	   _monitors = new ArrayList<ToolProgressMonitor>();
   }

   //
   // protected interface
   //

//   /**
//    * Progress listener.
//    */
//   protected ToolProgressMonitor getProgressMonitor ()
//   {
//      assert _progress != null: "Postconditon: result != null";
//      return _progress;
//   }
   
   /**
    * register progress monitor
    */
   public void addProgressMonitor(ToolProgressMonitor monitor) {
	   _monitors.add(monitor);
   }

   /**
    * deregister progress monitor
    */
   public void removeProgressMonitor(ToolProgressMonitor monitor) {
	   _monitors.remove(monitor);
   }

}