package lejos.pc.charting;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.BadLocationException;


/** The main GUI window for NXT Charting Logger.
 * @author Kirk P. Thompson
 */
class LogChartFrame extends JFrame {
    private final String THISCLASS;
   
    private JButton jButtonConnect = new JButton();
    private JPanel UIPanel = new JPanel();
    private JPanel connectionPanel = new JPanel();
    private JLabel jLabel1logfilename = new JLabel();
    private JTextField jTextFieldNXTName = new JTextField();
    
    private JTextArea jTextAreaStatus = new JTextArea();
    private JLabel jLabel5 = new JLabel();
    private JTextField logFileTextField = new JTextField();
    private JScrollPane statusScrollPane = new JScrollPane();
    private JScrollPane dataLogScrollPane = new JScrollPane();

    // make sure all non-GUI vars are below the ones added by Jdev
    private ChartModel customChartPanel = new CustomChartPanel();
//    private ChartModel customChartPanel = null; // TODO instantiate at loggerlistener level new CustomChartPanelScatterPlot();
    
    private final ConnectionProvider connectionManager;
    
    private boolean isNXTConnected = false;
    private File theLogFile; 
    
    private JMenuBar menuBar=new JMenuBar();
    private JMenu menu;
    private JMenuItem menuItem;
    
    private JTabbedPane jTabbedPane1 = new JTabbedPane();
    private JTextArea dataLogTextArea = new JTextArea();
    private SelfLogger loggerHook = new SelfLogger();
    private JTextArea FQPathTextArea = new JTextArea();
    private JButton selectFolderButton = new JButton();
    private ConcurrentLinkedQueue<String> logDataQueue= new ConcurrentLinkedQueue<String>();
    
    
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private Timer updateLogTextAreaTimer;
    private ExtensionGUIManager eGuiManager;
    private TunneledMessageManager tmm ;
    private LoggerProtocolManager lpm;
    
    private TimeSeriesOptionsPanel chartOptionsPanel = null;
    
