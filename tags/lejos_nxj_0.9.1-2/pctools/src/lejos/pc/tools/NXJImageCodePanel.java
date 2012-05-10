/*
 * Provide access to a code fragment from a converted image. The code
 * can be pasted into a program to create the image.
 *
 * Original code by Programus, imported to leJOS by Andy
 */

package lejos.pc.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class NXJImageCodePanel extends JPanel {
	/** SN */
	private static final long serialVersionUID = -551193589723841126L;

	private JButton editExecButton = new JButton();
	private JTextArea nxtText = new JTextArea(5, 50);

	public final static String CODE_UPDATE_PROP = "codeUpdated";

	private final static String EDIT_LABEL = "Edit";
	private final static String EXEC_LABEL = "Execute";

	public NXJImageCodePanel() {
		super();
		this.allocateComponents();
	}

	protected void allocateComponents() {
		this.setLayout(new BorderLayout());
		this.add(this.editExecButton, BorderLayout.NORTH);
		this.add(new JScrollPane(this.nxtText), BorderLayout.CENTER);

		this.nxtText.setLineWrap(true);
		this.nxtText.setToolTipText("Ctrl-A to select all and Ctrl-C to copy");
		this.setEditEnable(false);

		this.editExecButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (EDIT_LABEL.equals(NXJImageCodePanel.this.editExecButton.getText())) {
					NXJImageCodePanel.this.setEditEnable(true);
				} else {
					NXJImageCodePanel.this.firePropertyChange(NXJImageCodePanel.CODE_UPDATE_PROP, true, false);
				}
			}
		});
	}

	public void setCode(String code) {
		this.nxtText.setText(code);
		this.setEditEnable(false);
	}

	public String getCode() {
		return this.nxtText.getText();
	}

	public void setEditEnable(boolean enabled) {
		this.editExecButton.setText(enabled ? EXEC_LABEL : EDIT_LABEL);
		this.nxtText.setEditable(enabled);
		this.nxtText.setBackground(enabled ? Color.WHITE : Color.GRAY);
		this.nxtText.requestFocus();
	}
}
