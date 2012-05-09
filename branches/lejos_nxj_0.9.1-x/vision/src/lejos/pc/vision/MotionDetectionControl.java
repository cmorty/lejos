package lejos.pc.vision;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.Control;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Title: Lejos Vision System Description: Motion Detection Control
 * 
 * @author Konrad Rzeszutek Modified bty Lawrie Griffiths for lejos vision
 *         system
 */
public class MotionDetectionControl
   implements Control, ActionListener, ChangeListener
{
   private Component component;
   private JButton button;
   private JSlider threshold;
   private JLabel label;
   private MotionDetectionEffect motion;

   /**
    * Create the Motion Detection Control
    */
   public MotionDetectionControl (MotionDetectionEffect motion)
   {
      this.motion = motion;
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
         label = new JLabel("Set Motion threshold:");
         button = new JButton("Motion Debug");
         button.addActionListener(this);

         button.setToolTipText("Click to turn debugging mode on/off");

         threshold = new JSlider(JSlider.HORIZONTAL, 0,
            motion.THRESHOLD_MAX / 1000, motion.THRESHOLD_INIT / 1000);

         threshold.setMajorTickSpacing(motion.THRESHOLD_INC / 1000);
         threshold.setPaintLabels(true);
         threshold.addChangeListener(this);

         Panel componentPanel = new Panel();
         componentPanel.setLayout(new BorderLayout());
         componentPanel.add("South", button);
         componentPanel.add("Center", threshold);
         componentPanel.add("North", label);
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
         if (motion.debug == false)
            motion.debug = true;
         else
            motion.debug = false;
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
         motion.blob_threshold = threshold.getValue() * 1000;
      }
   }
}