package net.mosen.nxt.instrumentation;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;


//import javax.swing.SwingWorker;


public class LogChartFrame extends JFrame {
    private final String THISCLASS;
    
    private JButton jButtonConnect = new JButton();
    private JPanel jPanel1 = new JPanel();
    private JLabel jLabel1logfilename = new JLabel();
    private JTextField jTextFieldNXTName = new JTextField();
    
    private JTextArea jTextAreaStatus = new JTextArea();
    private JLabel jLabel5 = new JLabel();
    private JTextField jTextFieldLogfFile = new JTextField();
    private JScrollPane jScrollPaneStatus = new JScrollPane();
    private JScrollPane jScrollPaneDataLog = new JScrollPane();

    private Timer myTimer;
    private int timerVal=100;
    
    // make sure all non-GUI vars are below the ones added by Jdev
    private LogChartPanel loggingChartPanel = new LogChartPanel();
    //private Panel1 loggingChartPanel = new Panel1();
    private PCsideNXTDataLogger dataLogger = null;
    private final PCConnectionManager connectionManager;
    
    private boolean isNXTConnected = false;
    private File theLogFile;
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menuFile = new JMenu();
    private JMenuItem menuFileExit = new JMenuItem();
    private JTabbedPane jTabbedPane1 = new JTabbedPane();
    private JSlider jSliderDomainScale = new JSlider();
    private JTextArea jTextAreaDataLog = new JTextArea();
    //private JLabel jLabelXYval = new JLabel();


    public LogChartFrame() {
        
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Redirecting System.out..");
        System.setOut(new redirector(System.out));
//        try {
//            jTextPaneOutPut.read(System.in,"system.in");
//        } catch (IOException e) {
//            // TODO
//            System.out.println(e.toString());
//            e.printStackTrace();
//        }
        System.out.println("creating connectionManager instance");
        this.connectionManager = new PCConnectionManager();
        
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
    }


    public void closeCurrentConnection(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                jButtonConnect.setText("Connect");
                if (connectionManager!=null) connectionManager.closeConnection();
                isNXTConnected = false;
            }
        });
    }

    private void jTextFieldXval_actionPerformed(ActionEvent e) {
    }


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
                    //                    jTextAreaOutput.setText(jTextAreaOutput.getText() + fx + "\n" );
                    try {
                        jTextAreaStatus.getDocument().insertString(jTextAreaStatus.getDocument().getLength(),fx, null);
                    } catch (BadLocationException e) {
                        // TODO
                        System.out.print("BadLocationException:" +e.toString() + "\n");
                    }
                }
            });
        }
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(null);
        this.setSize(new Dimension(844, 548));
        this.setTitle("NXT Logger");
        this.setResizable(false);
        this.setEnabled(true);
        
        this.setJMenuBar(menuBar);
        this.menuFile.setText( "File" );
        menuBar.add( menuFile );
        menuFile.add( menuFileExit );
        menuFileExit.setText( "Exit" );
        menuFileExit.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { fileExit_ActionPerformed( ae ); } } );


        jTabbedPane1.setBounds(new Rectangle(200, 315, 615, 175));
        jSliderDomainScale.setBounds(new Rectangle(5, 275, 190, 25));
        jSliderDomainScale.setOpaque(false);
        jSliderDomainScale.setToolTipText("Use slider to change domain scale");
        jSliderDomainScale.setMinimum(1);
        jSliderDomainScale.setMaximum(1000);
        jSliderDomainScale.setMajorTickSpacing(1);
        jSliderDomainScale.setValue(1000);
        jSliderDomainScale.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jSliderDomainScale.setRequestFocusEnabled(false);

        jSliderDomainScale.setFocusable(false);
        class DomainSliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting() || true) {
                    loggingChartPanel.setDomainWidth(source.getValue());
                }    
            }
        }
        jSliderDomainScale.addChangeListener(new DomainSliderListener());

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
        jPanel1.setBounds(new Rectangle(10, 310, 175, 100));
        jPanel1.setBorder(BorderFactory.createTitledBorder("Connection"));
        jPanel1.setLayout(null);
        jPanel1.setFont(new Font("Tahoma", 0, 11));
        jLabel1logfilename.setText("Logfilename:");
        jLabel1logfilename.setBounds(new Rectangle(10, 445, 165, 20));
        jLabel1logfilename.setToolTipText("null");
        jLabel1logfilename.setHorizontalTextPosition(SwingConstants.RIGHT);
        jLabel1logfilename.setHorizontalAlignment(SwingConstants.LEFT);
        jTextFieldNXTName.setBounds(new Rectangle(5, 35, 165, 20));
        jTextFieldNXTName.setText("DORK-1");
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


