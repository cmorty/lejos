package lejos.pc.vision;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.Control;
import javax.swing.JButton;

/**
 * Title: Lejos Vision System Description: Flip Control
 * 
 * @author Lawrie Griffiths
 */
public class FlipControl implements Control, ActionListener
{
   private Component component;
   private JButton button;
   private FlipEffect effect;

   /**
    * Create the Motion Detection Control
    */
   public FlipControl (FlipEffect effect)
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
         button = new JButton("Toggle Flip");
         button.addActionListener(this);

         button.setToolTipText("Click to toggle horizontal flip on and off");

         Panel componentPanel = new Panel();
         componentPanel.setLayout(new BorderLayout());
         componentPanel.add("Center", button);
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
         effect.flip = !effect.flip;
      }
   }

}