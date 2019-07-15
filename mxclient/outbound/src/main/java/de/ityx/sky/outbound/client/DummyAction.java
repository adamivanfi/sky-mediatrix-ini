package de.ityx.sky.outbound.client;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.StartAction;
import de.ityx.mediatrix.data.ProjectInfo;

/**
 * overrides the default behavior of the service center frame.
 */
public class DummyAction extends StartAction {
    private static final long serialVersionUID = 7415540589841293795L;

    public static int         projectId        = 0;

    public DummyAction() {
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('&', InputEvent.ALT_GRAPH_MASK));
        putValue(ACTION_COMMAND_KEY, "     ");
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_AMPERSAND));
        putValue(NAME, " ");
        putValue(SHORT_DESCRIPTION, "");
        putValue(LONG_DESCRIPTION, "");

        putValue(SMALL_ICON, loadIcon("terminmanager.png"));
        putValue(BIG_ICON, loadIcon("terminmanagerBig.png"));
        putValue(BIG_ICON_OVER, loadIcon("terminmanagerBig_over.png"));
        
    }
    
    public static ImageIcon loadIcon(String classpath) {
    	ImageIcon icon = null;
    	InputStream resourceAsStream = DummyAction.class.getResourceAsStream("/de/ityx/layout/resources/icon/terminmanagerBig.png");
    	System.out.println(resourceAsStream);
    	try {
    		BufferedImage image = ImageIO.read(resourceAsStream);
    		icon = new ImageIcon(image);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return icon;
    }

    public void actionPerformed(ActionEvent e) {
    	// NOP
    }
}