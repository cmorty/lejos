package lejos.pc.charting;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class TimeSeriesOptionsPanel extends JPanel {
    private static final int MAXDOMAIN_DATAPOINT_LIMIT= 50000;
    private static final int MAXDOMAIN_TIME_LIMIT = 30000;
    private static final int MINDOMAIN_LIMIT= 10;
    private final static float DOMLIMIT_POW = 2.4f;
    
    private JCheckBox scrollDomainCheckBox = new JCheckBox();
    private JCheckBox showCommentsCheckBox = new JCheckBox();
    private JTextField axis4LabelTextField = new JTextField();
    private JTextField axis3LabelTextField = new JTextField();
    private JTextField axis2LabelTextField = new JTextField();
    private JTextField axis1LabelTextField = new JTextField();
    private JTextField chartTitleTextField = new JTextField();
    private JLabel jLabel6 = new JLabel();
    private JLabel jLabel2 = new JLabel();
    private JLabel jLabel3 = new JLabel();
    private JLabel jLabel4 = new JLabel();
    private JLabel jLabel1 = new JLabel();
    private JPanel chartDomLimitsPanel = new JPanel();
    private GridLayout gridLayout1 = new GridLayout();
    private JToggleButton tglbtnpauseplay;
    private JRadioButton useTimeRadioButton = new JRadioButton();
    private JRadioButton useDataPointsRadioButton = new JRadioButton();
    private JCheckBox datasetLimitEnableCheckBox = new JCheckBox();
    private JLabel domainLimitLabel = new JLabel();
    private JSlider domainDisplayLimitSlider = new JSlider();
    private int domainLimitSliderValue=MAXDOMAIN_DATAPOINT_LIMIT;
    
    private ChartModel customChartPanel = null; 
    private LoggerProtocolManager lpm = null;
    
    public TimeSeriesOptionsPanel(ChartModel customChartPanel) {
        this.customChartPanel = customChartPanel;
        init();
    }
    
    void setLoggerProtocolManagerRef(LoggerProtocolManager lpm){
        this.lpm = lpm;
    }
    
    private void init(){
        Font stdFont = new Font("Dialog", Font.PLAIN, 12);
        
        
        scrollDomainCheckBox.setFont(stdFont);
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
        
        showCommentsCheckBox.setText("Show Comment Markers");
        showCommentsCheckBox.setFont(stdFont);
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
        
        titleLabelChangeNotifier notifier = new titleLabelChangeNotifier();
        axis4LabelTextField.setBounds(new Rectangle(327, 110, 278, 20));
        axis4LabelTextField.getDocument().addDocumentListener(notifier);
        
        axis3LabelTextField.setBounds(new Rectangle(327, 85, 278, 20));
        axis3LabelTextField.getDocument().addDocumentListener(notifier);
        
        axis2LabelTextField.setBounds(new Rectangle(327, 60, 278, 20));
        axis2LabelTextField.getDocument().addDocumentListener(notifier);
        
        axis1LabelTextField.setBounds(new Rectangle(327, 35, 278, 20));
        axis1LabelTextField.getDocument().addDocumentListener(notifier);
        
        chartTitleTextField.setBounds(new Rectangle(327, 10, 278, 20));
        chartTitleTextField.getDocument().addDocumentListener(notifier);
        
        jLabel1.setText("Chart Title:");
        jLabel1.setFont(stdFont);
        jLabel1.setBounds(new Rectangle(200, 10, 85, 20));
        jLabel1.setPreferredSize(new Dimension(127, 14));
        jLabel2.setText("Range Axis 1 Label:");
        jLabel2.setFont(stdFont);
        jLabel2.setBounds(new Rectangle(200, 35, 127, 20));
        jLabel2.setSize(new Dimension(127, 20));
        jLabel3.setText("Range Axis 2 Label:");
        jLabel3.setBounds(new Rectangle(200, 60, 127, 20));
        jLabel3.setSize(new Dimension(127, 20));
        jLabel3.setFont(stdFont);
        jLabel4.setText("Range Axis 3 Label:");
        jLabel4.setFont(stdFont);
        jLabel4.setBounds(new Rectangle(200, 85, 127, 20));
        jLabel4.setSize(new Dimension(127, 20));
        jLabel6.setText("Range Axis 4 Label:");
        jLabel6.setFont(stdFont);
        jLabel6.setBounds(new Rectangle(200, 110, 127, 20));
        jLabel6.setSize(new Dimension(127, 20));
        
        gridLayout1.setRows(5);
        gridLayout1.setColumns(1);
        
        // domain display limits GUI
        this.setLayout(null);
        chartDomLimitsPanel.setBounds(new Rectangle(5, 35, 180, 135));
        chartDomLimitsPanel.setLayout(gridLayout1);
        chartDomLimitsPanel.setBorder(new TitledBorder(UIManager
                .getBorder("TitledBorder.border"), "Domain Display Limiting",
                TitledBorder.LEADING, TitledBorder.TOP, stdFont, null));
        
        useTimeRadioButton.setText("By Time");
        useTimeRadioButton.setFont(stdFont);
        useTimeRadioButton.setEnabled(false);
        useTimeRadioButton.setMnemonic('I');
        useTimeRadioButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        domainDisplayLimitRadioButton_actionPerformed(e);
                    }
                });
        useDataPointsRadioButton.setText("By Data Points");
        useDataPointsRadioButton.setFont(stdFont);
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
        datasetLimitEnableCheckBox.setFont(stdFont);
        datasetLimitEnableCheckBox.setFont(stdFont);
        datasetLimitEnableCheckBox.setRolloverEnabled(true);
        datasetLimitEnableCheckBox.setMnemonic('A');
        datasetLimitEnableCheckBox.setToolTipText("Enable Domain Clipping");
        datasetLimitEnableCheckBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        datasetLimitEnableCheckBox_actionPerformed(e);
                    }
                });
        domainLimitLabel.setText(String.format("%1$,d datapoints", new Integer(MAXDOMAIN_DATAPOINT_LIMIT)).toString());
        domainLimitLabel.setFont(stdFont);
        domainLimitLabel.setEnabled(false);
        
        domainDisplayLimitSlider.setEnabled(false);
        domainDisplayLimitSlider.setMaximum(MAXDOMAIN_DATAPOINT_LIMIT);
        domainDisplayLimitSlider.setMinimum(MINDOMAIN_LIMIT);
        domainDisplayLimitSlider.setValue(MAXDOMAIN_DATAPOINT_LIMIT);
        domainDisplayLimitSlider.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        domainDisplayLimitSlider_stateChanged(e);
                    }
                });
        
        chartDomLimitsPanel.add(datasetLimitEnableCheckBox, null);
        chartDomLimitsPanel.add(useDataPointsRadioButton, null);
        chartDomLimitsPanel.add(useTimeRadioButton, null);
        chartDomLimitsPanel.add(domainDisplayLimitSlider, null);
        chartDomLimitsPanel.add(domainLimitLabel, null);
        
        add(scrollDomainCheckBox, null);
        add(showCommentsCheckBox, null);
        add(axis4LabelTextField, null);
        add(axis3LabelTextField, null);
        add(axis2LabelTextField, null);
        add(axis1LabelTextField, null);
        add(chartTitleTextField, null);
        add(jLabel6, null);
        add(jLabel4, null);
        add(jLabel3, null);
        add(jLabel2, null);
        add(jLabel1, null);
        add(chartDomLimitsPanel, null);
        
        tglbtnpauseplay = new JToggleButton("");
        tglbtnpauseplay.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (lpm==null) return;
                lpm.setReaderPaused(e.getStateChange()==ItemEvent.SELECTED);
            }
        });
        
        tglbtnpauseplay.setSelectedIcon(new ImageIcon(LogChartFrame.class.getResource("/lejos/pc/charting/play.png")));
        tglbtnpauseplay.setIcon(new ImageIcon(LogChartFrame.class.getResource("/lejos/pc/charting/pause.png")));
        tglbtnpauseplay.setBounds(571, 135, 30, 30);
        this.add(tglbtnpauseplay);
        
    }
    
    void setPausePlaybuttonSelected(boolean selected){
        tglbtnpauseplay.setSelected(selected);
    }
    
    private void scrollDomainCheckBox_actionPerformed(ActionEvent e) {
        customChartPanel.setDomainScrolling(scrollDomainCheckBox.isSelected());
    }
    
    void manageAxisLabel() {
        JLabel tempLabel;
        JTextField tempTextField;
        for (int axisIndex=0;axisIndex<4;axisIndex++) {
            switch (axisIndex){
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
                    continue;
            }
           
            boolean axisExists = customChartPanel.axisExists(axisIndex);
            tempTextField.setEnabled(axisExists);
            tempLabel.setEnabled(axisExists);
            if (!axisExists) {continue;}
            
            String chartAxisLabel= customChartPanel.getAxisLabel(axisIndex);
            
            String customLabel=tempTextField.getText();
            if (customLabel.equals("")) {
                tempTextField.setText(chartAxisLabel);
            } else {
                //customChartPanel.setAxisLabel(axisIndex, customLabel);
                if (!chartAxisLabel.equals(customLabel)) {
                    tempTextField.setText(chartAxisLabel);
                }
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
                customChartPanel.setAxisLabel(rangeAxisID, title);
            } catch (NullPointerException e ) {
                 //ignore
            }
        }
    }
    
    void setChartTitle(String title) {
        customChartPanel.setChartTitle(title); 
        if (chartTitleTextField.getText().equals(title)) return;
        chartTitleTextField.setText(title);
    }
    
    private void datasetLimitEnableCheckBox_actionPerformed(ActionEvent e) {
        useDataPointsRadioButton.setEnabled(datasetLimitEnableCheckBox.isSelected());
        useTimeRadioButton.setEnabled(datasetLimitEnableCheckBox.isSelected());
        domainDisplayLimitSlider.setEnabled(datasetLimitEnableCheckBox.isSelected());
        domainLimitLabel.setEnabled(datasetLimitEnableCheckBox.isSelected());
        if (datasetLimitEnableCheckBox.isSelected()) {
            int mode = BaseXYChart.DAL_TIME;
            if (useDataPointsRadioButton.isSelected()) {
                mode=BaseXYChart.DAL_COUNT;
            }
            customChartPanel.setDomainLimiting(mode, this.domainLimitSliderValue);
        } else {
            customChartPanel.setDomainLimiting(BaseXYChart.DAL_UNLIMITED, 0);
        }
    }
    
    private void domainDisplayLimitSlider_stateChanged(ChangeEvent e) {
        JSlider workingSlider=(JSlider)e.getSource();
        
        String unit="ms";
        int mode = BaseXYChart.DAL_TIME;
        int maxSliderPerMode=MAXDOMAIN_TIME_LIMIT;
        if (useDataPointsRadioButton.isSelected()) {
            unit="datapoints";
            mode=BaseXYChart.DAL_COUNT;
            maxSliderPerMode=MAXDOMAIN_DATAPOINT_LIMIT;
        }
        this.domainLimitSliderValue=workingSlider.getValue();
        
        int working=maxSliderPerMode;
        if (this.domainLimitSliderValue!=maxSliderPerMode) {
            working=(int)(Math.pow((float)this.domainLimitSliderValue/maxSliderPerMode, DOMLIMIT_POW) * this.domainLimitSliderValue)+MINDOMAIN_LIMIT;
        } 
        
        domainLimitLabel.setText(String.format("%1$,d %2$s",working, unit));
         
        if (workingSlider.getValueIsAdjusting()) return;
        customChartPanel.setDomainLimiting(mode, working);
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
