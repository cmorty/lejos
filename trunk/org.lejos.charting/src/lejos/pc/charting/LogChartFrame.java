package lejos.pc.charting;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.BadLocationException;

import org.jfree.chart.JFreeChart;


/** The main GUI window for NXT Charting Logger.
 * @author Kirk P. Thompson
 */
class LogChartFrame extends JFrame {
    private final String THISCLASS;
    private static final int MAXDOMAIN_DATAPOINT_LIMIT= 50000;
    private static final int MAXDOMAIN_TIME_LIMIT = 30000;
    private static final int MINDOMAIN_LIMIT= 10;
    private final static float DOMLIMIT_POW = 2.4f;
    
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
    private CustomChartPanel customChartPanel = new CustomChartPanel();
    private JFreeChart loggingJFreeChart= customChartPanel.getLoggingChartPanel().getChart();
    
    
    private final LoggerComms connectionManager;
    
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
    private JPanel chartOptionsPanel = new JPanel();
    
    private JPanel chartDomLimitsPanel = new JPanel();
    private JSlider domainDisplayLimitSlider = new JSlider();
    private int domainLimitSliderValue=MAXDOMAIN_DATAPOINT_LIMIT;
    private JRadioButton useTimeRadioButton = new JRadioButton();
    private JRadioButton useDataPointsRadioButton = new JRadioButton();
    private JCheckBox datasetLimitEnableCheckBox = new JCheckBox();
    private JLabel domainLimitLabel = new JLabel();
    private GridLayout gridLayout1 = new GridLayout();
   
    private JLabel jLabel1 = new JLabel();
    private JLabel jLabel2 = new JLabel();
    private JLabel jLabel3 = new JLabel();
    private JLabel jLabel4 = new JLabel();
    private JLabel jLabel6 = new JLabel();
    private JTextField chartTitleTextField = new JTextField();
    private JTextField axis1LabelTextField = new JTextField();
    private JTextField axis2LabelTextField = new JTextField();
    private JTextField axis3LabelTextField = new JTextField();
    private JTextField axis4LabelTextField = new JTextField();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JCheckBox showCommentsCheckBox = new JCheckBox();
    private Timer updateLogTextAreaTimer;
    private JCheckBox scrollDomainCheckBox = new JCheckBox();

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
        System.out.println("creating connectionManager instance");
        this.connectionManager = new LoggerComms();
        
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
        
        // manage Range axis label fields state
        for (int i=0;i<4;i++) manageAxisLabel(i);
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

    private void scrollDomainCheckBox_actionPerformed(ActionEvent e) {
        customChartPanel.getLoggingChartPanel().setDomainScrolling(scrollDomainCheckBox.isSelected());
    }


    /**This class is used to provide listener callbacks from DataLogger.
     */
    private class SelfLogger implements LoggerListener{
        private long lastUpdate=0;
        
        public void logCommentReceived(int timestamp, String comment) {
            String theComment = String.format("%1$-1d\t%2$s\n", timestamp, comment);
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
            period=(int)(2155.1*Math.exp(-.0024*period));
            if (period<200) period=200;
            LogChartFrame.this.updateLogTextAreaTimer.setDelay(period);
            lastUpdate=System.currentTimeMillis();
        }
        
        public void dataInputStreamEOF() {
            closeCurrentConnection();
            // allows user to use interactive stuff without chart glitch    
            System.out.println("Finalizing chart");  
            loggingJFreeChart.setNotify(true); 
            customChartPanel.getLoggingChartPanel().setChartDirty();
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
                    chartLabels.append(":");
                    chartLabels.append(this.seriesDefs[i].axisID);
                    chartLabels.append("!");
                }
            }
            sb.append("\n");
            try {
                // clear the data log text area
                dataLogTextArea.getDocument().remove(0, dataLogTextArea.getDocument().getLength());
            } catch (BadLocationException e) {
                // TODO what to do here? I'm pretty sure the try(...) code should never throw this...
            }
            LogChartFrame.this.logDataQueue.add(sb.toString());
            
            // set the chartable series headers/labels
            customChartPanel.setSeries(chartLabels.toString().split("!"));  
            
            if (theLogFile!=null) {
                if (theLogFile.isFile()) {
                    setChartTitle(getCanonicalName(theLogFile));
                } else {
                    setChartTitle("Run " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(new Date()));
                }
            }
            
