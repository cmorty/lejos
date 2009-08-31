package lejos.pc.vision;

import java.io.IOException;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.ResourceUnavailableEvent;
import javax.media.StopByRequestEvent;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.SourceCloneable;

/**
 * Video recorder
 */
public class Recorder extends Thread implements ControllerListener
{
   // private instance variables

   static private Processor p;
   private Object waitSync = new Object();
   private boolean stateTransitionOK = true;
   private boolean eom = false;
   private boolean failed = false;
   private String filename;
   private int millis;

   /**
    * Create the recorder.
    */
   public Recorder (String filename, int millis)
   {
      this.filename = filename;
      this.millis = millis;
   }

   public static void stopRecording ()
   {
      p.stop();
   }

   /**
    *  
    */
   public void run ()
   {
      Vision.isRecording = true;

      DataSource ds = ((SourceCloneable) Vision.cds).createClone();

      Format formats[] = new Format[2];
      formats[0] = new AudioFormat(AudioFormat.LINEAR);
      formats[1] = new VideoFormat(VideoFormat.CINEPAK);
      FileTypeDescriptor outputType = new FileTypeDescriptor(
         FileTypeDescriptor.QUICKTIME);

      CaptureDeviceInfo di = CaptureDeviceManager.getDevice(Vision.soundDevice);

      DataSource[] dss = new DataSource[2];

      dss[0] = ds;

      System.out.println("Creating Audio data source");

      try
      {
         dss[1] = Manager.createDataSource(di.getLocator());
      }
      catch (Exception e)
      {
         System.out.println("Failed to create Audio data source "
            + e.getMessage());
         System.exit(1);
      }

      System.out.println("Creating Merging data source");

      DataSource mds = null;

      try
      {
         mds = Manager.createMergingDataSource(dss);
      }
      catch (Exception e)
      {
         System.out.println("Failed to merge data sources " + e.getMessage());
         System.exit(-1);
      }

      try
      {
         p = Manager.createRealizedProcessor(new ProcessorModel(mds, formats,
            outputType));
      }
      catch (Exception e)
      {
         System.err
            .println("Failed to create a processor from the given datasource: "
               + e);
         System.exit(-1);
      }

      p.addControllerListener(this);

      DataSource source = p.getDataOutput();

      // create a File protocol MediaLocator with the location
      // of the file to which the video is to be written
      MediaLocator dest = new MediaLocator("file://" + filename + ".mov");

      // create a datasink to do the file writing & open the
      // sink to make sure we can write to it.
      DataSink filewriter = null;
      try
      {
         filewriter = Manager.createDataSink(source, dest);
         filewriter.open();
      }
      catch (Exception e)
      {
         System.out.println("Failed to create file writer");
         System.exit(-1);
      }

      // now start the filewriter
      try
      {
         filewriter.start();
      }
      catch (IOException e)
      {
         System.exit(-1);
      }

      //  and start the processor.

      p.start();

      waitForState(Controller.Started);

      System.out.println("Recording...");

      // If milliseconds is zero, wait for stopRecording to be called

      if (millis > 0)
      {
         playToEndOfMedia(millis);
      }
      else
      {
         try
         {
            synchronized (waitSync)
            {
               waitSync.wait();
            }
         }
         catch (InterruptedException ioe)
         {}
      }

      p.close();

      Vision.isRecording = false;

      System.out.println("Finished");

      filewriter.close();

      // Restart the main processor, which is stopped as a side-effect

      Vision.p.start();
   }

   /**
    * Controller Listener.
    */
   public void controllerUpdate (ControllerEvent evt)
   {

      System.out.println(this.getClass().getName() + evt);

      if (evt instanceof StopByRequestEvent)
      {
         synchronized (waitSync)
         {
            stateTransitionOK = true;
            waitSync.notifyAll();
         }
      }
      else if (evt instanceof ResourceUnavailableEvent)
      {
         synchronized (waitSync)
         {
            stateTransitionOK = false;
            failed = true;
            waitSync.notifyAll();
         }
      }
      else if (evt instanceof EndOfMediaEvent)
      {
         eom = true;
      }
   }

   /**
    * Block until the processor has transitioned to the given state. Return
    * false if the transition failed.
    */
   boolean waitForState (int state)
   {
      synchronized (waitSync)
      {
         try
         {
            while (p.getState() != state && stateTransitionOK)
               waitSync.wait();
         }
         catch (Exception e)
         {}
      }
      return stateTransitionOK;
   }

   public boolean playToEndOfMedia (int timeOutMillis)
   {
      long startTime = System.currentTimeMillis();
      eom = false;
      failed = false;
      synchronized (this)
      {
         while (!eom && !failed)
         {
            try
            {
               wait(timeOutMillis);
            }
            catch (InterruptedException ie)
            {}
            if (System.currentTimeMillis() - startTime > timeOutMillis)
               break;
         }
      }
      return eom && !failed;
   }
}

