package de.ityx.sky.outbound.client;

import com.nttdata.de.sky.ityx.common.ExtendedNewMailFrame;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.StartAction;
import de.ityx.mediatrix.data.ProjectInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * overrides the default behavior of the service center frame.
 */
public class NewMailAction extends StartAction {
    private static final long serialVersionUID = 7415540589841293795L;

    public static int         projectId        = 0;

    public NewMailAction() {
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK));
        putValue(ACTION_COMMAND_KEY, "NewMail");
        putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
        putValue(NAME, "Neues Anschreiben");
        putValue(SHORT_DESCRIPTION, "Neues Anschreiben");
        putValue(LONG_DESCRIPTION, "Neues Anschreiben");

        putValue(SMALL_ICON, loadIcon("nav_newemail_menu.png"));
        putValue(BIG_ICON, loadIcon("nav_newemail.png"));
        putValue(BIG_ICON_OVER, loadIcon("nav_newemail_over.png"));
        
    }
    
    public static ImageIcon loadIcon(String classpath) {
    	ImageIcon icon = null;
    	InputStream resourceAsStream = NewMailAction.class.getResourceAsStream("/de/ityx/layout/resources/icon/nav_newemail_menu.png");
    	System.out.println(resourceAsStream);
    	try {
    		BufferedImage image = ImageIO.read(resourceAsStream);
    		icon = new ImageIcon(image);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return icon;
    }

    @Override
    public void setEnabled(boolean newValue) {
        boolean oldValue = this.enabled;

        if (oldValue != true) {
            this.enabled = true;
            firePropertyChange("enabled", Boolean.valueOf(oldValue), Boolean.valueOf(true));
        }
    }

    public void actionPerformed(ActionEvent e) {
        openNewMailFrame();

    }

    public void openNewMailFrame() {
        if (projectId == 0) {
            List<ProjectInfo> projectList = API.getClientAPI().getProjectAPI().loadProjectInfoList();
            for (ProjectInfo project : projectList) {
                if (project.getName().toLowerCase().indexOf("sky") > 0) {
                    projectId = project.getId();
                }
            }
        }
        ExtendedNewMailFrame newMail = new ExtendedNewMailFrame();
        newMail.setProjektID(projectId);
        newMail.setVisible(true);
        newMail.setDefaultFocus();
        newMail.getJMenuBar().setVisible(false);
        newMail.setTitle("Neues Anschreiben erstellen");

    }
}