package lejos.pc.tools;

import javax.swing.UIManager;

import lejos.pc.charting.ChartingLogger;

public class NXJChartingLogger
{
	public static int start(String[] args) throws Exception
	{
		// just throw exception and abort? or continue gracefully? 
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        new ChartingLogger();
        return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ToolStarter.startSwingTool(NXJChartingLogger.class, args);
	}

}
