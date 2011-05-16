package lejos.pc.vision;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.Control;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Title: Lejos Vision System Description: Color Detection Control
 * 
 * @author Lawrie Griffiths
 */
public class ColorDetectionControl
   implements Control, ActionListener, ChangeListener
{
   private Component component;
   private JButton button;
   private JSlider threshold;
   private JSlider proportionThreshold;
   private JLabel label;
   private JLabel proportionLabel;
   private ColorEffect effect;
   private boolean debug;

   /**
    * Create the Motion Detection Control
    */
   public ColorDetectionControl (ColorEffect effect)
   {
      this.effect = effect;
   }

   /**
    * Return the visual component
    * 
    * @return the component containing the GUI controls
    */
   public Component getControlComponent ()
   {
      if (component == null)
      {
         label = new JLabel("Set Pixel difference threshold:");
         proportionLabel = new JLabel("Set Proportion threshold:");

         button = new JButton("Color Debug");
         button.addActionListener(this);

         button.setToolTipText("Click to turn debugging mode on/off");

         threshold = new JSlider(JSlider.HORIZONTAL, 0,
            ColorEffect.MAX_PIXEL_THRESHOLD / 4, ColorEffect.pixelThreshold / 4);

         threshold.setMajorTickSpacing(1);
         threshold.setPaintLabels(true);
         threshold.addChangeListener(this);

         proportionThreshold = new JSlider(JSlider.HORIZONTAL, 0,
            (int) (ColorEffect.MAX_PROPORTION / ColorEffect.PROPORTION_INC),
            (int) (ColorEffect.requiredProportion / ColorEffect.PROPORTION_INC));

         proportionThreshold.setMajorTickSpacing(1);
         proportionThreshold.setPaintLabels(true);
         proportionThreshold.addChangeListener(this);

         Box componentPanel = Box.createVerticalBox();
         componentPanel.add(label);
         componentPanel.add(threshold);
         componentPanel.add(proportionLabel);
         componentPanel.add(proportionThreshold);

         componentPanel.invalidate();
         component = componentPanel;
      }
      return component;
   }

   /**
    * Toggle debug
    * 
    * @param e the action event (ignored)
    */
   public void actionPerformed (ActionEvent e)
   {
      Object o = e.getSource();
      if (o == button)
      {
         debug = !debug;
      }
   }

   /**
    * Set the threshold value
    * 
    * @param e the Changeevent (ignored)
    */
   public void stateChanged (ChangeEvent e)
   {
      Object o = e.getSource();
      if (o == threshold)
      {
         ColorEffect.pixelThreshold = threshold.getValue() * 4;
      }
      if (o == proportionThreshold)
      {
         ColorEffect.requiredProportion = proportionThreshold.getValue()
            * ColorEffect.PROPORTION_INC;
      }
   }
}