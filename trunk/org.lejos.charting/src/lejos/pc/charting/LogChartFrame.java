package lejos.pc.charting;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;


//import javax.swing.SwingWorker;


/** The main GUI window for NXT Charting Logger.
 * @author Kirk P. Thompson
 */
public class LogChartFrame extends JFrame {
    private final String THISCLASS;
    
    private JButton jButtonConnect = new JButton();
    private JPanel jPanel1 = new JPanel();
    private JLabel jLabel1logfilename = new JLabel();
    private JTextField jTextFieldNXTName = new JTextField();
    
    private JTextArea jTextAreaStatus = new JTextArea();
    private JLabel jLabel5 = new JLabel();
    private JTextField logFileTextField = new JTextField();
    private JScrollPane jScrollPaneStatus = new JScrollPane();
    private JScrollPane jScrollPaneDataLog = new JScrollPane();

    // make sure all non-GUI vars are below the ones added by Jdev
    private CustomChartPanel loggingChartPanel = new CustomChartPanel();
    private DataLogger dataLogger = null;
    private final LoggerComms connectionManager;
    
    private boolean isNXTConnected = false;
    private File theLogFile;
//    private JMenuBar menuBar = new JMenuBar();
//    private JMenu menuFile = new JMenu();
//    private JMenuItem menuFileExit = new JMenuItem();
    private JTabbedPane jTabbedPane1 = new JTabbedPane();
    private JTextArea jTextAreaDataLog = new JTextArea();
    private SelfLogger loggerHook = new SelfLogger();
    private JTextArea FQPathTextArea = new JTextArea();
    private JButton selectFolderButton = new JButton();
    private JSeparator jSeparator1 = new JSeparator();
    