    /** Default constructor
     */
    public LogChartFrame(ConnectionProvider connectionProvider) {
        try {
            chartOptionsPanel = new TimeSeriesOptionsPanel(customChartPanel); 
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Hooking into System.out..");
        System.setOut(new redirector(System.out));
        System.out.println("creating connectionManager instance");
        this.connectionManager = connectionProvider;
        
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
        
        // manage Range axis label fields state
        chartOptionsPanel.manageAxisLabel();
        
        this.eGuiManager = new ExtensionGUIManager(jTabbedPane1);
        this.tmm = new TunneledMessageManager(eGuiManager);
    }


    /** invoked by ChartingLogger on window close to clean up and close connection 
     */
    void closeCurrentConnection(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                jButtonConnect.setText("Connect");
                if (connectionManager!=null) connectionManager.closeConnection();
                isNXTConnected = false;
            }
        });
    }


    /**This class is used to provide listener callbacks from LoggerProtocolManager.
     */
    private class SelfLogger implements LoggerListener{
    	private static final String SERIES_DELIM = "!";
    	private long lastUpdate=0;
        
        public void logCommentReceived(int timestamp, String comment) {
            String theComment = String.format("%1$-1d\t%2$s\n", new Integer(timestamp), comment);
            LogChartFrame.this.logDataQueue.add(theComment);
            customChartPanel.addCommentMarker(timestamp, comment);
        }

        /** used to track series defs
         */
        private class SeriesDef{
            String name;
            boolean chartable;
            int axisID;
        }
        private SeriesDef[] seriesDefs;
        
        private SeriesDef[] parseSeriesDef(String[] logFields) {
            SeriesDef[] sd = new SeriesDef[logFields.length];
            String[] seriesDef;
            // parse the column defs into a struct
            for (int i = 0; i < logFields.length; i++) {
                seriesDef = logFields[i].split(SERIES_DELIM);
                sd[i] = new SeriesDef();
                sd[i].name = seriesDef[0];
                if (seriesDef.length>1) {
                    sd[i].chartable=seriesDef[1].equalsIgnoreCase("y");
                    sd[i].axisID=Integer.valueOf(seriesDef[2]).intValue();
                } else {
                    // coldef structure not correct, use defaults
                    sd[i].chartable=true;
                    sd[i].axisID=1;
                }
                // ensure domain val is always chartable
                if (i==0) sd[i].chartable=true;
            }
            return sd;
        }

        /**Parse chartable datapoints into a double array. Uses previous parsed seriesDefs[] to determine chartable
         * 
         * @param logDataItems The datatype representational "structs" from the logger
         * @return array of representative <code>double</code>
         */
        private double[] parseDataPoints(DataItem[] logDataItems) {
            double[] seriesTempvalues = new double[logDataItems.length];
            int chartableCount=0;
            
            
            for (int i=0;i<logDataItems.length;i++) { 
                if (seriesDefs[i].chartable) {
                    switch (logDataItems[i].datatype) {
                        case DataItem.DT_BOOLEAN: 
                        case DataItem.DT_BYTE: 
                        case DataItem.DT_SHORT: 
                        case DataItem.DT_INTEGER:
                            seriesTempvalues[chartableCount]=((Integer)logDataItems[i].value).doubleValue();
                            break;
                        case DataItem.DT_LONG: 
                            seriesTempvalues[chartableCount]=((Long)logDataItems[i].value).doubleValue();
                            break;
                        case DataItem.DT_FLOAT:
                            seriesTempvalues[chartableCount]=((Float)logDataItems[i].value).doubleValue();
                            break;
                        case DataItem.DT_DOUBLE:
                            seriesTempvalues[chartableCount]=((Double)logDataItems[i].value).doubleValue();
                            break;
                        case DataItem.DT_STRING:
                            chartableCount--;
                        default:
                            System.out.println("Bad datatype!" + logDataItems[i].datatype);
                    }
                    chartableCount++;
                }
            }
            double[] seriesTempvalues2 = new double[chartableCount];
            System.arraycopy(seriesTempvalues, 0, seriesTempvalues2, 0, chartableCount);
            return seriesTempvalues2;
        }
        
        public void logLineAvailable(DataItem[] logDataItems) {
            // tell the chart it has some data
            customChartPanel.addDataPoints(parseDataPoints(logDataItems)); 
            // queue text line for log textarea
            LogChartFrame.this.logDataQueue.add(LoggerProtocolManager.parseLogData(logDataItems));
            
            if (this.lastUpdate==0) this.lastUpdate=System.currentTimeMillis()-1;
            // variable textarea update timer delay based on update rate
            int period = (int)(System.currentTimeMillis()-lastUpdate);
            // magic numbers based on testing for non-linear update delay calc so slow-sampled data gets displayed
            // without delay
            period=(int)(2155.1*Math.exp(-.0024*period));
            if (period<200) period=200;
            LogChartFrame.this.updateLogTextAreaTimer.setDelay(period);
            lastUpdate=System.currentTimeMillis();
        }
        
        public void dataInputStreamEOF() {
            closeCurrentConnection();
            // allows user to use interactive stuff without chart glitch    
            System.out.println("Finalizing chart");  
            customChartPanel.setChartDirty();
            tmm.dataInputStreamEOF();
        }

//        *  The string format/structure of each string field passed by NXTDataLogger is:<br>
//        *  <code>[name]![y or n to indicate if charted]![axis ID 1-4]</code>
//        *  <br>i.e. <pre>"MySeries!y!1"</pre>
        public void logFieldNamesChanged(String[] logFields, int chartType) { 
        	System.out.println("client:logFieldNamesChanged");
            StringBuilder chartLabels = new StringBuilder();
            StringBuilder sb = new StringBuilder();
            
            // parse the array into a struct 
            this.seriesDefs=parseSeriesDef(logFields); 
            
            // Build headers/labels/series names for txt log and chartable for chart
            for (int i=0;i<this.seriesDefs.length;i++){
                sb.append(this.seriesDefs[i].name); 
                if (i<this.seriesDefs.length-1) sb.append("\t");
                if (this.seriesDefs[i].chartable) {
                    chartLabels.append(this.seriesDefs[i].name);
                    chartLabels.append(":");
                    chartLabels.append(this.seriesDefs[i].axisID);
                    chartLabels.append(SERIES_DELIM);
                }
            }
            sb.append("\n");
            LogChartFrame.this.logDataQueue.add(sb.toString());
            
            // spawn a copy if there was data in the chart and the headers changed. This is useful when the
            // appendColumn is used to build the initial columns/headers and there is no data but each
            // append would have triggered logFieldNamesChanged()
            if (customChartPanel.hasData()) {
            	try {
                    customChartPanel.spawnChartCopy();
                } catch (OutOfMemoryError e2) {
                    JOptionPane.showMessageDialog(LogChartFrame.this, "Not enough memory to create chart!", "Houston, we have a problem...",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            LogChartFrame.this.requestFocus();
            
            // set the chartable series headers/labels
            customChartPanel.setSeries(chartLabels.toString().split(SERIES_DELIM), chartType); 
            
            if (theLogFile!=null) {
                if (theLogFile.isFile()) {
                    chartOptionsPanel.setChartTitle(getCanonicalName(theLogFile));
                } else {
                    chartOptionsPanel.setChartTitle("Run " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(new Date()));
                }
            }
            
            // manage Range axis label fields state
            chartOptionsPanel.manageAxisLabel();
        }

		public void tunneledMessageReceived(byte[] message) {
			tmm.processMessage(message);
		}
    }
    
    
    /** enables "forking" of STDOUT
     */
    private class redirector extends PrintStream {
        public redirector(OutputStream out) {
            super(out);            
        }
     
        @Override
        public void println(String x) {
            print(x + "\n");
        }
        
        @Override
        public void write(int b) {
            // do nothing
        }
        
        @Override
        public void print(String x){
            super.print(x);
            toStatus(x);
        }
        
        private synchronized void toStatus(String x){
            final String fx = x;
            SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    try {
                        jTextAreaStatus.getDocument().insertString(jTextAreaStatus.getDocument().getLength(),fx, null);
                    } catch (BadLocationException e) {
                        // TODO Do we really need to output this?
                        System.out.print("BadLocationException:" +e.toString() + "\n");
                    }
                }
            });
        }
    }
    
   
    
    
    private class MenuEventListener implements MenuListener {
        private JMenuItem  getMenuItem(String menuFind, String menuItemFind) {
            MenuElement[] menus =  menuBar.getSubElements();
            JMenu menu1=null;
            for (int i=0;i<menus.length;i++) {
                menu1 = (JMenu)menus[i];
                if (!menu1.getActionCommand().equals(menuFind)) continue;
                MenuElement[] menuItems =  menu1.getPopupMenu().getSubElements();
                JMenuItem menuItem1=null;
                for (int j=0;j<menuItems.length;j++){
                    menuItem1 = (JMenuItem)menuItems[j];
                    if (menuItem1.getActionCommand().equalsIgnoreCase(menuItemFind)) return menuItem1;
                }
            }
            return null;
        }
        
        public void menuSelected(MenuEvent e) {
            JMenu menu1 = (JMenu)e.getSource();
            if (menu1.getActionCommand().equals("VIEW_MENU")) {
                JMenuItem tempMenuItem = getMenuItem(menu1.getActionCommand(), "Chart in New Window");
                if (tempMenuItem==null) {
                    return;
                }
                if (customChartPanel.isEmptyChart()) {
                    tempMenuItem.setEnabled(false);
                } else {
                    tempMenuItem.setEnabled(true);
                }
            }
        }

        public void menuDeselected(MenuEvent e) {
            // empty
        }

        public void menuCanceled(MenuEvent e) {
        	// empty
        }
    }

    // to handle menu events
    private class MenuActionListener implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equalsIgnoreCase("about")) new LicenseDialog(LogChartFrame.this).setVisible(true);
            if (e.getActionCommand().equalsIgnoreCase("generate sample data")) {
                LogChartFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                new Thread(new Runnable(){
                    public void run(){
                        populateSampleData();
                    }
                }
                ).start();
            }
            if (e.getActionCommand().equalsIgnoreCase("chart controls")) new UsageHelpDialog(LogChartFrame.this).setVisible(true);
            if (e.getActionCommand().equalsIgnoreCase("copy chart image")) {
                LogChartFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try { 
                    customChartPanel.copyChart();
                } catch (OutOfMemoryError e2) {
                    JOptionPane.showMessageDialog(LogChartFrame.this, "Not enough memory to copy chart image!", "Houston, we have a problem...",
                        JOptionPane.ERROR_MESSAGE);
                }
                LogChartFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            if (e.getActionCommand().equalsIgnoreCase("Copy Data Log")) {
                LogChartFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                int curPos = dataLogTextArea.getCaretPosition();
                try {
                    dataLogTextArea.selectAll();
                    dataLogTextArea.copy();
                } catch (OutOfMemoryError e2) {
                    JOptionPane.showMessageDialog(LogChartFrame.this, "Not enough memory to copy log data!", "Houston, we have a problem...",
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception e2) {
                    JOptionPane.showMessageDialog(LogChartFrame.this, "Problem copying log data! " + e2.toString(), "Houston, we have a problem...",
                        JOptionPane.ERROR_MESSAGE);
                }
                dataLogTextArea.setCaretPosition(curPos);
                LogChartFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            
            if (e.getActionCommand().equalsIgnoreCase("Expand Chart")) {
                JMenuItem mi = (JMenuItem)e.getSource();
                mi.setText("Restore Chart");
                mi.setMnemonic(KeyEvent.VK_R);
                UIPanel.setVisible(false);
                jTabbedPane1.setVisible(false);
            }
            if (e.getActionCommand().equalsIgnoreCase("Restore Chart")) {
                JMenuItem mi = (JMenuItem)e.getSource();
                mi.setText("Expand Chart");
                mi.setMnemonic(KeyEvent.VK_X);
                UIPanel.setVisible(true);
                jTabbedPane1.setVisible(true);
            }
            if (e.getActionCommand().equalsIgnoreCase("Chart in New Window")) {
                LogChartFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    customChartPanel.spawnChartCopy();
                } catch (OutOfMemoryError e2) {
                    JOptionPane.showMessageDialog(LogChartFrame.this, "Not enough memory to create chart!", "Houston, we have a problem...",
                        JOptionPane.ERROR_MESSAGE);
                }
                LogChartFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            
        }
    }
    
    /** All the setup of components, etc. What's scary is Swing is a "lightweight" GUI framework...
     * @throws Exception
     */
    private void jbInit() throws Exception {
        this.setJMenuBar(menuBar);
        this.setSize(new Dimension(819, 613));
        this.setMinimumSize(new Dimension(819, 613));
        this.setTitle("NXT Charting Logger");
        this.setEnabled(true);
        // enforce minimum window size
        this.addComponentListener(new ComponentAdapter(){
            @Override
			public void componentResized(ComponentEvent e){
                JFrame theFrame =(JFrame)e.getSource();
                Dimension d1=theFrame.getMinimumSize();
                Dimension d2=theFrame.getSize();
                boolean enforce=false;
                if (theFrame.getWidth()<d1.getWidth()) {
                    d2.setSize(d1.getWidth(),d2.getHeight());
                    enforce=true;
                }
                if (theFrame.getHeight()<d1.getHeight()) {
                    d2.setSize(d2.getWidth(),d1.getHeight());
                    enforce=true;
                }
                if (enforce) theFrame.setSize(d2);
            }
        });
        gridBagLayout1.columnWeights = new double[]{0.0, 0.0};
        gridBagLayout1.columnWidths = new int[]{0, 0};

        this.getContentPane().setLayout(gridBagLayout1);
        MenuActionListener menuItemActionListener = new MenuActionListener();
        MenuEventListener menuListener = new MenuEventListener();
        
        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(menu);
        menuItem = new JMenuItem("Copy Chart Image", KeyEvent.VK_I);
        menuItem.addActionListener(menuItemActionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Copy Data Log", KeyEvent.VK_D);
        menuItem.addActionListener(menuItemActionListener);
        menu.add(menuItem);
        
        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.setActionCommand("VIEW_MENU");
        menu.addMenuListener(menuListener); 
        menuBar.add(menu);
        menuItem = new JMenuItem("Expand Chart",KeyEvent.VK_F);
        menuItem.addActionListener(menuItemActionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Chart in New Window",KeyEvent.VK_N);
        menuItem.addActionListener(menuItemActionListener);
        menu.add(menuItem);
        
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menu);
        menuItem = new JMenuItem("Chart controls", KeyEvent.VK_C);
        menuItem.addActionListener(menuItemActionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("Generate sample data", KeyEvent.VK_G);
        menuItem.addActionListener(menuItemActionListener);
        menu.add(menuItem);
        menuItem = new JMenuItem("About",KeyEvent.VK_A);
        menuItem.addActionListener(menuItemActionListener);
        jTabbedPane1.setPreferredSize(new Dimension(621, 199));
        jTabbedPane1.setMinimumSize(new Dimension(621, 199));
        menu.add(menuItem);


        jButtonConnect.setText("Connect");
        jButtonConnect.setBounds(new Rectangle(25, 65, 115, 25));
        jButtonConnect.setToolTipText("Connect/disconnect toggle");
        jButtonConnect.setMnemonic('C');
        jButtonConnect.setSelected(true);
        jButtonConnect.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jButtonConnect_actionPerformed(e);
                    }
                });
        UIPanel.setSize(new Dimension(820, 200));
        UIPanel.setLayout(null);
        UIPanel.setPreferredSize(new Dimension(300, 200));
        UIPanel.setMinimumSize(new Dimension(300, 200));
        UIPanel.setBounds(new Rectangle(0, 350, 820, 200));
        UIPanel.setMaximumSize(new Dimension(300, 32767));
        connectionPanel.setBounds(new Rectangle(10, 10, 175, 100));
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connection"));
        connectionPanel.setLayout(null);
        connectionPanel.setFont(new Font("Tahoma", 0, 11));

        jLabel1logfilename.setText("Log File:");
        jLabel1logfilename.setBounds(new Rectangle(10, 125, 165, 20));
        jLabel1logfilename.setHorizontalTextPosition(SwingConstants.RIGHT);
        jLabel1logfilename.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel1logfilename.setToolTipText("Specify the name of your log file here");
        
        jTextFieldNXTName.setBounds(new Rectangle(5, 40, 165, 20));
        jTextFieldNXTName.setToolTipText("The name or Address of the NXT. Leave empty and the first one found will be used.");
        jTextFieldNXTName.setText(ConfigurationManager.getConfigItem(ConfigurationManager.CONFIG_NXTNAME, ""));
        jTextFieldNXTName.requestFocus();
        
        jTextAreaStatus.setLineWrap(true);
        jTextAreaStatus.setFont(new Font("Tahoma", 0, 11));
        jTextAreaStatus.setWrapStyleWord(true);
        jTextAreaStatus.setBackground(SystemColor.window);
        
        dataLogTextArea.setLineWrap(false);
        dataLogTextArea.setFont(new Font("Tahoma", 0, 11));
        dataLogTextArea.setBackground(SystemColor.window);

        FQPathTextArea.setBounds(new Rectangle(5, 170, 185, 30));
        FQPathTextArea.setLineWrap(true);
//        FQPathTextArea.setText(getCanonicalName(new File(".", "")));
        FQPathTextArea.setText(
        	ConfigurationManager.getConfigItem(
        			ConfigurationManager.CONFIG_FILE_PATH, getCanonicalName(new File(".", ""))
        ));
        FQPathTextArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FQPathTextArea.setRows(2);

        FQPathTextArea.setFont(new Font("Tahoma", 0, 9));
        FQPathTextArea.setOpaque(false);
        FQPathTextArea.setEditable(false);
        
        selectFolderButton.setText("Folder...");
        selectFolderButton.setBounds(new Rectangle(120, 125, 70, 20));
        selectFolderButton.setMargin(new Insets(1, 1, 1, 1));
        selectFolderButton.setFocusable(false);
        selectFolderButton.setMnemonic('F');
        selectFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectFolderButton_actionPerformed(e);
            }
        });

        logFileTextField.setBounds(new Rectangle(10, 145, 180, 20));
