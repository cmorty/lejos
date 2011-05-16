/*
 * Convert and image into a form suitable for use with the leJOS graphics
 * classes.
 *
 * Original code by Programus, imported to leJOS by Andy
 */

package lejos.pc.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;


public class NXJImageMainPanel extends JPanel {
	
	// header is LNI0 => 0x4c4e4930 (big endian)
	private static final int LNI0_HEADER = 0x4c4e4930;
	
	private int mode = NXJImageConverter.BIT_8;
	//implement JDK-1.6-like FileNameExtensionFilter
	private static class FileNameExtensionFilter extends FileFilter	{
		private final String message;
		private final String ext;
		
		public FileNameExtensionFilter(String message, String ext) {
			this.message = message;
			this.ext = "." + ext.toLowerCase();
		}

		@Override
		public boolean accept(File f) {
			return f != null && (f.isDirectory() || f.getName().toLowerCase().endsWith(this.ext));
		}

		@Override
		public String getDescription() {
			return this.message;
		}		
	}
	
	/** SN */
	private static final long serialVersionUID = -2222575532385000674L;

	private static final String EXT = "lni";

	private NXJImagePicturePanel picPanel = new NXJImagePicturePanel();
	private NXJImageCodePanel codePanel = new NXJImageCodePanel();

	private File lastDir = null;

	private byte[] currData;
	private Dimension currSize;

	public NXJImageMainPanel() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		splitPane.setLeftComponent(this.picPanel);
		splitPane.setRightComponent(this.codePanel);

		splitPane.setResizeWeight(1);

		this.setLayout(new BorderLayout());
		this.add(splitPane, BorderLayout.CENTER);