    /** Default constructor
     */
    public LogChartFrame() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Hooking into System.out..");
        System.setOut(new redirector(System.out));
//        try {
//            jTextPaneOutPut.read(System.in,"system.in");
//        } catch (IOException e) {
//            // TODO
//            System.out.println(e.toString());
//            e.printStackTrace();
//        }
        System.out.println("creating connectionManager instance");
        this.connectionManager = new LoggerComms();
        
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
        
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new Thread(new Runnable(){
            public void run(){
                doWait(1000);
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        populateSampleData();
                    }
                });
               
            }
        }
        ).start();
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


    
    
    /**This class is used to provide listener callbacks from DataLogger.
     */
    private class SelfLogger implements DataLogger.LoggerListener{
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
                seriesDef = logFields[i].split("!");
                sd[i] = new SeriesDef();
                sd[i].name = seriesDef[0];
                if (seriesDef.length>1) {
                    sd[i].chartable=seriesDef[1].equalsIgnoreCase("y");
                    sd[i].axisID=Integer.valueOf(seriesDef[2]);
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

        // TODO where to do multiaxis flag?
        /** Parse chartable datapoints into a double array. Uses previos parsed seriesDefs[] to determine chartable
         * 
         * @param logLine
         * @return
         */
        private double[] parseDataPoints(String logLine) {
            String[] values = logLine.split("\t");
            double[] seriesTempvalues = new double[values.length];
            int chartableCount=0;
           
            for (int i=0;i<values.length;i++) { 
                if (seriesDefs[i].chartable) {
                    try {
                        seriesTempvalues[chartableCount]=Double.valueOf(values[i]);
                    } catch (NumberFormatException e){
                        System.err.format("%1$s.parseDataPoints: iterator [%2$d] invalid value: %3$s", this.getClass().getName(), i, values[i]);
                    }
                    chartableCount++;
                }
            }
            double[] seriesTempvalues2 = new double[chartableCount];
            System.arraycopy(seriesTempvalues, 0, seriesTempvalues2, 0, chartableCount);
            return seriesTempvalues2;
        }
        
        public void logLineAvailable(String logLine) {
            // tell the chart it has some data
            loggingChartPanel.addDataPoints(parseDataPoints(logLine)); 
            // send to redirected STDOUT (status text area)
            toLogArea(logLine);
        }

        public void dataInputStreamEOF() {
            closeCurrentConnection();
            // allows user use interactive stuff without glitch    
            System.out.println("Finalizing chart");
            loggingChartPanel.getChart().fireChartChanged();
            loggingChartPanel.getChart().setNotify(true);
        }

//        *  The string format/structure of each string field passed by NXTDataLogger is:<br>
//        *  <code>[name]![y or n to indicate if charted]![axis ID 1-4]</code>
//        *  <br>i.e. <pre>"MySeries!y!1"</pre>
        public void logFieldNamesChanged(String[] logFields) {
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
                    chartLabels.append("!");
                }
            }
            sb.append("\n");
            try {
                // clear the data log text area
                jTextAreaDataLog.getDocument().remove(0, jTextAreaDataLog.getDocument().getLength());
            } catch (BadLocationException e) {
                // TODO what to do here? I'm pretty sure the try(...) code should never throw this...
            }
            toLogArea(sb.toString());
            if (theLogFile!=null) {
                if (theLogFile.isFile()) {
                    loggingChartPanel.getChart().setTitle(getCanonicalName(theLogFile));
                } else {
                    loggingChartPanel.getChart().setTitle("");
                }
            }
            
            // set the chartable series headers/labels
            loggingChartPanel.setSeries(chartLabels.toString().split("!"));  
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
        public void write(int b) {}
        
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

    /** All the setup of components, etc. What's scary is Swing is a lightweight GUI framework...
     * @throws Exception
     */
    private void jbInit() throws Exception {
        this.getContentPane().setLayout(null);
        this.setSize(new Dimension(823, 598));
        this.setTitle("NXT Charting Logger");
        this.setResizable(false);
        this.setEnabled(true);
        
//        this.setJMenuBar(menuBar);
//        this.menuFile.setText( "File" );
//        menuBar.add( menuFile );
//        menuFile.add( menuFileExit );
//        menuFileExit.setText( "Exit" );
//        menuFileExit.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { fileExit_ActionPerformed( ae ); } } );


        jTabbedPane1.setBounds(new Rectangle(200, 370, 615, 195));


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
        jPanel1.setBounds(new Rectangle(10, 380, 175, 100));
        jPanel1.setBorder(BorderFactory.createTitledBorder("Connection"));
        jPanel1.setLayout(null);
        jPanel1.setFont(new Font("Tahoma", 0, 11));
        
        jLabel1logfilename.setText("Log File:");
        jLabel1logfilename.setBounds(new Rectangle(10, 490, 165, 20));
        jLabel1logfilename.setHorizontalTextPosition(SwingConstants.RIGHT);
        jLabel1logfilename.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel1logfilename.setToolTipText("Specify the name of your log file here");
        
        jTextFieldNXTName.setBounds(new Rectangle(5, 40, 165, 20));
        //jTextFieldNXTName.setText("DORK-1");
        jTextFieldNXTName.setToolTipText("The name or Address of the NXT. Leave empty and the first one found will be used.");
        jTextFieldNXTName.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jTextFieldNXTName_actionPerformed(e);
                    }
                });
        jTextFieldNXTName.requestFocus();
        
        jTextAreaStatus.setLineWrap(true);
        jTextAreaStatus.setFont(new Font("Tahoma", 0, 11));
        jTextAreaStatus.setWrapStyleWord(true);
        jTextAreaStatus.setBackground(SystemColor.window);
        
        jTextAreaDataLog.setLineWrap(false);
        jTextAreaDataLog.setFont(new Font("Tahoma", 0, 11));
        jTextAreaDataLog.setBackground(SystemColor.window);

        FQPathTextArea.setBounds(new Rectangle(10, 530, 185, 38));
        FQPathTextArea.setLineWrap(true);
        FQPathTextArea.setText(getCanonicalName(new File(".", "")));
        FQPathTextArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FQPathTextArea.setRows(2);

        FQPathTextArea.setFont(new Font("Tahoma", 0, 9));
        FQPathTextArea.setOpaque(false);
        FQPathTextArea.setEditable(false);
        
        selectFolderButton.setText("Folder...");
        selectFolderButton.setBounds(new Rectangle(120, 490, 70, 20));
        selectFolderButton.setMargin(new Insets(1, 1, 1, 1));
        selectFolderButton.setFocusable(false);
        selectFolderButton.setMnemonic('F');
        selectFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectFolderButton_actionPerformed(e);
            }
        });
        
        
        logFileTextField.setBounds(new Rectangle(10, 510, 180, 20));
        logFileTextField.setText("NXTData.txt");
        logFileTextField.setPreferredSize(new Dimension(180, 20));
        logFileTextField.setToolTipText("File name. Leave empty to not log to file.");
        logFileTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jTextFieldNXTName_actionPerformed(e);
            }
        });
        jScrollPaneStatus.setOpaque(false);
        jScrollPaneDataLog.setOpaque(false);
        jScrollPaneDataLog.setToolTipText("The tab-delimited log of the data sent from the NXT");
        
        loggingChartPanel.setPreferredSize(new Dimension(805, 335));
        loggingChartPanel.setBounds(new Rectangle(5, 5, 810, 340));
        loggingChartPanel.setMinimumSize(new Dimension(400, 200));
        loggingChartPanel.setBounds(new Rectangle(5, 5, 810, 360));
        
        jLabel5.setText("NXT Name/Address:");
        jLabel5.setBounds(new Rectangle(5, 20, 160, 20));
        jLabel5.setToolTipText(jTextFieldNXTName.getToolTipText());
        jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
        jLabel5.setHorizontalAlignment(SwingConstants.LEFT);

        jPanel1.add(jTextFieldNXTName, null);
        jPanel1.add(jButtonConnect, null);
        jPanel1.add(jLabel5, null);
        
        jScrollPaneDataLog.getViewport().add(jTextAreaDataLog,null);
        jTabbedPane1.addTab("Data Log", jScrollPaneDataLog);
        jScrollPaneStatus.getViewport().add(jTextAreaStatus, null);
        jTabbedPane1.addTab("Status", jScrollPaneStatus);
        loggingChartPanel.add(jSeparator1, null);
        
        this.getContentPane().add(loggingChartPanel, null);
        this.getContentPane().add(selectFolderButton, null);
        this.getContentPane().add(FQPathTextArea, null);
        this.getContentPane().add(jTabbedPane1, null);
        this.getContentPane().add(jPanel1, null);
        this.getContentPane().add(logFileTextField, null);
        this.getContentPane().add(jLabel1logfilename, null);
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
        if (isNXTConnected) {
            new Thread(new Runnable() {
                // Start the logging run with the specifed file
                public void run() {
                    // if the log file field is empty, the PC logger will handle it. we need to make sure the title is appropo
                    dataLogger = new DataLogger(connectionManager, theLogFile,fileAction==1);
                    dataLogger.addLoggerListener(loggerHook);
                    // start the logger
                    try {
                        dataLogger.startLogging();
                    } catch (IOException e) {
                        System.out.println(THISCLASS+" IOException in makeConnection():" + e);
                    }
                    // remove the ref so we can gc()
                    dataLogger.removeLoggerListener(loggerHook);
                    dataLogger=null;
                    System.gc();
                }
            }).start();
        } 
        return isNXTConnected;
    }
    
    private void doWait(long milliseconds) {
         try {
             Thread.sleep(milliseconds);
         } catch (InterruptedException e) {
             //Thread.currentThread().interrupt();
         }
    }
    
    private void populateSampleData() {
        float value=0, value2=0;
        int x=0;
        
        loggerHook.logFieldNamesChanged(new String[]{"System_ms!n!1","Sine!Y!1","Random!y!1"}); //,"c4","c5","c6","c7","c8","c9","c10"});
        for (int i = 0; i < 10000; i++) {
            if (i%40==0) value2=(float)(Math.random()*5-2.5);
            if (i % 10 == 0) {
                loggerHook.logLineAvailable(String.format("%1$-1d\t%2$-13.4f\t%3$-13.4f\n", x, Math.sin(value), value2));
                x += 10;
                value += .1f;
            }
        }
        loggingChartPanel.getChart().setTitle("Sample Dataset");
//        loggingChartPanel.initZoomWorkaround();
        System.out.println("Sample dataset generation complete");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    private void toLogArea(String x){
        final String fx = x;
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                //                    jTextAreaOutput.setText(jTextAreaOutput.getText() + fx + "\n" );
                try {
                    jTextAreaDataLog.getDocument().insertString(jTextAreaDataLog.getDocument().getLength(),fx, null);
                } catch (BadLocationException e) {
                    // TODO
                    System.out.print("BadLocationException:" +e.toString() + "\n");
                }
            }
        });
    }
    
    
    final JFrame mySelf = this; // needed by AIC
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
                        mySelf.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        if (makeConnection()) {
                            jButtonConnect.setText("Disconnect");
                        } else {
                            jButtonConnect.setText("Connect");
                        }
                        mySelf.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        jButtonConnect.setEnabled(true);
                    }

                    System.out.println(ee.getActionCommand().toString());
                    //            System.out.println(e.paramString());
                } else {
                    closeCurrentConnection();
                }
            }
        };
        new Thread(doAction).start();
    }
    
    private void selectFolderButton_actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser(new File(FQPathTextArea.getText(), ""));
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setApproveButtonText("Select");
        jfc.setDialogTitle("Select Directory");
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        int returnVal = jfc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            FQPathTextArea.setText(getCanonicalName(jfc.getSelectedFile()));
            jfc.setCurrentDirectory(jfc.getSelectedFile());
            System.out.println("folder set to \"" + getCanonicalName(jfc.getSelectedFile()) + "\"");
        }           
    }
    
    private void jTextFieldNXTName_actionPerformed(ActionEvent e) {
        if (e.getSource()==jTextFieldNXTName) System.out.println("true");
        System.out.println(e.paramString());
    }
    
    void fileExit_ActionPerformed(ActionEvent e) {
        loggingChartPanel=null;
        System.gc();
        System.exit(0);
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
    
//    void helpAbout_ActionPerformed(ActionEvent e) {
//        JOptionPane.showMessageDialog(this, new Frame1_AboutBoxPanel1(), "About", JOptionPane.PLAIN_MESSAGE);
//    }
    
}