//        logFileTextField.setText("NXTData" + ConfigurationManager.getConfigItem(ConfigurationManager.CONFIG_FILE_EXTENSION, ".txt"));
        
        logFileTextField.setPreferredSize(new Dimension(180, 20));
        logFileTextField.setToolTipText("File name. Leave empty to not log to file.");
        statusScrollPane.setOpaque(false);
        dataLogScrollPane.setOpaque(false);

        customChartPanel.setMinimumSize(new Dimension(400,300));
        customChartPanel.setPreferredSize(new Dimension(812, 400));
        
        jLabel5.setText("Name/Address:");
        jLabel5.setBounds(new Rectangle(5, 20, 160, 20));
        jLabel5.setToolTipText(jTextFieldNXTName.getToolTipText());
        jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
        jLabel5.setHorizontalAlignment(SwingConstants.LEFT);
        
        connectionPanel.add(jTextFieldNXTName, null);
        connectionPanel.add(jButtonConnect, null);
        connectionPanel.add(jLabel5, null);
        dataLogScrollPane.setViewportView(dataLogTextArea);
        jTabbedPane1.addTab("Data Log", dataLogScrollPane);
        statusScrollPane.setViewportView(jTextAreaStatus);
        jTabbedPane1.addTab("Status", statusScrollPane);
        jTabbedPane1.addTab("Chart", chartOptionsPanel);
        
        jTabbedPane1.setToolTipTextAt(0, "The tab-delimited log of the data sent from the NXT");
        jTabbedPane1.setToolTipTextAt(1, "Status output");
        jTabbedPane1.setToolTipTextAt(2, "Chart options");
        jTabbedPane1.setMnemonicAt(0, KeyEvent.VK_D);
        jTabbedPane1.setMnemonicAt(1,KeyEvent.VK_S);
        jTabbedPane1.setMnemonicAt(2,KeyEvent.VK_T);
        this.getContentPane().add(customChartPanel, 
                                  new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, 
                                                         new Insets(0, 0, 0, 
                                                                    0), 0, 0));
        this.getContentPane().add(UIPanel, 
                                  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, 
                                                         new Insets(0, 0, 0, 
                                                                    0), -107, 
                                                         0));

        this.getContentPane().add(jTabbedPane1, 
                                  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 
                    new Insets(0, 0, 0, 0), 0, 0));
        UIPanel.add(connectionPanel,null);
        UIPanel.add(selectFolderButton,null);
        UIPanel.add(logFileTextField,null);
        UIPanel.add(jLabel1logfilename,null);
        UIPanel.add(FQPathTextArea, null);
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String theData=null;
                for (;;) {
                    theData=LogChartFrame.this.logDataQueue.poll();
                    if (theData==null) break;
                    try {
                        dataLogTextArea.getDocument().insertString(dataLogTextArea.getDocument().getLength(),theData, null);
                    } catch (BadLocationException e) {
                        System.out.print("BadLocationException in datalog textarea updater thread:" +e.toString() + "\n");
                    }
                }
            }
         };
        this.updateLogTextAreaTimer = new Timer(1000, taskPerformer);
        this.updateLogTextAreaTimer.start();
    }

    /** Attempt to start a connection using a thread so the GUI stays responsive.
     * @return <code>false</code> if the file action is user-canceled (if file exists, user is
     * chicken-tested) or connection to NXT fails.
     */
    private boolean makeConnection(){
        final int fileAction = getFileAppend(); // sets theLogFile
        if (fileAction==2) return false; // return if closed or cancel
        // get the NXT to connect to and try to connect
        isNXTConnected=this.connectionManager.connect(jTextFieldNXTName.getText());
        ConfigurationManager.setConfigItem(ConfigurationManager.CONFIG_NXTNAME, jTextFieldNXTName.getText());
        if (isNXTConnected) {
            jTextFieldNXTName.setText(this.connectionManager.getConnectedName());
            tmm.setDataOutputStream(new DataOutputStream(this.connectionManager.getOutputStream()));
            
            new Thread(new Runnable() {
            	@SuppressWarnings("unused")
                private DataLogger dataLogger = null;
                
                
                // Start the logging run with the specified file
                public void run() {
                    try {
                        lpm= new LoggerProtocolManager(connectionManager.getInputStream(), connectionManager.getOutputStream());
                        chartOptionsPanel.setLoggerProtocolManagerRef(lpm);
                        lpm.addLoggerListener(loggerHook);
                    }
                    catch (IOException e) {
                        System.out.println(THISCLASS+" IOException in makeConnection():" + e);
                        return;
                    }
                    dataLogger = new DataLogger(lpm, theLogFile, fileAction==1);

                    // clear the data log text area 
                    try {
                        // TODO may need to save this in a popup window
                        dataLogTextArea.getDocument().remove(0, dataLogTextArea.getDocument().getLength());
                    } catch (BadLocationException e) {
                        // TODO what to do here? I'm pretty sure the try(...) code should never throw this...
                    }
                    
                    // if the log file field is empty, the PC logger will handle it. we need to make sure the title is appropo
                    // start the logger
                    try {
                        chartOptionsPanel.setPausePlaybuttonSelected(false);
                        lpm.startListen(); // will block until logging session ends
                    } catch (IOException e) {
                        System.out.println(THISCLASS+" IOException in makeConnection():" + e);
                    }
                    // remove the ref so we can gc()
                    dataLogger=null;
                    System.gc();
                }
            }).start();
        } 
        return isNXTConnected;
    }
    

	private void populateSampleData() {
        float value=0, value2=0;
        int x=0;

        DataItem[] di = {new DataItem(), new DataItem(), new DataItem()};
        di[0].datatype=DataItem.DT_INTEGER;
        di[1].datatype=DataItem.DT_FLOAT;
        di[2].datatype=DataItem.DT_FLOAT;
        loggerHook.logFieldNamesChanged(new String[]{"System_ms!n!1","Sine!Y!1","Random!y!2"}, LoggerProtocolManager.CT_XY_TIMEDOMAIN); 
        for (int i = 0; i < 10000; i++) {
            if (i%100==0) value2=(float)(Math.random()*5000-2500);
            if (i % 10 == 0) {
                di[0].value=new Integer(x);
                di[1].value=new Float(Math.sin(value));
                di[2].value=new Float(value2);
                loggerHook.logLineAvailable(di);
                if (i==4000) loggerHook.logCommentReceived(x,"Event 1: This is an example of what NXTDataLogger.writeComment() does.");
                if (i==6510) loggerHook.logCommentReceived(x,"Event 2: This illustrates that multiple comment markers can be added to the chart.");
                if (i==9000) loggerHook.logCommentReceived(x,"Look! Did you notice that the domain value is displayed in the tooltip? ");
                x += 10;
                value += .1f;
//                msDelay(20);
            }
        }
        chartOptionsPanel.setChartTitle("Sample Multiple Range Axis Dataset");
        loggerHook.dataInputStreamEOF();
        System.out.println("Sample dataset generation complete");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
//        // emulate a CMD_INIT_HANDLER command from the NXT lejos.util.LogMessageManager
//        byte[] buf = {0,3,-1,-1,1};
//        EndianTools.encodeShortBE(1, buf, 2);
//        tmm.processMessage(buf);
       
//        
//        // emulate a CMD_DELIVER_PACKET command from the NXT lejos.util.LogMessageManager
//		buf = new byte[10];
//		buf[0]= 3; // CMD_DELIVER_PACKET
//		buf[1]= 2; // TYPE_ROBOT_DRIVE
//		EndianTools.encodeShortBE(6, buf, 2); // Packet size = 6: 2 bytes , 1 float
//		buf[4]= 1; // Handler uniqID
//		buf[5]= 0; // handler command: Set Forward
//		EndianTools.encodeIntBE(Float.floatToIntBits(500.6675f), buf, 6);
//		System.out.println("emulate: intbits=" + Float.floatToIntBits(500f));
//		tmm.tunneledMessageReceived(buf);
 
    }
	
	static void msDelay(long period)
    {
        if (period <= 0) return;
        long end = System.currentTimeMillis() + period;
        boolean interrupted = false;
        do {
            try {
                Thread.sleep(period);
            } catch (InterruptedException ie)
            {
                interrupted = true;
            }
            period = end - System.currentTimeMillis();
        } while (period > 0);
        if (interrupted)
            Thread.currentThread().interrupt();
    }
	
    private String getCanonicalName(File file){
        String FQPFilename;
        try {
            FQPFilename = file.getCanonicalPath();
        } catch (IOException e) {
             FQPFilename="";
        }
        return FQPFilename;
    }

    /** if file exists, ask user to append or overwrite. sets this.theLogFile.
     * @return 0 to replace, 1 to append, 2 to cancel
     */
    private int getFileAppend(){
        JFrame frame = new JFrame("DialogDemo");
        int n=0;
        Object[] options = {"Replace it",
                            "Add to it",
                            "Cancel"};
        // if the log file field is empty, the PC logger will handle it. we need to make sure the title is appropo
        this.theLogFile = new File(FQPathTextArea.getText(), logFileTextField.getText());
        if (theLogFile.isFile()) {
            n = JOptionPane.showOptionDialog(frame,
                "File \"" + getCanonicalName(theLogFile) + "\" exists.\nWhat do you want to do?",
                "I have a Question...",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]); //default is "Replace it"
        }
        frame=null;
        if (n==JOptionPane.CLOSED_OPTION) n=2;
        return n;
    }
    
    /** Attempt to start a connection using a thread so the GUI stays responsive. Manage connect button state
     * @param e
     */
    private void jButtonConnect_actionPerformed(ActionEvent e) {
        final ActionEvent ee = e;
        Runnable doAction = new Runnable() {
            public void run() {
                if (jButtonConnect.getText().equals("Connect")) {
                    if (!isNXTConnected) {
                        jButtonConnect.setText("Connecting..");
                        jButtonConnect.setEnabled(false);
                        LogChartFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        if (makeConnection()) {
                            jButtonConnect.setText("Disconnect");
                        } else {
                            jButtonConnect.setText("Connect");
                        }
                        LogChartFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        jButtonConnect.setEnabled(true);
                    }

                    System.out.println(ee.getActionCommand().toString());
                } else {
                    closeCurrentConnection();
                }
            }
        };
        new Thread(doAction).start();
    }
    
    private void selectFolderButton_actionPerformed(ActionEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JFileChooser jfc = new JFileChooser(new File(FQPathTextArea.getText(), ""));
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setApproveButtonText("Select");
        jfc.setDialogTitle("Select Directory");
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        int returnVal = jfc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	String newPath = getCanonicalName(jfc.getSelectedFile());
            FQPathTextArea.setText(newPath);
            jfc.setCurrentDirectory(jfc.getSelectedFile());
            
            System.out.println("folder set to \"" + newPath + "\"");
            ConfigurationManager.setConfigItem(ConfigurationManager.CONFIG_FILE_PATH, newPath);
        }           
    }
    
    

    void fileExit_ActionPerformed(ActionEvent e) {
        customChartPanel=null;
        System.gc();
        System.exit(0);
    }
    
    
}
