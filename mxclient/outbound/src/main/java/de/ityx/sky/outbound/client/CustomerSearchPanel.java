package de.ityx.sky.outbound.client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.client.dialog.util.newmail.NewMailFrame;
import de.ityx.mediatrix.data.Customer;
import de.ityx.sky.outbound.data.Messages;

/**
 * searches for the customer and sets it into the frame.
 */
public class CustomerSearchPanel extends JPanel implements ActionListener,
		MouseListener, KeyListener {
	private static final String SEARCH = "search";
	private static final String CUSTOMER_ID = "customerid_field";
	private static final String CUSTOMER_LIST = "customer_list";

	private JTextField tf_customerId;
	private JTextField tf_contractId;
	private JButton jb_search = new JButton(Messages.getString("SearchPanel.1"));
	private JList jl_customers = new JList();

	private NewMailFrame parent;

	public CustomerSearchPanel(NewMailFrame parent) {
		this.parent = parent;
		setName(Messages.getString("SearchPanel.2"));
		init();
	}

	private void init() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0,
				Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel(Messages.getString("SearchPanel.3"));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		tf_customerId = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		add(tf_customerId, gbc_textField);
		tf_customerId.setColumns(10);
		tf_customerId.addKeyListener(this);
		tf_customerId.setName(CUSTOMER_ID);

		JLabel lblNewLabel_1 = new JLabel(Messages.getString("SearchPanel.4"));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		add(lblNewLabel_1, gbc_lblNewLabel_1);

		tf_contractId = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 1;
		add(tf_contractId, gbc_textField_1);
		tf_contractId.setColumns(10);

		jb_search.setActionCommand(SEARCH);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 2;
		add(jb_search, gbc_btnNewButton);
		jb_search.addActionListener(this);

		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.gridwidth = 2;
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 3;
		add(jl_customers, gbc_list);
		jl_customers.setCellRenderer(new DefaultListCellRenderer() {

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(
						list, value, index, isSelected, cellHasFocus);
				label.setPreferredSize(new Dimension(250, 115));
				label.setMinimumSize(new Dimension(120, 115));
				if (value instanceof Customer) {
					label.setText(htmlForKunde((Customer) value));
				}
				return label;
			}

		});
		jl_customers.addMouseListener(this);
		jl_customers.addKeyListener(this);
		jl_customers.setName(CUSTOMER_LIST);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals(SEARCH)) {
			String text = tf_customerId.getText();
			if (text.length() > 0) {
				List<Customer> customers = new ArrayList<Customer>();
				List<Object> parameter = new ArrayList<Object>();
				parameter.add(parent.getProjectId());
				parameter.add(text);
				List<Object> result = (List<Object>) API.getClientAPI()
						.getConnectionAPI()
						.sendServerEvent("ACTION_LOAD_CUSTOMER", parameter);
				if (result.size() > 0
						&& result.get(0).getClass().equals(Customer.class)) {
					Customer customer = (Customer) result.get(0);
					customers.add(customer);
				}
				jl_customers.setListData(customers.toArray());
			}
		}

	}

	private String htmlForKunde(Customer customer) {
		return "<html><body style='margin:3px' padding=0>" + "<table cellspacing=0 cellpadding=0 border='0'>" + "<tr>" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ "<td width='160'>" //$NON-NLS-1$
				+ customer.getFirstname().toUpperCase() + " " //$NON-NLS-1$
				+ customer.getName().toUpperCase() + "</td>" //$NON-NLS-1$
				+ "</tr><tr>" //$NON-NLS-1$
				+ "<td>" //$NON-NLS-1$
				+ customer.getId() + "</td>" //$NON-NLS-1$
				+ "</tr><tr>" //$NON-NLS-1$
				+ "<td>" //$NON-NLS-1$
				+ customer.getPostcode() + " " //$NON-NLS-1$
				+ customer.getCity() + "</td>" //$NON-NLS-1$
				+ "</tr><tr>" //$NON-NLS-1$
				+ "<td>" //$NON-NLS-1$
				+ customer.getCountry() + "</td>" //$NON-NLS-1$
				+ "</tr><tr>" //$NON-NLS-1$
				+ "<td>" //$NON-NLS-1$
				+ customer.getEmail() + "</td>" //$NON-NLS-1$
				+ "</tr><tr>" //$NON-NLS-1$
				+ "</table>" + "</body></html>"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			addCustomer();
		}
	}

	private void addCustomer() {
		Customer customer = (Customer) jl_customers.getSelectedValue();
		parent.setTo(customer.getEmail());
	}

	@Override
	public void mousePressed(MouseEvent paramMouseEvent) {
	}

	@Override
	public void mouseReleased(MouseEvent paramMouseEvent) {
	}

	@Override
	public void mouseEntered(MouseEvent paramMouseEvent) {
	}

	@Override
	public void mouseExited(MouseEvent paramMouseEvent) {
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getComponent().getName().equals(CUSTOMER_ID)
				&& event.getKeyCode() == KeyEvent.VK_ENTER) {
			jb_search.doClick();
			jb_search.transferFocus();
			if (jl_customers.getModel().getSize() > 0) {
				jl_customers.setSelectedIndex(0);
			}
		} else if (event.getComponent().getName().equals(CUSTOMER_LIST)
				&& event.getKeyCode() == KeyEvent.VK_ENTER) {
			addCustomer();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
