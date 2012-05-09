package lejos.pc.vision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Codec;
import javax.media.ConfigureCompleteEvent;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.UnsupportedPlugInException;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.CaptureDevice;
import javax.media.protocol.DataSource;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * Java version of Vision Command.
 */
public class Vision extends Frame implements ControllerListener
{
   // package protected fields
   static int imageWidth = -1, imageHeight = -1;
   static float frameRate = 15;
   static String snapshotFilename;
   static Processor p;
   static DataSource cds;
   static String cameraDevice, soundDevice;
   static boolean isRecording = false;
   static Recorder recorder;
   static boolean captureColor = false;
   static Vision visionFrame;
   static ColorEffect colorEffect = new ColorEffect();
   static FlipEffect flipEffect = new FlipEffect();
   static RegionEffect regionEffect = new RegionEffect();
   static MotionDetectionEffect motionDetectionEffect = new MotionDetectionEffect();

   // private instance variables

   private Object waitSync = new Object();
   private boolean stateTransitionOK = true;
   private static Properties videoProperties;
   private final static String DEFAULT_VIDEO_DEV_NAME = "vfw:Logitech USB Video Camera:0";
   private final static String DEFAULT_SOUND_DEV_NAME = "DirectSoundCapture";
   private static Region[] regions = new Region[Region.MAX_REGIONS];
   private static boolean takeSnapshot = false;

   /**
    * Create the viewer frame with a title.
    * 
    * @param title the title for the viewer
    */
   public Vision (String title)
   {
      super(title);
   }

   /**
    * Get the viewer frame. Allows extra controls to be added.
    * 
    * @return the frame
    */
   public static Frame getFrame ()
   {
      return (Frame) visionFrame;
   }

   /**
    * Given a datasource, create a processor and use that processor as a player
    * to playback the media.
    * 
    * During the processor's Configured state, the FlipEffect,
    * MotionDetectionEffect ColorEffect and RegionEffect are inserted into the
    * video track.
    *  
    */
   public boolean open (DataSource tds)
   {

      // Create a cloneable data source so that it cab be cloned for video
      // capture

      cds = Manager.createCloneableDataSource(tds);

      // Create a processor for the capture device

      try
      {
         p = Manager.createProcessor(cds);
      }
      catch (Exception e)
      {
         System.err
            .println("Failed to create a processor from the given datasource: "
               + e);
         return false;
      }

      // Make this Vision instance thr controller

      p.addControllerListener(this);

      // Put the Processor into configured state.

      p.configure();
      if (!waitForState(Processor.Configured))
      {
         System.err.println("Failed to configure the processor.");
         return false;
      }

      // So I can use it as a player.

      p.setContentDescriptor(null);

      // Obtain the track controls.

      TrackControl tc[] = p.getTrackControls();
      if (tc == null)
      {
         System.err
            .println("Failed to obtain track controls from the processor.");
         return false;
      }

      // Search for the track control for the video track.

      TrackControl videoTrack = null;

      for (int i = 0; i < tc.length; i++)
      {
         if (tc[i].getFormat() instanceof VideoFormat)
         {
            videoTrack = tc[i];
            break;
         }
      }

      if (videoTrack == null)
      {
         System.err.println("The input media does not contain a video track.");
         return false;
      }

      // Instantiate and set the frame access codec to the data flow path.

      try
      {
         Codec codec[] =
         {
            flipEffect, motionDetectionEffect, regionEffect, colorEffect
         };
         videoTrack.setCodecChain(codec);
      }
      catch (UnsupportedPlugInException e)
      {
         System.err.println("The processor does not support effects.");
         return false;
      }

      // Realize the processor.

      p.prefetch();
      if (!waitForState(Controller.Prefetched))
      {
         System.err.println("Failed to realize the processor.");
         return false;
      }

      // Layout the components

      setLayout(new BorderLayout());

      // Display the visual & control component if they exist.

      Component cc, vc;

      if ((vc = p.getVisualComponent()) != null)
      {
         add("Center", vc);
      }

      if ((cc = p.getControlPanelComponent()) != null)
      {
         add("South", cc);
      }

      // Start the processor.

      p.start();

      // Show the frame

      setVisible(true);

      // Detect the window close event

      addWindowListener(new WindowAdapter()
      {
         public void windowClosing (WindowEvent we)
         {
            p.close();
            System.exit(0);
         }
      });

      // If we get here, its worked

      return true;
   }

   /**
    * Close Video viewer
    */
   public static void stopViewer ()
   {
      visionFrame.setVisible(false);
      p.close();
   }