//        jLabelXYval.setText("jLabelXYval");
//        jLabelXYval.setBounds(new Rectangle(595, 275, 120, 20));
//        jLabelXYval.setFocusable(false);
//        jLabelXYval.setFont(new Font("Arial", 0, 10));
//        jLabelXYval.setHorizontalAlignment(SwingConstants.RIGHT);
        
        jTextFieldLogfFile.setBounds(new Rectangle(10, 465, 165, 20));
        jTextFieldLogfFile.setText("NXTData.log");
        jTextFieldLogfFile.setSize(new Dimension(165, 20));
        jTextFieldLogfFile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jTextFieldNXTName_actionPerformed(e);
                    }
                });
        jScrollPaneStatus.setOpaque(false);
        jScrollPaneDataLog.setOpaque(false);

        loggingChartPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        loggingChartPanel.setLayout(null);
        loggingChartPanel.setPreferredSize(new Dimension(600, 400));
        loggingChartPanel.setBounds(new Rectangle(15, 5, 800, 300));
        
        jLabel5.setText("NXT Name/Address:");
        jLabel5.setBounds(new Rectangle(5, 15, 160, 20));
        jLabel5.setToolTipText("null");
        jLabel5.setHorizontalTextPosition(SwingConstants.RIGHT);
        jLabel5.setHorizontalAlignment(SwingConstants.CENTER);

        jPanel1.add(jTextFieldNXTName, null);
        jPanel1.add(jButtonConnect, null);
        jPanel1.add(jLabel5, null);
        jScrollPaneStatus.getViewport().add(jTextAreaStatus, null);
        jScrollPaneDataLog.getViewport().add(jTextAreaDataLog,null);
        jTabbedPane1.addTab("Status", jScrollPaneStatus);
        jTabbedPane1.addTab("Data", jScrollPaneDataLog);
        this.getContentPane().add(jTabbedPane1, null);
//        loggingChartPanel.add(jLabelXYval, null);
        loggingChartPanel.add(jSliderDomainScale, null);
        this.getContentPane().add(loggingChartPanel, null);

        this.getContentPane().add(jPanel1, null);
        this.getContentPane().add(jTextFieldLogfFile, null);
        this.getContentPane().add(jLabel1logfilename, null);
        this.myTimer = new Timer(timerVal,new ActionListener(){
            float x=0;
            public void actionPerformed(ActionEvent evt){
                //loggingChartPanel.add(Math.random()*5, Math.random()*5);
                 //loggingChartPanel.add(Math.sin(x), Math.random()*5);
                 x+=.35;
                 if  (x>360f) x= 0;
            }
        });
        myTimer.setRepeats(true);
//        myTimer.start();
        
//        SwingWorker worker = new SwingWorker<Integer, Void>() {
//            public Integer doInBackground() {
//                while(!this.isCancelled()){
//                    chartPanelPIDPlot.add(Math.random()*5, Math.random()*5);
//                    doWait(100);
//                }
//                return new Integer(0);
//            }
//            private void doWait(long milliseconds) {
//                try {
//                    Thread.sleep(milliseconds);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        };
//        worker.execute();
    }

    private boolean makeConnection(){
        isNXTConnected=this.connectionManager.connect(jTextFieldNXTName.getText());
        if (isNXTConnected) {
            new Thread(new Runnable() {
                // Start the logging run with the specifed file
                public void run() {
                    theLogFile = new File(".", jTextFieldLogfFile.getText());
                    dataLogger = new PCsideNXTDataLogger(connectionManager, theLogFile);
                    dataLogger.addLoggerListener(new Self_Logger());
                    dataLogger.startLogging();
                }
            }).start();
        } 
        return isNXTConnected;
    }
    

    
    
    
    private class Self_Logger implements PCsideNXTDataLogger.LoggerListener{
        public void logLineAvailable(String logLine) {
            // tell the chart it has some data
            loggingChartPanel.addDataPoints(logLine);
            // send to redirected STDOUT (status text area)
//            System.out.print(logLine);
            toLogArea(logLine);
        }

        public void dataInputStreamEOF() {
            // TODO shut down the connection from here
            closeCurrentConnection();
//            _JFChartPrimary.setNotify(true)
            // allows user use interactive stuff without glitch    
            loggingChartPanel.getChart().setNotify(true);
            loggingChartPanel.getChart().fireChartChanged();
            //loggingChartPanel.getChart().getXYPlot().getDomainAxis().setAutoRange(true);
        }

        public void logFieldNamesChanged(String[] logFields) {
            System.out.println("client:logFieldNamesChanged");
            loggingChartPanel.setSeries(logFields);
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<logFields.length;i++){
                sb.append(logFields[i]);
                if (i<logFields.length-1) sb.append("\t");
            }
            sb.append("\n");
            try {
                jTextAreaDataLog.getDocument().remove(0, jTextAreaDataLog.getDocument().getLength());
            } catch (BadLocationException e) {
                // TODO
            }
            toLogArea(sb.toString());
            try {
                if (theLogFile!=null) loggingChartPanel.setTitle(theLogFile.getCanonicalPath());
            } catch (IOException e) {
                ; //IGNORE
            }
        }
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
    
    
    final JFrame mySelf = this;
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
        (new Thread(doAction)).start();
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
    
}