		this.picPanel.addPropertyChangeListener(NXJImagePicturePanel.IMAGE_UPDATE_PROP, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				NXJImageMainPanel.this.updateNxtPart();
			}
		});

		this.codePanel.addPropertyChangeListener(NXJImageCodePanel.CODE_UPDATE_PROP, new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				NXJImageMainPanel.this.updateImageFromCode();
			}
		});
	}

	protected void updateNxtPart() {
		String text;
		if (!this.picPanel.hasPic())
			text = "";
		else
		{
			currSize = this.picPanel.getImageSize();
			currData = this.picPanel.getNxtImageData();
			text = NXJImageConverter.getImageCreateString(currData, currSize,mode);
		}
		this.codePanel.setCode(text);
	}

	protected void updateImageFromCode() {
		String code = this.codePanel.getCode();
		
		String error;
		BufferedImage image;
		try
		{
			image = NXJImageConverter.getImageFromNxtImageCreateString(code,mode);
			error = null;
		}
		catch (Exception e)
		{
			image = null;
			error = e.getMessage();
		}
		
		//TODO properly convert error string to HTML
		if (image == null) {
			String message;
			if (mode == NXJImageConverter.BIT_8)
				message = "<html>Code format error: "+error+"<br />" +
					"Please use format like below:<br />" +
					"<code>(w,h) \"\\u00XX\\0\\u00XX...\"</code>" +
					"</html>";
			else if (mode == NXJImageConverter.BIT_16)
				message = "<html>Code format error: "+error+"<br />" +
						"Please use format like below:<br />" +
						"<code>(w,h) \"\\uXXXX\\0\\uXXXX...\"</code>" +
						"</html>";
			else if (mode == NXJImageConverter.BYTEA)
				message = "<html>Code format error: "+error+"<br />" +
				"Please use format like below:<br />" +
				"<code>new Image(w,h,new byte[]{(byte) 0xXX,(byte) 0xXX, ... })</code>" +
				"</html>";
			else
				message = "ERROR";
			JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			this.readImage(image);
		}
	}

	public boolean setFile(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		if (image == null)
			return false;
		
		this.picPanel.setImage(image);
		return true;
	}

	public void readImage(BufferedImage image) {
		this.picPanel.setImage(image);
	}

	protected void saveImage(File file) throws IOException {
		DataOutputStream out = new DataOutputStream(new FileOutputStream(file, false));
		try {
			out.writeInt(LNI0_HEADER);
			out.writeShort(this.currSize.width);
			out.writeShort(this.currSize.height);
			out.write(this.currData);
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
	}

	protected void readNxtImage(File file) throws IOException {
		byte[] data;
		int p, w, h;
		FileInputStream fin = new FileInputStream(file);
		try {
			DataInputStream in = new DataInputStream(fin);
			// byte size -> int size.
//			w = in.read();
//			if (w < 0) {
//				throw new IOException("File format error!");
//			}
//			h = in.read();
//			if (h < 0) {
//				throw new IOException("File format error!");
//			}
			try {
				p = in.readInt();
				w = in.readUnsignedShort();
				h = in.readUnsignedShort();
			} catch (EOFException e) {
				IOException e2 = new IOException("File format error!");
				e2.initCause(e);
				throw e2;
			}
			
			if (p != LNI0_HEADER)
				throw new IOException("File format error!");
			
			data = new byte[w * ((h + 7) / 8)];
			in.readFully(data);
		} finally {
			fin.close();
		}
		BufferedImage image = NXJImageConverter.nxtImageData2Image(data, w, h);
		this.readImage(image);
	}
	
	public JMenu modeLabel = new JMenu("Mode: 8 Bit");

	public JMenuBar getMenuBar(final JPanel panel) {
		JMenu menu;
		JMenuBar menuBar = new JMenuBar();

		// image menu
		menu = new JMenu("Image");
		menu.setMnemonic(KeyEvent.VK_I);
		menuBar.add(menu);
		menuBar.add(modeLabel);
		
		Action mode16Action = new AbstractAction("16 Bit Mode"){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0) {
				mode = NXJImageConverter.BIT_16;
				updateNxtPart();
				modeLabel.setText("Mode: 16 Bit");
			}
		};
		Action mode8Action = new AbstractAction("8 Bit Mode"){
			/**
			 * 
			 */
			private static final long serialVersionUID = -7948282904817247677L;

			public void actionPerformed(ActionEvent arg0) {
				mode = NXJImageConverter.BIT_8;
				updateNxtPart();
				modeLabel.setText("Mode: 8 Bit");
			}
		};
		Action modeBAction = new AbstractAction("Byte[] Mode"){
			/**
			 * 
			 */
			private static final long serialVersionUID = -7684693417480716472L;

			public void actionPerformed(ActionEvent arg0){
				mode = NXJImageConverter.BYTEA;
				updateNxtPart();
				modeLabel.setText("Mode: byte[]");
			}
		};
		modeLabel.add(mode8Action);
		modeLabel.add(mode16Action);
		modeLabel.add(modeBAction);

		// Import
		Action importFileAction = new AbstractAction("Import Image...") {
			/**SN*/
			private static final long serialVersionUID = -1915349463841717491L;

			public void actionPerformed(ActionEvent evt) {
				importImage(panel);
			}
		};
		// Export
		final Action exportFileAction = new AbstractAction("Export Image...") {
			/**SN*/
			private static final long serialVersionUID = -1915349463841717491L;

			public void actionPerformed(ActionEvent evt) {
				exportImage(panel);
			}
		};

		menu.add(importFileAction);
		menu.add(exportFileAction);
		menu.addSeparator();

		// Open lni (LeJOS NXT Image) file
		Action openFileAction = new AbstractAction("Open LeJOS NXT Image File...") {
			/**SN*/
			private static final long serialVersionUID = 3458676330985853465L;

			public void actionPerformed(ActionEvent evt) {
				openFile(panel);
			}
		};

		// Export
		final Action saveFileAction = new AbstractAction("Export LeJOS NXT Image File...") {
			/**SN*/
			private static final long serialVersionUID = 3458676330985853465L;

			public void actionPerformed(ActionEvent evt) {
				saveFile(panel);
			}
		};

		menu.add(openFileAction);
		menu.add(saveFileAction);

		menu.addSeparator();

		// Exit
		Action exitAction = new AbstractAction("Exit") {
			/**SN*/
			private static final long serialVersionUID = -2290105226448425978L;

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};

		menu.add(exitAction);

		menu.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
				//nothing
			}
			public void menuDeselected(MenuEvent e) {
				//nothing
			}
			public void menuSelected(MenuEvent e) {
				boolean b = NXJImageMainPanel.this.currData != null && NXJImageMainPanel.this.currSize != null;
				saveFileAction.setEnabled(b);
				exportFileAction.setEnabled(b);
			}
		});

		return menuBar;
	}

	private void importImage(final JPanel panel)
	{
		JFileChooser dialog = new JFileChooser(lastDir);
		if (dialog.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
			String errorMsg = null;
			lastDir = dialog.getCurrentDirectory();
			try {
				BufferedImage image = ImageIO.read(dialog.getSelectedFile());
				if (image == null) {
					errorMsg = "Not an image file or file format not supported. ";
				} else {
					boolean canReadImage = true;
					if (image.getWidth() * image.getHeight() > 100 * 64 * 2) {
						String message = "<html>NXT can only display images smaller than <font color='blue'>100x64</font>. <br>" +
							"But the image you are importing is <font color='red'>" + image.getWidth() + "x" + image.getHeight() + "</font>.<br>" +
							"Are you sure you want to import this image? <br>" +
							"(This may also cause a performance issue!)</html>";
						canReadImage = JOptionPane.showConfirmDialog(NXJImageMainPanel.this, message, "Confirm", JOptionPane.YES_NO_OPTION | JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
					}
					if (canReadImage) {
						NXJImageMainPanel.this.readImage(image);
					}
				}
			} catch (IOException e) {
				errorMsg = "Error occured when reading file: " + e.getMessage();
			}
			if (errorMsg != null) {
				JOptionPane.showMessageDialog(NXJImageMainPanel.this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void exportImage(final JPanel panel)
	{
		JFileChooser dialog = new JFileChooser(lastDir);
		if (dialog.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {
			lastDir = dialog.getCurrentDirectory();
			File file = dialog.getSelectedFile();
			if (file.exists()) {
				if (JOptionPane.showConfirmDialog(panel, "File exists. Overwrite?", "Confirm", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
					return;
				}
			}
			String errorMsg = null;
			try {
				String name = file.getName();
				int i = name.lastIndexOf('.');
				if (i < 0)
					errorMsg = "no file extension";
				else
				{
					String ext = name.substring(i+1);
					//TODO not really correct: using extension as format name
					ImageIO.write(picPanel.getBlackAndWhiteImage(), ext, file);
				}
			} catch (IOException e) {
				errorMsg = "Error occured when reading file: " + e.getMessage();
			}
			if (errorMsg != null) {
				JOptionPane.showMessageDialog(NXJImageMainPanel.this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveFile(final JPanel panel)
	{
		JFileChooser dialog = new JFileChooser(lastDir);
		dialog.setFileFilter(new FileNameExtensionFilter("LeJOS NXT Image File (*." + EXT + ")", EXT));
		dialog.setAcceptAllFileFilterUsed(false);
		if (dialog.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {
			lastDir = dialog.getCurrentDirectory();
			File file = dialog.getSelectedFile();
			if (file.exists()) {
				if (JOptionPane.showConfirmDialog(panel, "File exists. Overwrite?", "Confirm", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
					return;
				}
			}
			try {
				if (!file.exists()) {
					String fname = file.getPath();
					if (!fname.endsWith("." + EXT)) {
						if (!fname.endsWith(".")) {
							fname += ".";
						}
						file = new File(fname + EXT);
					}
					file.createNewFile();
				}
				if (!file.canWrite()) {
					JOptionPane.showMessageDialog(panel, "File cannot be written!", "Error", JOptionPane.ERROR_MESSAGE | JOptionPane.OK_OPTION);
					return;
				}
				NXJImageMainPanel.this.saveImage(file);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(panel, "<html>Error occured when write data into file.<br><font color='red'>" + e.getMessage() + "</font></html>", "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	private void openFile(final JPanel panel)
	{
		JFileChooser dialog = new JFileChooser(lastDir);
		dialog.setFileFilter(new FileNameExtensionFilter("LeJOS NXT Image File (*." + EXT + ")", EXT));
		dialog.setAcceptAllFileFilterUsed(false);
		if (dialog.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
			lastDir = dialog.getCurrentDirectory();
			File file = dialog.getSelectedFile();
			try {
				NXJImageMainPanel.this.readNxtImage(file);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(panel, "<html>Error occured when reading file.<br><font color='red'>" + e.getMessage() + "</font></html>", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}


}