   public void addNotify ()
   {
      super.addNotify();
      pack();
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

   /**
    * Controller Listener.
    */
   public void controllerUpdate (ControllerEvent evt)
   {

      // System.out.println(this.getClass().getName()+evt);

      if (evt instanceof ConfigureCompleteEvent
         || evt instanceof RealizeCompleteEvent
         || evt instanceof PrefetchCompleteEvent)
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
            waitSync.notifyAll();
         }
      }
      else if (evt instanceof EndOfMediaEvent)
      {
         p.close();
         System.out.println("End of Media");
         System.exit(0);
      }
   }

   /**
    * Start the video viewer frame
    */
   public static void startViewer (String title)
   {

      /*
       * We use a properties file to allow the user to define what kind of
       * camera and resolution they want to use for the vision input
       */

      String videoPropFile = System.getProperty("video.properties",
         "video.properties");

      // Open the video properties file

      try
      {
         FileInputStream fis = new FileInputStream(new File(videoPropFile));
         videoProperties = new Properties();
         videoProperties.load(fis);
      }
      catch (IOException ioe)
      {
         System.err.println("Failed to read property file");
         System.exit(1);
      }

      // Set the camera device

      cameraDevice = videoProperties.getProperty("video-device-name",
         DEFAULT_VIDEO_DEV_NAME);

      // Set the sound device

      soundDevice = videoProperties.getProperty("sound-device-name",
         DEFAULT_SOUND_DEV_NAME);

      // If not set by the API, set the image width

      if (imageWidth < 0)
         imageWidth = Integer.parseInt(videoProperties.getProperty(
            "resolution-x", "320"));

      // If not set by the API, set the image height

      if (imageHeight < 0)
         imageHeight = Integer.parseInt(videoProperties.getProperty(
            "resolution-y", "320"));

      // System.out.println("Searching for [" + cameraDevice + "]");

      /*
       * Try to get the CaptureDevice that matches the name supplied by the user
       */
      CaptureDeviceInfo device = CaptureDeviceManager.getDevice(cameraDevice);

      if (device == null)
      {
         System.out.println("No device found [ " + cameraDevice + "]");
         System.exit(1);
      }

      // Create a media locator from the device

      MediaLocator ml = device.getLocator();

      // Create a data source from the media locator

      DataSource tds = null;

      try
      {
         tds = Manager.createDataSource(ml);
      }
      catch (Exception e)
      {
         System.err.println("Failed to create a datasource");
         System.exit(1);
      }

      // Set the format on the relevant format control for the device
      // This specifies the image height, width, fram rate etc.
      // These need to be supported by the device.
      // Only 24-bit color is supported

      FormatControl[] formatControls = ((CaptureDevice) tds)
         .getFormatControls();

      // Create the required format. Seem to need the extra 4 bytes

      Format format = new RGBFormat(new Dimension(imageWidth, imageHeight),
         (imageWidth * imageHeight * 3) + 4, Format.byteArray, frameRate, 24,
         3, 2, 1, 3, Format.NOT_SPECIFIED, Format.TRUE, Format.NOT_SPECIFIED);

      if (formatControls == null || formatControls.length == 0)
      {
         System.out.println("No format controls");
         System.exit(1);
      }

      // Only one format control is expected

      for (int i = 0; i < formatControls.length; i++)
      {
         if (formatControls[i] == null)
            continue;

         formatControls[i].setFormat(format);
      }

      // Create the frame
      visionFrame = new Vision(title);

      // Start the video viewer
      if (!visionFrame.open(tds))
         System.exit(1);
   }

   /**
    * Play an audio file
    * 
    * @param fileName the audio file to play
    */
   public static void playSound (String fileName)
   {

      // Create an Audio Stream from the file

      try
      {
         AudioInputStream stream = AudioSystem.getAudioInputStream(new File(
            fileName));

         // Get the Audio format
         javax.sound.sampled.AudioFormat format = stream.getFormat();

         // Create a DataLine Info object

         DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(),
            ((int) stream.getFrameLength() * format.getFrameSize()));

         // Create the audio clip

         Clip clip = (Clip) AudioSystem.getLine(info);

         // Load the audio clip - does not return until the audio file is
         // completely loaded

         clip.open(stream);

         // Start playing

         clip.start();

      }
      catch (IOException e)
      {
         System.err.println("Audio file not found");
      }
      catch (LineUnavailableException e)
      {
         System.err.println("Could not play the audio file");
      }
      catch (UnsupportedAudioFileException e)
      {
         System.err.println("Unsupported Audio File Format");
      }
   }

   /**
    * Add a rectangular region
    * 
    * @param region the region number
    * @param x the x co-ordinate of the region bottom left corner
    * @param y the y co-ordinate of the region bottom left corner
    * @param width the width of the region
    * @param height the height of the region
    */
   public static void addRectRegion (int region, int x, int y, int width,
      int height)
   {
      regions[region - 1] = new Region(x, y, width, height);
   }

   /**
    * Get the array of regions
    * 
    * @return the array of regions
    */
   public static Region[] getRegions ()
   {
      return regions;
   }

   /**
    * Set the frame rate
    * 
    * @param rate the required frame rate
    */
   public static void setFrameRate (float rate)
   {
      frameRate = rate;
   }

   /**
    * Set the size of the video viewer image
    * 
    * @param width the required image width
    * @param height the required image height
    */
   public static void setImageSize (int width, int height)
   {
      imageWidth = width;
      imageHeight = height;
   }

   /**
    * Add a Motion Listener for the region
    * 
    * @param region the region
    * @param ml the Motion Listener
    */
   public static void addMotionListener (int region, MotionListener ml)
   {
      regions[region - 1].addMotionListener(ml);
   }

   /**
    * Add a Color Listener for the region
    * 
    * @param region the region
    * @param cl the Color Listener
    * @param color the color to listen for
    */
   public static void addColorListener (int region, ColorListener cl, int color)
   {
      regions[region - 1].addColorListener(cl, color);
   }

   /**
    * Add a LightSensor Listener for the region
    * 
    * @param region the region
    * @param ll the LightSensor Listener
    */
   public static void addLightListener (int region, LightListener ll)
   {
      regions[region - 1].addLightListener(ll);
   }

   /**
    * Return the state of the snapshot flag
    * 
    * @return true if a snapshot is required, else false
    */
   static boolean takeSnapshot ()
   {
      return takeSnapshot;
   }

   /**
    * Take a snapshot
    * 
    * @param filename the JPG file to write the snapshop to
    */
   public static void snapshot (String filename)
   {
      snapshotFilename = filename;
      takeSnapshot = true;
   }

   /**
    * Set or unset the take snapshot flag
    * 
    * @param sanap true if snapshot is required, else false
    */
   static void setSnapshot (boolean snap)
   {
      takeSnapshot = snap;
   }

   /**
    * Write to <code>fn</code> file the <code>data</code> using the
    * <code>width, height</code> variables. Data is assumed to be 8bit RGB. A
    * JPEG format file is written.
    * 
    * @param fn the filename
    * @param data the data to write
    * @param width the width of the image
    * @param height the height of the image
    * @throws FileNotFoundException if the directory/image specified is wrong
    * @throws IOException if there are problems reading the file.
    */
   public static void writeImage (String fn, byte[] data, int width, int height)
      throws FileNotFoundException, IOException
   {

      // Open the file

      FileOutputStream fOut = new FileOutputStream(fn);

      // Create a JPG encoder for the file

      JPEGImageEncoder jpeg_encode = JPEGCodec.createJPEGEncoder(fOut);

      // Reformat the data to an array on int

      int ints[] = new int[data.length / 3];
      int k = 0;
      for (int i = height - 1; i > 0; i--)
      {
         for (int j = 0; j < width; j++)
         {
            ints[k++] = 255 << 24
               | (int) (data[i * width * 3 + j * 3 + 2] & 0xff) << 16
               | (int) (data[i * width * 3 + j * 3 + 1] & 0xff) << 8
               | (int) (data[i * width * 3 + j * 3] & 0xff);
         }
      }

      // Create a buffered image

      BufferedImage image = new BufferedImage(width, height,
         BufferedImage.TYPE_INT_RGB);
      image.setRGB(0, 0, width, height, ints, 0, width);

      // Encode the image and close the output file

      jpeg_encode.encode(image);
      fOut.close();
   }

   /**
    * Start the video recorder
    * 
    * @param fileName the file to write the video to
    * @param millis the number of milliseconds to record for. 0 means record
    *           until stopRecoder() is called.
    */
   public static void startRecorder (String fileName, int millis)
   {
      // Create the recorder
      recorder = new Recorder(fileName, millis);

      // Start the video viewer
      recorder.start();
   }

   /**
    * Test is recording is in progress
    * 
    * @return true if recoding, else false
    */
   public static boolean isRecording ()
   {
      return isRecording;
   }

   /*
    * Stop recording
    */
   public static void stopRecording ()
   {
      Recorder.stopRecording();
   }

   /**
    * Get the average red value for the region
    * 
    * @param region the region
    * @return the average red value
    */
   public static int getAvgRed (int region)
   {
      return colorEffect.averageRed[region - 1];
   }

   /**
    * Get the average green value for the region
    * 
    * @param region the region
    * @return the average green value
    */
   public static int getAvgGreen (int region)
   {
      return colorEffect.averageGreen[region - 1];
   }

   /**
    * Get the average blue value for the region
    * 
    * @param region the region
    * @return the average blue value
    */
   public static int getAvgBlue (int region)
   {
      return colorEffect.averageBlue[region - 1];
   }

   /**
    * Get the average RGB value for the region
    * 
    * @param region the region
    * @return the average RGB value
    */
   public static int getAvgRGB (int region)
   {
      return ((new Color(getAvgRed(region), getAvgGreen(region),
         getAvgBlue(region))).getRGB()) & 0xffffff;
   }

   /**
    * Flip the image in the image viewer horizontally
    * 
    * @param flip true to flip, else false
    */
   public static void flipHorizontal (boolean flip)
   {
      flipEffect.flip = flip;
   }

   /**
    * Comment for <code>serialVersionUID</code>
    */
   private static final long serialVersionUID = 3257286945972432951L;
}
