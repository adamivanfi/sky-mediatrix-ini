package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import de.ityx.contex.impl.documentviewer.tagmatch.interfaces.ITagMatchView;
import de.ityx.contex.impl.documentviewer.tagmatch.view.TagMatchViewElement;
import de.ityx.contex.interfaces.extag.TagMatch;

import javax.swing.*;
import java.awt.*;


/**
 * The class extends the <code>TagMatchViewElement</code> by dynamically setting the colors.
 * 
 * @see de.ityx.contex.impl.documentviewer.tagmatch.view.TagMatchViewElement
 */
@SuppressWarnings("serial")
public class NBTagMatchViewElement extends TagMatchViewElement {

    /**
     * 
     */
    private Color       signalColor       = Color.YELLOW.darker();

    /**
     * 
     */
    private final Color signalColorLabel  = Color.DARK_GRAY;

    /**
     * 
     */
    private int         signalLevel       = 90;

    private long        warningLevel      = 45;

    private Color       warningColor      = Color.RED;

    private final Color warningColorLabel = Color.RED;

    /**
     * @param tagmatchPanel
     * @param tagMatch
     */
    public NBTagMatchViewElement(ITagMatchView tagmatchPanel, TagMatch tagMatch) {
        super(tagmatchPanel);
        init(tagMatch);
        setWarningLevel(signalLevel / 2);
        setColor();
    }

    /**
     * @param tagMatch
     */
    private void init(TagMatch tagMatch) {
        setLayout(new BorderLayout());
        this.tagmatch = tagMatch;
        this.setBackground(Color.white);

        this.label = new JLabel();
        this.label.setPreferredSize(new Dimension(120, 30));
        if (this.tagmatch.getCaption() == null || this.tagmatch.getCaption().trim().equals("")) {
            this.label.setText(this.tagmatch.getIdentifier() + ":");
        }
        else {
            this.label.setText(this.tagmatch.getCaption() + ":");
        }
        this.label.setFont(new Font(null, Font.BOLD, 12));
        this.label.setToolTipText(this.tagmatch.getIdentifier() + ":");
        add(this.label, BorderLayout.WEST);

        this.textField.setEditable(tagMatch.isEditable());
        this.textField.setMargin(new Insets(5, 5, 5, 10));
        this.textField.setText(this.tagmatch.getTagValue());
        this.textField.setName(this.tagmatch.getIdentifier());
        this.textField.setFont(new Font(null, Font.PLAIN, 14));
        add(this.textField);
        this.tagmatch = tagMatch;

        final JLabel label_1 = new JLabel();
        label_1.setPreferredSize(new Dimension(5, 5));
        add(label_1, BorderLayout.SOUTH);
        Action clearAction = new ClearTextFieldAction();
        textField.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put((KeyStroke) clearAction.getValue(Action.ACCELERATOR_KEY), clearAction.getValue(Action.NAME));
        textField.getActionMap().put(clearAction.getValue(Action.NAME), clearAction);
        textField.getDocument().addDocumentListener(this);
    }

    /**
     * 
     */
    private void setColor() {
        if (this.tagmatch != null) {
            final double quality = this.tagmatch.getConfidence();
			if (quality < this.warningLevel) {
                this.textField.setBackground(this.warningColor);
                this.label.setForeground(this.warningColorLabel);
            }
            else if (quality < this.signalLevel) {
                this.textField.setBackground(this.signalColor);
                this.label.setForeground(this.signalColorLabel);
            }
            else {
                this.textField.setBackground(Color.WHITE);
                this.label.setForeground(Color.BLACK);
            }
        }
    }

    /* (non-Javadoc)
     * @see de.ityx.contex.impl.documentviewer.tagmatch.view.ITagMatchViewElement#setTagMatch(de.ityx.contex.interfaces.extag.TagMatch)
     */
    @Override
    public void setTagMatch(TagMatch tm) {
        this.textField.setText(tm.getTagValue());
        setColor();
    }

    /**
     * @param newColor
     */
    public void setSignalColor(Color newColor) {
        this.signalColor = newColor;
        setColor();
    }

    /**
     * @param newValue
     */
    public void setSignalLevel(int newValue) {
        this.signalLevel = newValue;
        setColor();
    }

    /* (non-Javadoc)
     * @see de.ityx.contex.impl.documentviewer.tagmatch.view.TagMatchViewElement#setWarningColor(java.awt.Color)
     */
    @Override
    public void setWarningColor(Color newColor) {
        this.warningColor = newColor;
        setColor();
    }

    /* (non-Javadoc)
     * @see de.ityx.contex.impl.documentviewer.tagmatch.view.TagMatchViewElement#setWarningLevel(int)
     */
    @Override
    public void setWarningLevel(int newValue) {
        this.warningLevel = newValue;
        setColor();
    }

    public void setConfidence(double q) {
        this.tagmatch.setConfidence(q);
        setColor();
    }
}
