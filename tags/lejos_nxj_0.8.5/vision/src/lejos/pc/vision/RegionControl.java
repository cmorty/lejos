package lejos.pc.vision;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.Control;
import javax.swing.JButton;

/**
 * Title: Lejos Vision System Description: REgion Control
 * 
 * @author Lawrie Griffiths
 */
public class RegionControl implements Control, ActionListener
{
   private Component component;
   private JButton button;
   private RegionEffect effect;

   /**
    * Create the Region Control
    */
   public RegionControl (RegionEffect effect)
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
         button = new JButton("Toggle Show Regions");
         button.addActionListener(this);

         button.setToolTipText("Click to toggle show regions on and off");

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
         effect.show = !effect.show;
      }
   }

}