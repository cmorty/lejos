package lejos.pc.charting;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Debug output console implementation. Utilized by <code>lejos.util.LoggerDebugConsole</code>.
 * 
 * @author Kirk P. Thompson
 *
 */
public class PanelDebugConsole extends AbstractTunneledMessagePanel {
	private JTextArea textAreaConsole;
	
	public PanelDebugConsole(int handlerID,
			ExtensionGUIManager extensionGUIManager) {
		super(handlerID, extensionGUIManager);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 23, 498, 138);
		add(scrollPane);
		
		textAreaConsole = new JTextArea();
		textAreaConsole.setLineWrap(true);
		textAreaConsole.setTabSize(4);
		scrollPane.setViewportView(textAreaConsole);
		
		JButton btnCopyAll = new JButton("Copy All");
		btnCopyAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyToClipboard(textAreaConsole);
			}
		});
		btnCopyAll.setMnemonic('A');
		btnCopyAll.setBounds(522, 138, 89, 23);
		add(btnCopyAll);
		this.registerCommandCallback(0, CMD_IGNORE);// for setting the label
	}

	/* (non-Javadoc)
	 * @see lejos.pc.charting.AbstractTunneledMessagePanel#getHandlerTypeID()
	 */
	@Override
	protected int getHandlerTypeID() {
		return TYPE_DEBUG_CONSOLE;
	}

	/* (non-Javadoc)
	 * @see lejos.pc.charting.AbstractTunneledMessagePanel#customSETMessage(int, byte[])
	 */
	@Override
	protected void customSETMessage(int command, byte[] message) {
		String label = super.decodeString(message) + "\n";
		switch (command){
			case 0: //Set Command 1 1abel
			try {
				textAreaConsole.getDocument().insertString(textAreaConsole.getDocument().getLength(),label, null);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				System.out.println("customSETMessage error:");
				e.printStackTrace();
			}
				break;
			default:
				return;
		}

	}

	@Override
	protected boolean requestFocusOnMessage() {
		return false;
	}
	
	private void copyToClipboard(JTextArea dataLogTextArea){
		int curPos = dataLogTextArea.getCaretPosition();
        try {
            dataLogTextArea.selectAll();
            dataLogTextArea.copy();
        } catch (OutOfMemoryError e2) {
//            JOptionPane.showMessageDialog(LogChartFrame.this, "Not enough memory to copy log data!", "Houston, we have a problem...",
//                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e2) {
//            JOptionPane.showMessageDialog(LogChartFrame.this, "Problem copying log data! " + e2.toString(), "Houston, we have a problem...",
//                JOptionPane.ERROR_MESSAGE);
        }
        dataLogTextArea.setCaretPosition(curPos);
	}
	
	@Override
	protected boolean showPollButton() {
		return false;
	}
}