            // manange Range axis label fields state
            for (int i=0;i<4;i++) manageAxisLabel(i);
        }
    }
    
    private void manageAxisLabel(int AxisIndex) {
        JLabel tempLabel;
        JTextField tempTextField;
        
        switch (AxisIndex){
            case 0:
                tempLabel=jLabel2;
                tempTextField=axis1LabelTextField;
                break;
            case 1:
                tempLabel=jLabel3;
                tempTextField=axis2LabelTextField;
                break;
            case 2:
                tempLabel=jLabel4;
                tempTextField=axis3LabelTextField;
                break;
            case 3:
                tempLabel=jLabel6;
                tempTextField=axis4LabelTextField;
                break;
            default:
                return;
        }
       
        boolean axisExists = loggingJFreeChart.getXYPlot().getRangeAxis(AxisIndex)!=null;
        tempTextField.setEnabled(axisExists);
        tempLabel.setEnabled(axisExists);
        if (!axisExists) return;
        
        String chartAxisLabel=loggingJFreeChart.getXYPlot().getRangeAxis(AxisIndex).getLabel();
        String customLabel=tempTextField.getText();
        if (customLabel.equals("")) {
            tempTextField.setText(chartAxisLabel);
        } else {
            loggingJFreeChart.getXYPlot().getRangeAxis(AxisIndex).setLabel(customLabel);
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
    
    private void setChartTitle(String title) {
        loggingJFreeChart.setTitle(title); 
        loggingJFreeChart.setNotify(true);
        if (chartTitleTextField.getText().equals(title)) return;
        chartTitleTextField.setText(title);
    }
    
    
    private class MenuEventListener implements MenuListener {
        private JMenuItem  getMenuItem(String menuFind, String menuItemFind) {
            MenuElement[] menus =  menuBar.getSubElements();
            JMenu menu=null;
            for (int i=0;i<menus.length;i++) {
                menu = (JMenu)menus[i];
                if (!menu.getActionCommand().equals(menuFind)) continue;
                MenuElement[] menuItems =  menu.getPopupMenu().getSubElements();
                JMenuItem menuItem=null;
                for (int j=0;j<menuItems.length;j++){
                    menuItem = (JMenuItem)menuItems[j];
                    if (menuItem.getActionCommand().equalsIgnoreCase(menuItemFind)) return menuItem;
                }
            }
            return null;
        }
        
        public void menuSelected(MenuEvent e) {
            JMenu menu = (JMenu)e.getSource();
            if (menu.getActionCommand().equals("VIEW_MENU")) {
                JMenuItem tempMenuItem = getMenuItem(menu.getActionCommand(), "Chart in New Window");
                if (tempMenuItem==null) {
                    return;
                }
                if (customChartPanel.getLoggingChartPanel().isEmptyChart()) {
                    tempMenuItem.setEnabled(false);
                } else {
                    tempMenuItem.setEnabled(true);
                }
            }
        }

        public void menuDeselected(MenuEvent e) {
            
        }

        public void menuCanceled(MenuEvent e) {
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
                    customChartPanel.getLoggingChartPanel().spawnChartCopy();
                } catch (OutOfMemoryError e2) {
                    JOptionPane.showMessageDialog(LogChartFrame.this, "Not enough memory to create chart!", "Houston, we have a problem...",
                        JOptionPane.ERROR_MESSAGE);
                }
                LogChartFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            
        }
    }
    
    // to allow keystroke-by-keystroke updating of Chart Title and range axis labels
    private class titleLabelChangeNotifier implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            setText(e);
        }

        public void removeUpdate(DocumentEvent e) {
            setText(e);
        }

        public void changedUpdate(DocumentEvent e) {
            setText(e);
        }
        
        private void setText(DocumentEvent e) {
            if (e.getDocument()==chartTitleTextField.getDocument() )  {
                setChartTitle(chartTitleTextField.getText());
            } else if (e.getDocument()==axis1LabelTextField.getDocument()) {
                setAxisLabel(0, axis1LabelTextField.getText());
            } else if (e.getDocument()==axis2LabelTextField.getDocument()) {
                setAxisLabel(1, axis2LabelTextField.getText());
            } else if (e.getDocument()==axis3LabelTextField.getDocument()) {
                setAxisLabel(2, axis3LabelTextField.getText());
            } else if (e.getDocument()==axis4LabelTextField.getDocument()) {
                setAxisLabel(3, axis4LabelTextField.getText());
            }
        }
        
        private void setAxisLabel(int rangeAxisID, String title) {
            try {
                loggingJFreeChart.getXYPlot().getRangeAxis(rangeAxisID).setLabel(title);
                loggingJFreeChart.setNotify(true);
            } catch (NullPointerException e ) {
                ; //ignore
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

        jTextFieldNXTName.requestFocus();
        
        jTextAreaStatus.setLineWrap(true);
        jTextAreaStatus.setFont(new Font("Tahoma", 0, 11));
        jTextAreaStatus.setWrapStyleWord(true);
        jTextAreaStatus.setBackground(SystemColor.window);
        
        dataLogTextArea.setLineWrap(false);
        dataLogTextArea.setFont(new Font("Tahoma", 0, 11));
        dataLogTextArea.setBackground(SystemColor.window);

        FQPathTextArea.setBounds(new Rectangle(5, 170, 185, 40));
        FQPathTextArea.setLineWrap(true);
        FQPathTextArea.setText(getCanonicalName(new File(".", "")));
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

        // domain display limits GUI
        chartOptionsPanel.setLayout(null);
        chartDomLimitsPanel.setBounds(new Rectangle(5, 35, 180, 135));
        chartDomLimitsPanel.setLayout(gridLayout1);
        chartDomLimitsPanel.setBorder(BorderFactory.createTitledBorder("Domain Display Limiting"));
        domainDisplayLimitSlider.setEnabled(false);
        domainDisplayLimitSlider.setMaximum(MAXDOMAIN_DATAPOINT_LIMIT);
        domainDisplayLimitSlider.setMinimum(MINDOMAIN_LIMIT);
        domainDisplayLimitSlider.setValue(MAXDOMAIN_DATAPOINT_LIMIT);
        domainDisplayLimitSlider.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        domainDisplayLimitSlider_stateChanged(e);
                    }
                });
        useTimeRadioButton.setText("By Time");
        useTimeRadioButton.setEnabled(false);
        useTimeRadioButton.setMnemonic('I');
        useTimeRadioButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        domainDisplayLimitRadioButton_actionPerformed(e);
                    }
                });
        useDataPointsRadioButton.setText("By Data Points");
        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(useTimeRadioButton);
        bg1.add(useDataPointsRadioButton);
        useDataPointsRadioButton.setSelected(true);
        useDataPointsRadioButton.setEnabled(false);
        useDataPointsRadioButton.setMnemonic('P');
        useDataPointsRadioButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        domainDisplayLimitRadioButton_actionPerformed(e);
                    }
                });
        datasetLimitEnableCheckBox.setText("Enable");
        datasetLimitEnableCheckBox.setRolloverEnabled(true);
        datasetLimitEnableCheckBox.setMnemonic('A');
        datasetLimitEnableCheckBox.setToolTipText("Enable Domain Clipping");
        datasetLimitEnableCheckBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        datasetLimitEnableCheckBox_actionPerformed(e);
                    }
                });
        domainLimitLabel.setText(String.format("%1$,d datapoints", MAXDOMAIN_DATAPOINT_LIMIT).toString());
        domainLimitLabel.setEnabled(false);
        gridLayout1.setRows(5);
        gridLayout1.setColumns(1);

        
        jLabel1.setText("Chart Title:");
        jLabel1.setBounds(new Rectangle(200, 10, 85, 20));
        jLabel1.setPreferredSize(new Dimension(115, 14));
        jLabel2.setText("Range Axis 1 Label:");
        jLabel2.setBounds(new Rectangle(200, 35, 115, 20));
        jLabel2.setSize(new Dimension(115, 20));
        jLabel3.setText("Range Axis 2 Label:");
        jLabel3.setBounds(new Rectangle(200, 60, 115, 20));
        jLabel3.setSize(new Dimension(115, 20));
        jLabel4.setText("Range Axis 3 Label:");
        jLabel4.setBounds(new Rectangle(200, 85, 115, 20));
        jLabel4.setSize(new Dimension(115, 20));
        jLabel6.setText("Range Axis 4 Label:");
        jLabel6.setBounds(new Rectangle(200, 110, 115, 20));
        jLabel6.setSize(new Dimension(115, 20));
        titleLabelChangeNotifier notifier = new titleLabelChangeNotifier();
        chartTitleTextField.setBounds(new Rectangle(315, 10, 290, 20));
        chartTitleTextField.getDocument().addDocumentListener(notifier);
        axis1LabelTextField.setBounds(new Rectangle(315, 35, 290, 20));
        axis1LabelTextField.getDocument().addDocumentListener(notifier);
        axis2LabelTextField.setBounds(new Rectangle(315, 60, 290, 20));
        axis2LabelTextField.getDocument().addDocumentListener(notifier);
        axis3LabelTextField.setBounds(new Rectangle(315, 85, 290, 20));
        axis3LabelTextField.getDocument().addDocumentListener(notifier);
        axis4LabelTextField.setBounds(new Rectangle(315, 110, 290, 20));
        showCommentsCheckBox.setText("Show Comment Markers");
        showCommentsCheckBox.setBounds(new Rectangle(200, 140, 185, 25));
        showCommentsCheckBox.setToolTipText("Show/Hide any comment markers on the chart");
        showCommentsCheckBox.setRolloverEnabled(true);
        showCommentsCheckBox.setSelected(true);
        showCommentsCheckBox.setMnemonic('M');
        showCommentsCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                customChartPanel.setCommentsVisible(e.getStateChange()==ItemEvent.SELECTED);
            }
        });
        scrollDomainCheckBox.setText("Scroll Domain");
        scrollDomainCheckBox.setBounds(new Rectangle(10, 5, 175, 20));
        scrollDomainCheckBox.setSize(new Dimension(175, 25));
        scrollDomainCheckBox.setSelected(true);
        scrollDomainCheckBox.setMnemonic('O');
        scrollDomainCheckBox.setToolTipText("Checked to scroll domain as new data is received");
        scrollDomainCheckBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        scrollDomainCheckBox_actionPerformed(e);
                    }
                });
        axis4LabelTextField.getDocument().addDocumentListener(notifier);
        
        logFileTextField.setBounds(new Rectangle(10, 145, 180, 20));
        logFileTextField.setText("NXTData.txt");
        logFileTextField.setPreferredSize(new Dimension(180, 20));
        logFileTextField.setToolTipText("File name. Leave empty to not log to file.");
        statusScrollPane.setOpaque(false);
        dataLogScrollPane.setOpaque(false);

        customChartPanel.setMinimumSize(new Dimension(400,300));
        customChartPanel.setPreferredSize(new Dimension(812, 400));
        
        jLabel5.setText("NXT Name/Address:");
        jLabel5.setBounds(new Rectangle(5, 20, 160, 20));
        jLabel5.setToolTipText(jTextFieldNXTName.getToolTipText());
        jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
        jLabel5.setHorizontalAlignment(SwingConstants.LEFT);

        connectionPanel.add(jTextFieldNXTName, null);
        connectionPanel.add(jButtonConnect, null);
        connectionPanel.add(jLabel5, null);

        dataLogScrollPane.getViewport().add(dataLogTextArea,null);
        jTabbedPane1.addTab("Data Log", dataLogScrollPane);
        statusScrollPane.getViewport().add(jTextAreaStatus, null);
        jTabbedPane1.addTab("Status", statusScrollPane);
        jTabbedPane1.addTab("Chart", chartOptionsPanel);
        chartDomLimitsPanel.add(datasetLimitEnableCheckBox, null);
        chartDomLimitsPanel.add(useDataPointsRadioButton, null);
        chartDomLimitsPanel.add(useTimeRadioButton, null);
        chartDomLimitsPanel.add(domainDisplayLimitSlider, null);
        chartDomLimitsPanel.add(domainLimitLabel, null);
        chartOptionsPanel.add(scrollDomainCheckBox, null);
        chartOptionsPanel.add(showCommentsCheckBox, null);
        chartOptionsPanel.add(axis4LabelTextField, null);
        chartOptionsPanel.add(axis3LabelTextField, null);
        chartOptionsPanel.add(axis2LabelTextField, null);
        chartOptionsPanel.add(axis1LabelTextField, null);
        chartOptionsPanel.add(chartTitleTextField, null);
        chartOptionsPanel.add(jLabel6, null);
        chartOptionsPanel.add(jLabel4, null);
        chartOptionsPanel.add(jLabel3, null);
        chartOptionsPanel.add(jLabel2, null);
        chartOptionsPanel.add(jLabel1, null);
        chartOptionsPanel.add(chartDomLimitsPanel, null);
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
                                  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, 
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
        if (isNXTConnected) {
            jTextFieldNXTName.setText(this.connectionManager.getConnectedNXTName());
            new Thread(new Runnable() {
                private DataLogger dataLogger = null;
                private LoggerProtocolManager lpm;
                
                // Start the logging run with the specifed file
                public void run() {
                    try {
                        lpm= new LoggerProtocolManager(connectionManager.getInputStream(), connectionManager.getOutputStream());
                        lpm.addLoggerListener(loggerHook);
                    }
                    catch (IOException e) {
                        System.out.println(THISCLASS+" IOException in makeConnection():" + e);
                        return;
                    }
                    dataLogger = new DataLogger(lpm, theLogFile, fileAction==1);
                    // if the log file field is empty, the PC logger will handle it. we need to make sure the title is appropo
                    // start the logger
                    try {
                        
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
    
    private void doWait(long milliseconds) {
         try {
             Thread.sleep(milliseconds);
         } catch (InterruptedException e) {
             ; // do nothing
             //Thread.currentThread().interrupt();
         }
    }
    
    private void populateSampleData() {
        float value=0, value2=0;
        int x=0;
        DataItem[] di = {new DataItem(), new DataItem(), new DataItem()};
        di[0].datatype=DataItem.DT_INTEGER;
        di[1].datatype=DataItem.DT_FLOAT;
        di[2].datatype=DataItem.DT_FLOAT;
        loggerHook.logFieldNamesChanged(new String[]{"System_ms!n!1","Sine!Y!1","Random!y!2"}); 
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
            }
        }
        setChartTitle("Sample Multiple Range Axis Dataset");
        loggerHook.dataInputStreamEOF();
        System.out.println("Sample dataset generation complete");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
            FQPathTextArea.setText(getCanonicalName(jfc.getSelectedFile()));
            jfc.setCurrentDirectory(jfc.getSelectedFile());
            System.out.println("folder set to \"" + getCanonicalName(jfc.getSelectedFile()) + "\"");
        }           
    }
    
    private void datasetLimitEnableCheckBox_actionPerformed(ActionEvent e) {
        useDataPointsRadioButton.setEnabled(datasetLimitEnableCheckBox.isSelected());
        useTimeRadioButton.setEnabled(datasetLimitEnableCheckBox.isSelected());
        domainDisplayLimitSlider.setEnabled(datasetLimitEnableCheckBox.isSelected());
        domainLimitLabel.setEnabled(datasetLimitEnableCheckBox.isSelected());
        if (datasetLimitEnableCheckBox.isSelected()) {
            customChartPanel.getLoggingChartPanel();
			int mode = LoggingChart.DAL_TIME;
            if (useDataPointsRadioButton.isSelected()) {
                customChartPanel.getLoggingChartPanel();
				mode=LoggingChart.DAL_COUNT;
            }
            customChartPanel.getLoggingChartPanel().setDomainLimiting(mode, this.domainLimitSliderValue);
        } else {
            customChartPanel.getLoggingChartPanel();
			customChartPanel.getLoggingChartPanel().setDomainLimiting(LoggingChart.DAL_UNLIMITED, 0);
        }
    }

    void fileExit_ActionPerformed(ActionEvent e) {
        customChartPanel=null;
        System.gc();
        System.exit(0);
    }
    
    private void domainDisplayLimitSlider_stateChanged(ChangeEvent e) {
        JSlider workingSlider=(JSlider)e.getSource();
        
        String unit="ms";
        customChartPanel.getLoggingChartPanel();
		int mode = LoggingChart.DAL_TIME;
        int maxSliderPerMode=MAXDOMAIN_TIME_LIMIT;
        if (useDataPointsRadioButton.isSelected()) {
            unit="datapoints";
            customChartPanel.getLoggingChartPanel();
			mode=LoggingChart.DAL_COUNT;
            maxSliderPerMode=MAXDOMAIN_DATAPOINT_LIMIT;
        }
        this.domainLimitSliderValue=workingSlider.getValue();
        
        int working=maxSliderPerMode;
        if (this.domainLimitSliderValue!=maxSliderPerMode) {
            working=(int)(Math.pow((float)this.domainLimitSliderValue/maxSliderPerMode, DOMLIMIT_POW) * this.domainLimitSliderValue)+MINDOMAIN_LIMIT;
        } 
        
        domainLimitLabel.setText(String.format("%1$,d %2$s",working, unit));
         
        if (workingSlider.getValueIsAdjusting()) return;
        customChartPanel.getLoggingChartPanel().setDomainLimiting(mode, working);
    }

    private void domainDisplayLimitRadioButton_actionPerformed(ActionEvent e) {
        if (e.getSource()==useTimeRadioButton) {
            domainDisplayLimitSlider.setMaximum(MAXDOMAIN_TIME_LIMIT);
            domainDisplayLimitSlider.setMinimum(MINDOMAIN_LIMIT);
            domainDisplayLimitSlider.setValue(MAXDOMAIN_TIME_LIMIT);
        } else if (e.getSource()==useDataPointsRadioButton) {
            domainDisplayLimitSlider.setMaximum(MAXDOMAIN_DATAPOINT_LIMIT);
            domainDisplayLimitSlider.setMinimum(MINDOMAIN_LIMIT);
            domainDisplayLimitSlider.setValue(MAXDOMAIN_DATAPOINT_LIMIT);
        }
        
    }

}
