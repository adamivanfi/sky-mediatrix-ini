package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.contex.data.match.Request;
import de.ityx.contex.data.match.Response;
import de.ityx.contex.data.match.Row;
import de.ityx.contex.impl.document.CDocumentContainer;
import de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IClassificationValidationCallbackListener;
import de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer;
import de.ityx.contex.impl.documentviewer.tagmatch.interfaces.ITagMatchViewElement;
import de.ityx.contex.impl.documentviewer.tagmatch.view.ClassificationValidationView;
import de.ityx.contex.impl.documentviewer.tagmatch.view.TagMatchComboBoxElement;
import de.ityx.contex.impl.documentviewer.tagmatch.view.TagMatchViewElement;
import de.ityx.contex.interfaces.document.CBase;
import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.document.CPage;
import de.ityx.contex.interfaces.document.IDocumentViewer;
import de.ityx.contex.interfaces.extag.TagMatch;
import de.ityx.contex.interfaces.match.IFuzzyMatcherEngine;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.IClientAPI;
import de.ityx.mediatrix.client.dialog.Start;
import de.ityx.mediatrix.client.dialog.objects.question.TagMatchListener;
import de.ityx.mediatrix.client.util.Repository;
import de.ityx.mediatrix.data.OperatorLogRecord;
import de.ityx.mediatrix.data.Project;
import de.ityx.mediatrix.data.Question;
import de.ityx.tools.ccbox.CCBoxModel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ValidationFormtypePanel_Sky_Lager_EZM extends JPanel implements IFormtypeViewer, KeyListener, ActionListener, ListSelectionListener {
	// Maps TagMatch identifiers to FuzzySearch column names.
	private static Map<String, String>	tagNames;

	// Contains the names of the view elements that should occur in the first
	// column.
	private static Set<String>			firstColumnTags;

	// Welche Attribute lösen Prefix-Suche aus???
	private static Set<String>			tagNames4PrefixSearch;

	private static Boolean				lastOp	= false;

	// Does some initialization.
	static {
		// Instantiates a linked Map because insertion order matters for layout.
		ValidationFormtypePanel_Sky_Lager_EZM.tagNames = new LinkedHashMap<String, String>();
		ValidationFormtypePanel_Sky_Lager_EZM.tagNames.put(TagMatchDefinitions.CUSTOMER_ID, "CUSTOMER_ID");

		ValidationFormtypePanel_Sky_Lager_EZM.tagNames.put(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, "CONTRACT_ID");

		ValidationFormtypePanel_Sky_Lager_EZM.tagNames.put(TagMatchDefinitions.TAGMATCH_SMARTCARD_NUMBER, "SERIAL_NUMBER");

		ValidationFormtypePanel_Sky_Lager_EZM.tagNames.put(TagMatchDefinitions.CUSTOMER_FIRST_NAME, "FIRST_NAME");

		ValidationFormtypePanel_Sky_Lager_EZM.tagNames.put(TagMatchDefinitions.CUSTOMER_LAST_NAME, "LAST_NAME");

		ValidationFormtypePanel_Sky_Lager_EZM.tagNames.put(TagMatchDefinitions.CUSTOMER_STREET, "STREET");

		ValidationFormtypePanel_Sky_Lager_EZM.tagNames.put(TagMatchDefinitions.CUSTOMER_ZIP_CODE, "ZIPCODE");

		ValidationFormtypePanel_Sky_Lager_EZM.tagNames.put(TagMatchDefinitions.CUSTOMER_CITY, "CITY");

		ValidationFormtypePanel_Sky_Lager_EZM.tagNames.put(TagMatchDefinitions.TAGMATCH_EMAIL, "EMAIL_ADDRESS");

		ValidationFormtypePanel_Sky_Lager_EZM.tagNames.put(TagMatchDefinitions.TAGMATCH_BANK, "ACCOUNT_NUMBER");

		// ValidationFormtypePanel_Sky.tagNames.put(
		// TagMatchDefinitions.META_WASHING_MACHINE, "WASH_MACHINE");

		ValidationFormtypePanel_Sky_Lager_EZM.firstColumnTags = new TreeSet<String>();
		// ValidationFormtypePanel_Sky.firstColumnTags
		// .add(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER);
		// ValidationFormtypePanel_Sky.firstColumnTags
		// .add(TagMatchDefinitions.CUSTOMER_ID);
		// ValidationFormtypePanel_Sky.firstColumnTags
		// .add(TagMatchDefinitions.TAGMATCH_SMARTCARD_NUMBER);
		// ValidationFormtypePanel_Sky.firstColumnTags
		// .add(TagMatchDefinitions.CUSTOMER_FIRST_NAME);
		// ValidationFormtypePanel_Sky.firstColumnTags
		// .add(TagMatchDefinitions.TAGMATCH_SMARTCARD_NUMBER);
		// ValidationFormtypePanel_Sky.firstColumnTags
		// .add(TagMatchDefinitions.CUSTOMER_ZIP_CODE);
		// ValidationFormtypePanel_Sky.firstColumnTags
		// .add(TagMatchDefinitions.TAGMATCH_EMAIL);

		ValidationFormtypePanel_Sky_Lager_EZM.firstColumnTags.add(TagMatchDefinitions.CUSTOMER_ID);
		ValidationFormtypePanel_Sky_Lager_EZM.firstColumnTags.add(TagMatchDefinitions.TAGMATCH_SMARTCARD_NUMBER);
		ValidationFormtypePanel_Sky_Lager_EZM.firstColumnTags.add(TagMatchDefinitions.CUSTOMER_FIRST_NAME);
		ValidationFormtypePanel_Sky_Lager_EZM.firstColumnTags.add(TagMatchDefinitions.CUSTOMER_STREET);
		ValidationFormtypePanel_Sky_Lager_EZM.firstColumnTags.add(TagMatchDefinitions.CUSTOMER_ZIP_CODE);
		ValidationFormtypePanel_Sky_Lager_EZM.firstColumnTags.add(TagMatchDefinitions.TAGMATCH_EMAIL);
		// ValidationFormtypePanel_Sky.firstColumnTags
		// .add(TagMatchDefinitions.META_WASHING_MACHINE);

		ValidationFormtypePanel_Sky_Lager_EZM.tagNames4PrefixSearch = new TreeSet<String>();
		//ValidationFormtypePanel_Sky_Lager_EZM.tagNames4PrefixSearch.add(TagMatchDefinitions.TAGMATCH_EMAIL);
		ValidationFormtypePanel_Sky_Lager_EZM.tagNames4PrefixSearch.add(TagMatchDefinitions.CUSTOMER_FIRST_NAME);
		ValidationFormtypePanel_Sky_Lager_EZM.tagNames4PrefixSearch.add(TagMatchDefinitions.CUSTOMER_LAST_NAME);
		ValidationFormtypePanel_Sky_Lager_EZM.tagNames4PrefixSearch.add(TagMatchDefinitions.CUSTOMER_STREET);
		ValidationFormtypePanel_Sky_Lager_EZM.tagNames4PrefixSearch.add(TagMatchDefinitions.CUSTOMER_CITY);

	}

	/**
	 * Defines the Action that is performed for fuzzy search.
	 */
	@SuppressWarnings("serial")
	public class FuzzySearchAction extends AbstractAction {

		/**
		 * The default constructor defines the keyboard shortcut, the action
		 * command and the action name.
		 */
		public FuzzySearchAction() {
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK));
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
			this.putValue(Action.ACTION_COMMAND_KEY, "Search"); //$NON-NLS-1$
			this.putValue(Action.NAME, "Search");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			SkyLogger.getClientLogger().debug("FuzzySearchAction: " + e.getSource());
			final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			final Container parent = focusOwner.getParent();
			if (!(parent instanceof ITagMatchViewElement)) {
				ValidationFormtypePanel_Sky_Lager_EZM.this.lbStatus.setVisible(true);
				ValidationFormtypePanel_Sky_Lager_EZM.this.lbStatus.setText(Messages.getString("ValidationFormtypePanel.1"));
				return;
			}

			executeFuzzySearch();
			ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent = (JComponent) focusOwner;
		}

		// Executes the search operation.
		private void executeFuzzySearch() {
			final Integer projektId = ValidationFormtypePanel_Sky_Lager_EZM.this.question.getProjectId();
			List<Object> parameter = new ArrayList<Object>();
			parameter.add(projektId);
			Request request = new Request();
			for (String tagIdentifier : ValidationFormtypePanel_Sky_Lager_EZM.viewElements.keySet()) {
				TagMatchViewElement viewElement = ValidationFormtypePanel_Sky_Lager_EZM.viewElements.get(tagIdentifier);
				if (viewElement != null) {
					String textValue = viewElement.getTextValue();
					// Only searches for something.
					if (textValue != null && textValue.length() > 0) {
						int mode = IFuzzyMatcherEngine.CANDIDATES_FUZZY;
						if (ValidationFormtypePanel_Sky_Lager_EZM.tagNames4PrefixSearch.contains(tagIdentifier)) {
							mode = IFuzzyMatcherEngine.CANDIDATES_PREFIX;
						}
						request.addColumn(ValidationFormtypePanel_Sky_Lager_EZM.tagNames.get(tagIdentifier), textValue, true, 1, mode);
					}
				}
			}

			parameter.add(request);
			List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_FUZZY_SEARCH.name(), parameter);
			if (result.size() > 0 && result.get(0) instanceof Response) {
				Response filteredresults= filterResults((Response) result.get(0));
				setFuzzyResponseTable(filteredresults);
			}
		}


		public Response filterResults(Response input){

			Response output=new Response(input.getNamespace(), input.getColumnCount());
			int i=0;
			for (String col:input.getColumnNames()){
				output.setColumnName(i,col);
				i++;
			}
			int maxRecords=10;
			double maxDistance=0.10;

			i=0;
			double bestCaseError=1.0;
			for(Row row:input.getRows()){
				if (row.getError()<=bestCaseError){
					bestCaseError=row.getError();
					output.addRow(row);
					SkyLogger.getClientLogger().debug("filter MI-FuzzyResults:BestCase-Match:"+bestCaseError+"  "+ row.getField(0)+":"+ row.getField(1)+":"+ row.getField(2)+":"+ row.getField(3));
				}else{ //(row.getError()>bestCaseError) {
					if (i<maxRecords &&  row.getError()< (bestCaseError+ maxDistance)){
						output.addRow(row);
						SkyLogger.getClientLogger().debug("filter MI-FuzzyResults:StandardCase-Match:"+row.getError()+"<"+(bestCaseError+ maxDistance)+" "+i+"  "+ row.getField(0)+":"+ row.getField(1)+":"+ row.getField(2)+":"+ row.getField(3));
					}else{
						SkyLogger.getClientLogger().debug("filter MI-FuzzyResults:OutOfScope-Match:"+row.getError()+"<"+(bestCaseError+ maxDistance)+" "+i+"  "+ row.getField(0)+":"+ row.getField(1)+":"+ row.getField(2)+":"+ row.getField(3));
					}
				}
				i++;
			}

			return output;
		}

		// Sets the search result.
		private void setFuzzyResponseTable(Response result) {
			ValidationFormtypePanel_Sky_Lager_EZM.this.fuzzyResponse = result;
			setTableModel(true);
			if (ValidationFormtypePanel_Sky_Lager_EZM.this.fuzzyResponse.getRowCount() == 0) {
				ValidationFormtypePanel_Sky_Lager_EZM.this.lbStatus.setVisible(true);
				ValidationFormtypePanel_Sky_Lager_EZM.this.lbStatus.setText(Messages.getString("ValidationFormtypePanel.160"));
			} else if (ValidationFormtypePanel_Sky_Lager_EZM.this.fuzzyResponse.getRowCount() == 1) {
				ValidationFormtypePanel_Sky_Lager_EZM.this.lbStatus.setVisible(false);
			} else if (ValidationFormtypePanel_Sky_Lager_EZM.this.fuzzyResponse.getRowCount() > 1) {
				ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.requestFocus();
				final int rowCount = ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.getRowCount();
				if (rowCount > 0 && ValidationFormtypePanel_Sky_Lager_EZM.this.fuzzyResponse.getRow(0).getError() == 0.0 && (rowCount == 1 || ValidationFormtypePanel_Sky_Lager_EZM.this.fuzzyResponse.getRow(1).getError() > 0.0)) {
					ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.getSelectionModel().setSelectionInterval(0, 0);
					ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.getSelectionModel().setLeadSelectionIndex(0);
					ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
					valueChanged(new ListSelectionEvent(ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults, 0, 0, true));
				}
				ValidationFormtypePanel_Sky_Lager_EZM.this.lbStatus.setVisible(false);
			}
		}
	}

	/**
	 * Extends a TableCellRenderer by setting the background color of a table
	 * row depending on the double value of its first column.
	 */
	public class ExactMatchHighlighter implements TableCellRenderer {

		// Renders the cells.
		TableCellRenderer	superRenderer;

		/**
		 * The constructor sets the <code>defaultRenderer</code> as delegate.
		 * 
		 * @param defaultRenderer
		 *            The delegate renderer.
		 */
		public ExactMatchHighlighter(TableCellRenderer defaultRenderer) {
			this.superRenderer = defaultRenderer;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent
		 * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			TableModel model = table.getModel();
			int numOfCols = model.getColumnCount();
			Object quality = model.getValueAt(row, 0);
			Component tableCellRendererComponent = this.superRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			Color background = tableCellRendererComponent.getBackground();
			if (!isSelected) {
				if (quality instanceof Double && (Double) quality == 100.0) {
					background = Color.GREEN.brighter();
				} else {
					background = Color.YELLOW;
				}
			}

			tableCellRendererComponent.setBackground(background);
			return tableCellRendererComponent;
		}
	}

	/**
	 * Defines the Action that sets the input focus on the result table.
	 */
	@SuppressWarnings("serial")
	public class FocusTableAction extends AbstractAction {

		/**
		 * The default constructor defines the keyboard shortcut, the action
		 * command and the action name.
		 */
		public FocusTableAction() {
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
			this.putValue(Action.ACTION_COMMAND_KEY, "FocusTable"); //$NON-NLS-1$
			this.putValue(Action.NAME, "FocusTable");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.isFocusOwner()) {
				ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.requestFocus();
				if (ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.getRowCount() > 0) {
					ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.getSelectionModel().setSelectionInterval(0, 0);
				}
			}
		}
	}

	/**
	 * Defines the Action that checks if saving the document is possible and
	 * executes.
	 */
	@SuppressWarnings("serial")
	public class SaveMeAction extends AbstractAction {

		// Performs saving of the document.
		private final Action	saveAction;

		/**
		 * This constructor defines the keyboard shortcut, the action command
		 * and the action name. It takes an action as parameter that should be
		 * performed after successfully performing this action.
		 * 
		 * @param saveAction
		 *            The save action to be performed.
		 */
		public SaveMeAction(Action saveAction) {
			this.putValue(Action.ACTION_COMMAND_KEY, "SaveMe"); //$NON-NLS-1$
			this.putValue(Action.NAME, "SaveMe");
			this.saveAction = saveAction;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			final CDocument doc = ValidationFormtypePanel_Sky_Lager_EZM.this.document.getDocument(0);

			// ExtractionValidationView.this.startConsumeFocusEvents();
			// Das aktuell eingebene Feld muss noch übertragen werden, dazu
			// versenden wir ein FocusLost und deaktivieren den Listener:
			Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			FocusEvent fe = new FocusEvent(owner, FocusEvent.FOCUS_LOST);
			FocusListener[] focusListeners = owner.getFocusListeners();
			for (FocusListener focusListener : focusListeners) {
				focusListener.focusLost(fe);
				owner.removeFocusListener(focusListener);
			}
			final TagMatch tagMatch = ValidationFormtypePanel_Sky_Lager_EZM.this.document.getTags().get(0);

			final TagMatch customer = tagMatch.getTagMatch(TagMatchDefinitions.CUSTOMER_ID);

			if (customer == null || customer.getTagValue().trim().length() == 0 || !checkNumberInput(customer)) {
				JOptionPane.showMessageDialog(null, "Ungültige Kundennummer: " + customer);
				return;
			}
			final String formtype = doc.getFormtype();
			
			// Needed for validation of document formtype in enrichment.
			doc.setNote(TagMatchDefinitions.MANUAL_FORMTYPE, formtype);
			doc.setNote(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID, String.valueOf(question.getId()));

			TagMatch topLevel = new TagMatch("ManualValidation");
			List<TagMatch> tmList = new ArrayList<TagMatch>();
			tmList.add(topLevel);

			// Needed for validation of document formtype in enrichment.
			topLevel.add(new TagMatch(TagMatchDefinitions.MANUAL_FORMTYPE, doc.getFormtype()));
			topLevel.add(new TagMatch(TagMatchDefinitions.DMS_MANUAL_INDEXING_QID, String.valueOf(question.getId())));
			
			topLevel.add(customer);
			final TagMatch contract = tagMatch.getTagMatch(TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER);
			if (contract != null && contract.getTagValue().trim().length() > 0 && checkNumberInput(contract)) {
				topLevel.add(contract);
			} else {
				JOptionPane.showMessageDialog(null, "Ungültige Vertragsnummer");
				return;
			}

			ValidationFormtypePanel_Sky_Lager_EZM.this.document.setTags(new ArrayList<TagMatch>());
			doc.setTags(tmList);

			Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
			IClientAPI clientAPI = API.getClientAPI();
			clientAPI.getQuestionAPI().protocolLog(question, "ManualValidation_EZM:" + " DocumentID=" + doc.getNote(TagMatchDefinitions.DOCUMENT_ID) + ", CustomerID=" + customer.getTagValue() + ", ContractID=" + contract.getTagValue() + ", Formtype=" + doc.getFormtype(), OperatorLogRecord.ACTION_CHECKLIST);
                        if (doc.getNote(TagMatchDefinitions.DOCUMENT_ID)!=null){
                            question.setDocId((String) doc.getNote(TagMatchDefinitions.DOCUMENT_ID));
                        }
			this.saveAction.actionPerformed(e);
			if (getLastOp()) {
				Repository.putObject(Repository.LAST_OPERATOR, Repository.LAST_OPERATOR);
			}
		}

		private boolean checkNumberInput(TagMatch customer) {
			final Integer projektId = ValidationFormtypePanel_Sky_Lager_EZM.this.question.getProjectId();
			String textValue = customer.getTagValue();
			if (textValue != null && textValue.length() > 0) {
				String trim = textValue.trim();
				trim=trim.replaceAll("\\.\\s_","");
				if (!trim.equals(textValue)) {
					textValue = trim;
					customer.setTagValue(trim);
				}
				if (!textValue.matches("\\d+")){
					return false;
				}
				List<Object> parameter = new ArrayList<Object>();
				parameter.add(projektId);
				Request request = new Request();
				int mode = IFuzzyMatcherEngine.CANDIDATES_EXACT;
				request.addColumn(ValidationFormtypePanel_Sky_Lager_EZM.tagNames.get(customer.getIdentifier()), textValue, true, 0, mode);
				parameter.add(request);
				List<Object> result = (List<Object>) API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_FUZZY_SEARCH.name(), parameter);
				if (result != null && result.get(0) instanceof Response) {
					Response res = (Response) result.get(0);
					if (res.getRowCount() > 0) {
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * Defines the Action that resets the search result table.
	 */
	@SuppressWarnings("serial")
	public class SoftUnfocusTableAction extends AbstractAction {

		/**
		 * The default constructor defines the keyboard shortcut, the action
		 * command and the action name.
		 */
		public SoftUnfocusTableAction() {
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK));
			this.putValue(Action.ACTION_COMMAND_KEY, "SoftUnfocusTable"); //$NON-NLS-1$
			this.putValue(Action.NAME, "SoftUnfocusTable");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.isFocusOwner()) {
				System.out.println("LastFocused:" + ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent);
				ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent.requestFocus();
				ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.setModel(new DefaultTableModel());
			}
		}
	}

	/**
	 * Defines the Action that applies the selected row to the view elements.
	 */
	@SuppressWarnings("serial")
	public class UnfocusTableAction extends AbstractAction {

		/**
		 * The default constructor defines the keyboard shortcut, the action
		 * command and the action name.
		 */
		public UnfocusTableAction() {
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
			this.putValue(Action.ACTION_COMMAND_KEY, "UnfocusTable"); //$NON-NLS-1$
			this.putValue(Action.NAME, "UnfocusTable");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.isFocusOwner()) {
				// Die selektierte Zeile in Attribut 1 und Adressblock
				// übertragen:
				valueChanged(new ListSelectionEvent(ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults, ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.getSelectedRow(),
						ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.getSelectedRow(), true));
				((DefaultTableModel) ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.getModel()).setRowCount(0);
			}
		}
	}

	// Maps TagMatch identifiers to view elements.
	private static final Map<String, TagMatchViewElement>	viewElements	= new HashMap<String, TagMatchViewElement>();

	/**
	 * Defines the Action that selects the next page of the document.
	 */
	@SuppressWarnings("serial")
	public class NextPageAction extends AbstractAction {

		/**
		 * The default constructor defines the keyboard shortcut, the action
		 * command and the action name.
		 */
		public NextPageAction() {
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0));
			this.putValue(Action.ACTION_COMMAND_KEY, "PageDown"); //$NON-NLS-1$
			this.putValue(Action.NAME, "PageDown");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			final int selectedpage = ValidationFormtypePanel_Sky_Lager_EZM.this.view.getDocumentViewer().getSelectedpage();
			if (selectedpage + 1 < ValidationFormtypePanel_Sky_Lager_EZM.this.view.getDocumentViewer().getDocument().getPageCount()) {
				ValidationFormtypePanel_Sky_Lager_EZM.this.requestFocus();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						ValidationFormtypePanel_Sky_Lager_EZM.this.view.getDocumentViewer().selectPage(selectedpage + 1);
					}
				});
			}
		}
	}

	/**
	 * Defines the Action that selects the previous page of the document.
	 */
	@SuppressWarnings("serial")
	public class PreviousPageAction extends AbstractAction {

		/**
		 * The default constructor defines the keyboard shortcut, the action
		 * command and the action name.
		 */
		public PreviousPageAction() {
			this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0));
			this.putValue(Action.ACTION_COMMAND_KEY, "PageUp"); //$NON-NLS-1$
			this.putValue(Action.NAME, "PageUp");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.AbstractAction#isEnabled()
		 */
		@Override
		public boolean isEnabled() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			final int selectedpage = ValidationFormtypePanel_Sky_Lager_EZM.this.view.getDocumentViewer().getSelectedpage();
			if (selectedpage > 0) {
				ValidationFormtypePanel_Sky_Lager_EZM.this.requestFocus();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						ValidationFormtypePanel_Sky_Lager_EZM.this.view.getDocumentViewer().selectPage(selectedpage - 1);
					}
				});
			}
		}
	}

	/**
	 * Serial UID
	 */
	private static final long						serialVersionUID	= -4759885372794324045L;

	/**
	 * Defines the warning color of the view elements.
	 */
	public static final Color						WARNING_COLOR		= new Color(255, 128, 128);

	// Defines the warning level of the view elements.
	protected static final int						WARNINGLEVEL		= 50;

	/**
	 * Defines the signal color of the view elements.
	 */
	public static final Color						SIGNAL_COLOR		= Color.YELLOW;

	// Defines the signal level of the view elements.
	protected static final int						SIGNALLEVEL			= 90;

	// Contains the selectable formtypes.
	private Map<String, Vector<String>>				validationData;

	// References the parent component.
	private ClassificationValidationView<CDocument>	view;

	// Selects the formtype of an document.
	private TagMatchComboBoxElement					inputBox;

	// Contains the mediatrix project id of the question.
	private int										projektId;

	// Contains the metainformation document of the question.
	private CDocumentContainer<CDocument>			document;

	// Layouts this Panel.
	private GridBagConstraints						gbc;

	// Defines the font for the view elements.
	private Font									fontForTextFields;

	// References the question to be validated.
	private Question								question;

	// Holds the responses of the fuzzy search.
	private Response								fuzzyResponse;

	// Contains column names of the result table.
	Vector<String>									columnNames			= new Vector<String>();

	// Shows the fuzzy matcher search result.
	protected JXTable								tbSearchResults;

	// Shows the status of the validation.
	protected JLabel								lbStatus;

	// Contains the result table.
	protected JScrollPane							spSearchResult		= new JScrollPane();

	/**
	 * Merkt sich, welche Componente als letztes fokusiert war
	 */
	static protected JComponent						lastFocusComponent;

	// protected Color lastBackGround = Color.WHITE;
	// private final boolean formtypeInitialized = false;

	// Contains the TagMatch view elements.
	private JPanel									tagPanel;

	// Referneces the document containing the selected page.
	private CDocument								documentForPage;

	/**
	 * The default constructor.
	 */
	public ValidationFormtypePanel_Sky_Lager_EZM() {
		SkyLogger.getClientLogger().info("ValidationFormtypePanel");
	}

	/**
	 * Initializes this component.
	 */
	public void initView() {
		initDocument((CDocumentContainer<CDocument>) this.view.getDocument());
		Project project = API.getClientAPI().getProjectAPI().load(this.question.getProjectId());
		this.view.setTagMatchListener(new TagMatchListener(this.question, project));
		initDocumentView();
	}

	// Initializes the search result table.
	private void initFuzzyTable() {
		// this.gbc.gridy = 2;
		this.gbc.weightx = 1.0;
		this.gbc.anchor = GridBagConstraints.NORTHEAST;

		JPanel separator = new JPanel();
		separator.setBackground(Color.BLACK);
		separator.setPreferredSize(new Dimension(5, 5));
		separator.setMinimumSize(new Dimension(5, 5));
		this.gbc.gridy += 1;
		add(separator, this.gbc);
		separator = new JPanel();
		separator.setBackground(Color.WHITE);
		separator.setPreferredSize(new Dimension(5, 5));
		separator.setMinimumSize(new Dimension(5, 5));
		this.gbc.gridy += 1;
		add(separator, this.gbc);

		this.lbStatus = new JLabel("STATUS TEXT");
		this.lbStatus.setFont(this.lbStatus.getFont().deriveFont(17.0F));
		this.lbStatus.setBackground(Color.RED);
		this.lbStatus.setOpaque(true);
		this.lbStatus.setVisible(false);
		this.gbc.gridy += 1;
		add(this.lbStatus, this.gbc);

		this.tbSearchResults = new JXTable();
		this.tbSearchResults.setEditable(false);
		this.tbSearchResults.setSortable(false);
		this.tbSearchResults.getTableHeader().setReorderingAllowed(false);
		TableCellRenderer defaultRenderer = this.tbSearchResults.getDefaultRenderer(String.class);
		this.tbSearchResults.setDefaultRenderer(Object.class, new ExactMatchHighlighter(defaultRenderer));
		this.tbSearchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tbSearchResults.getSelectionModel().addListSelectionListener(this);
		this.tbSearchResults.setHorizontalScrollEnabled(true);

		this.spSearchResult.getViewport().add(this.tbSearchResults);
		this.gbc.gridy++;
		this.gbc.weighty = 1.0;
		this.gbc.fill = GridBagConstraints.BOTH;
		add(this.spSearchResult, this.gbc);

		initActionMapForSearchResult();
	}

	// Initializes the view with the first document.
	private void initDocumentView() {
		this.documentForPage = this.document.getDocumentForPage(0);
		this.fontForTextFields = new JLabel().getFont().deriveFont(14.0f);
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		this.gbc = new GridBagConstraints();
		this.gbc.ipady = 10;
		initButtonPanel();
		initFormTypeBox();
		this.tagPanel = new JPanel(new GridBagLayout());
		tagPanel.setBackground(getBackground());
		initTagMatchPanel();
		initFuzzyTable();
	}

	// Initializes the button panel.
	protected void initButtonPanel() {
		this.gbc.gridx = 0;
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setBackground(this.getBackground());
		GridBagConstraints gbc = new GridBagConstraints();
		initCleanTagsButton(buttonPanel, gbc);
		add(buttonPanel, this.gbc);
	}

	// Initializes the selection of formtypes.
	private void initFormTypeBox() {
		this.gbc.anchor = GridBagConstraints.EAST;
		this.gbc.gridy = 1;
		this.gbc.fill = GridBagConstraints.HORIZONTAL;
		this.gbc.weightx = 1.0;
		this.gbc.gridx = 0;
		this.gbc.gridwidth = 1;
		final String formtype = this.document.getDocumentForPage(0).getFormtype();
		final TagMatch tagMatch = new TagMatch("Form Type", formtype);
		// tagMatch.setQuality(formtype.equals(FormTypes.DEFAULT_FORMTYPE) ? 0 :
		// 100);
		this.inputBox = new TagMatchComboBoxElement(this.view, tagMatch);
		this.inputBox.getCbComboBox().setEditable(false);
		this.inputBox.setFont(this.fontForTextFields);
		CCBoxModel<String> comboBoxModel = new CCBoxModel<String>(this.validationData.get(TagMatchDefinitions.FORM_TYPE_CATEGORY));
		comboBoxModel.setStrict(true);
		this.inputBox.getCbComboBox().setModel(comboBoxModel);
		this.inputBox.addKeyListener(this);
		this.inputBox.addActionListener(this);
		add(this.inputBox, this.gbc);
	}

	// Initializes the TagMatch view elements.
	private void initTagMatchPanel() {
		this.tagPanel.removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		gbc.anchor = GridBagConstraints.EAST;

		// Instantiates a linked Set because insertion order matters for layout.
		Set<TagMatch> tagmatches = new LinkedHashSet<TagMatch>();
		for (String tagIdentifier : tagNames.keySet()) {
			tagmatches.add(new TagMatch(tagIdentifier));
		}
		int labelWidth = calculateLabelWidth(tagmatches);
		this.inputBox.setLabelWidth(labelWidth);

		// Searches for customer grouping tag.
		List<TagMatch> matches = new ArrayList<TagMatch>();
		TagMatch match = null;
		for (TagMatch tm : this.document.getTags()) {
			if (tm.getIdentifier().equals("customer")) {
				match = tm;
				break;
			}
		}

		// Creates a customer grouping tag.
		if (match == null) {
			match = new TagMatch("customer");
			for (TagMatch tm : this.document.getTags()) {
				match.add(tm);
			}
		}

		// Sets the customer grouping tag as the documents only tag.
		matches.add(match);
		this.document.setTags(matches);
		for (CDocument doc : this.document.getDocuments()) {
			doc.setTags(new ArrayList<TagMatch>());
			for (CPage page : doc.getPages()) {
				page.setTags(new ArrayList<TagMatch>());
			}
		}

		// Creates the view elements.
		int gridy = 0;
		ValidationFormtypePanel_Sky_Lager_EZM.viewElements.clear();
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.5;
		gbc.gridwidth = 1;
		for (TagMatch tm : tagmatches) {
			final String identifier = tm.getIdentifier();
			TagMatch tagMatch = match.getTagMatch(identifier);
			if (tagMatch == null) {
				match.add(tm);
				tagMatch = tm;
				tagMatch.setConfidence(0);
			} else if (tagMatch.getTagValue() == null) {
				tagMatch.setTagValue("");
				tagMatch.setConfidence(0);
			} else if (tagMatch.getTagValue().trim().length() > 0) {
				tagMatch.setConfidence(100L);
			}
			tagMatch.setCaption(TagMatchDefinitions.getCaption(identifier));
			TagMatchViewElement tagView = new NBTagMatchViewElement(this.view, tagMatch);
			tagView.setFont(this.fontForTextFields);
			tagView.getTextField().setEditable(true);
			if (this.view instanceof FocusListener) {
				tagView.addFocusListener((FocusListener) this.view);
			}
			tagView.addKeyListener(this);
			tagView.setBackground(Color.WHITE);
			tagView.setLabelWidth(labelWidth);
			if (ValidationFormtypePanel_Sky_Lager_EZM.firstColumnTags.contains(identifier)) {
				gbc.gridx = 0;
				gridy++;
				gbc.gridy = gridy;
			} else {
				gbc.gridx = 1;
				gbc.gridy = gridy;
			}
			ValidationFormtypePanel_Sky_Lager_EZM.viewElements.put(identifier, tagView);
			this.tagPanel.add(tagView, gbc);
		}

		this.gbc.fill = GridBagConstraints.HORIZONTAL;
		this.gbc.gridy = 2;
		add(this.tagPanel, this.gbc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer
	 * #getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer
	 * #getView()
	 */
	@Override
	public ClassificationValidationView getView() {
		return this.view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer
	 * #selectNextFormType()
	 */
	@Override
	public void selectNextFormType() {
		// empty
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer
	 * #selectPreviousFormType()
	 */
	@Override
	public void selectPreviousFormType() {
		// empty

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer
	 * #setActualFormtype(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setActualFormtype(String formType) {
		if (formType == null || formType.length() == 0) {
			formType = TagMatchDefinitions.DEFAULT_FORMTYPE;
		}
		initKeyCodes();
		if (!formType.equals(this.inputBox.getCbComboBox().getSelectedItem())) {
			this.inputBox.getCbComboBox().setSelectedItem(formType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer
	 * #setCallbackListener(java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setCallbackListener(Collection<? extends IClassificationValidationCallbackListener> list) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer
	 * #setFormTypeListe(java.lang.String[])
	 */
	@Override
	public void setFormTypeListe(String[] formTypeList) {
		// empty

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer
	 * #setView(de.ityx.contex.impl.documentviewer.tagmatch.view.
	 * ClassificationValidationView)
	 */
	@Override
	public void setView(ClassificationValidationView view) {
		this.view = view;
		initView();
	}

	// Initializes the document of the question to be validated.
	private void initDocument(CDocumentContainer<CDocument> cDocumentContainer) {
		this.document = cDocumentContainer;
		this.question = (Question) this.document.getMetainformation("MTX-Question");
		if (this.question == null) {
			throw new NullPointerException("MTX-Question is null");
		}
		this.projektId = this.question.getProjectId();
		readValidationData();
	}

	/**
	 * Setzt im aktuell selektierten Dokument den Formtype mit dem aktuellen Wert der ComboBox.
	 */
	@SuppressWarnings("unchecked")
	private void setFormtypeInDocument() {
		CDocumentContainer<CDocument> document = getView().getDocument();
		this.documentForPage = document.getDocumentForPage(getView().selectedIcon);
		String selectedFormType = (String) this.inputBox.getCbComboBox().getSelectedItem();
		String formtype = this.documentForPage.getFormtype();
		if (!formtype.equals(selectedFormType)) {
			setActualFormtype(selectedFormType);
			this.documentForPage.setFormtype(selectedFormType);
			initTagMatchPanel();
			this.tagPanel.updateUI();
			this.view.setFormType(selectedFormType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer #setFormType(java.lang.String, int, int, int, int, int)
	 * @see
	 * de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer
	 * #setFormType(java.lang.String, int, int, int, int, int)
	 */
	@Override
	public boolean setFormType(String newValue, int page, int x1, int x2, int y1, int y2) {
		this.inputBox.getCbComboBox().setSelectedItem(newValue);
		setFormtypeInDocument();
		return false;
	}

	// Read the selectable formtypes.
	@SuppressWarnings("unchecked")
	private void readValidationData() {
		SkyLogger.getClientLogger().info(getClass().getName() + " readValidationData");
		this.validationData = new TreeMap<String, Vector<String>>();
		Vector<String> keywords = ClientOutboundRule.loadFormtypeKeywords(this.projektId);
		this.validationData.put(TagMatchDefinitions.FORM_TYPE_CATEGORY, keywords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println(arg0);
		System.out.println("Page formtype: " + this.view.getSelectedPage().getNote(CBase.StateKeys.KEY_BESTCAT.getKey()));
		setFormtypeInDocument();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		// empty
		SkyLogger.getClientLogger().debug(arg0.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent arg0) {
		// empty
		SkyLogger.getClientLogger().debug(arg0.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");
		final Component source = (Component) arg0.getSource();
		final Component parent = source.getParent();
		if (parent.getClass().equals(NBTagMatchViewElement.class)) {
			SkyLogger.getClientLogger().info(logPrefix + ": checkQuality");
			final NBTagMatchViewElement tagView = (NBTagMatchViewElement) parent;
			final char keyChar = arg0.getKeyChar();
			if (!(arg0.isControlDown() && keyChar == '\n')) {
				final String text = ((JTextField) source).getText().trim();
				final int length = text.length();
				SkyLogger.getClientLogger().debug(logPrefix + ": length = " + length);
				if (length > 0) {
					tagView.setConfidence(60);
				} else {
					tagView.setConfidence(0);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ityx.contex.impl.documentviewer.tagmatch.interfaces.IFormtypeViewer
	 * #keyPressed(java.awt.event.ActionEvent)
	 */
	@Override
	public void keyPressed(ActionEvent e) {
		// empty
		SkyLogger.getClientLogger().debug(e.toString());
	}

	// Calculates the label width.
	protected int calculateLabelWidth(Set<TagMatch> tags) {
		int labelWidth = 0;
		FontMetrics fontMetrics = Start.getInstance().getGraphics().getFontMetrics(this.fontForTextFields);
		final TagMatch tm = this.inputBox.getTagmatch();
		labelWidth = fontMetrics.charsWidth((tm.getCaption() + ": ").toCharArray(), 0, tm.getCaption().length() + 2);
		for (TagMatch tmv : tags) {
			int charsWidth = fontMetrics.charsWidth((tmv.getCaption() + ": ").toCharArray(), 0, tmv.getCaption().length() + 2);
			labelWidth = Math.max(labelWidth, charsWidth);
		}
		return labelWidth;
	}

	@SuppressWarnings("unchecked")
	@Override
	// Nicht mehr ernsthaft ein Listener. Die Funktionalitaet wird aber noch
	// gebraucht.
	public void valueChanged(ListSelectionEvent e) {
		int firstIndex = this.tbSearchResults.getSelectedRow();
		if (firstIndex < 0 || firstIndex >= this.tbSearchResults.getRowCount()) {
			return;
		}
		firstIndex = this.tbSearchResults.convertRowIndexToModel(firstIndex);
		if (this.fuzzyResponse != null && firstIndex >= 0 && firstIndex < this.fuzzyResponse.getRowCount()) {
			DefaultTableModel dtm = (DefaultTableModel) this.tbSearchResults.getModel();
			Vector<Object> dataVector = dtm.getDataVector();
			setCustomerData((Vector<Object>) dataVector.get(firstIndex));
		}
	}

	// Applies the selected row to the view elements and corresponding
	// tagmatches.
	private void setCustomerData(Vector<Object> tableRow) {
		for (ITagMatchViewElement itmve : ValidationFormtypePanel_Sky_Lager_EZM.viewElements.values()) {
			final TagMatch tagmatch = itmve.getTagmatch();
			final String identifier = tagmatch.getIdentifier();
			String columnName = ValidationFormtypePanel_Sky_Lager_EZM.tagNames.get(identifier);
			int column = columnNames.indexOf(columnName) - 1;
			if (column >= 0) {
				column++;
				String value = (String) tableRow.get(column);
				tagmatch.setTagValue(value);
				tagmatch.setConfidence(100L);
				itmve.setTagMatch(tagmatch);
			}
		}
	}

	@SuppressWarnings("unchecked")
	// Sets the model of the fuzzy result table.
	protected void setTableModel(boolean fillTagMatches) {
		this.tbSearchResults.clearSelection();
		this.fuzzyResponse.sort();
		columnNames.clear();
		columnNames.add("%"); //$NON-NLS-1$
		boolean dealer = false;
		boolean host = false;
		for (String colName : this.fuzzyResponse.getColumnNames()) {
			columnNames.add(colName);
		}
		Vector<Object> data = new Vector<Object>();
		for (Row row : this.fuzzyResponse) {
			Vector<Object> v = new Vector<Object>();
			v.add(Math.ceil(100 - row.getError() * 100));
			for (Object object : row.getFields()) {
				v.add(object);
			}
			data.add(v);
		}
		DefaultTableModel dataModel = new DefaultTableModel(data, columnNames);
		ValidationFormtypePanel_Sky_Lager_EZM.this.tbSearchResults.setModel(dataModel);
		// Erste Row nehmen und die zusaetzlichen Attribute füllen:
		if (fillTagMatches && this.fuzzyResponse.getRowCount() == 1) {
			Vector<Object> tableRow = (Vector<Object>) data.get(0);
			setCustomerData(tableRow);
		}
	}

	/**
	 * @param newValue
	 * @param page
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	public void setValueFromExternalView(String newValue, int page, int x1, int x2, int y1, int y2) {
		if (newValue.length() == 0) {
			return;
		}

		if (ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent != null && ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent instanceof JTextField) {
			if (ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent.getName().equals("attribute3") && newValue.length() > 9) { //$NON-NLS-1$
				newValue = newValue.substring(0, 9);
			}
			((JTextField) ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent).setText(newValue);
			TagMatch tagmatch = ((ITagMatchViewElement) ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent.getParent()).getTagmatch();
			tagmatch.setPage(page + 1);
			tagmatch.setX1(x1);
			tagmatch.setX2(x2);
			tagmatch.setY1(y1);
			tagmatch.setY2(y2);
			if (!ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent.getName().equals("attribute3")) { //$NON-NLS-1$
				ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent.transferFocus();
			} else {
				ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent.requestFocus();
			}
		} else if (ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent != null && ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent instanceof JComboBox) {
			((JComboBox) ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent).setSelectedItem(newValue);
			ValidationFormtypePanel_Sky_Lager_EZM.lastFocusComponent.transferFocus();
		}
	}

	// Adds the Action that resets the search result table.
	private void initActionMapForSearchResult() {
		Action anAction = new SoftUnfocusTableAction();
		this.tbSearchResults.getActionMap().put(anAction.getValue(Action.NAME), anAction);
		KeyStroke aKeyStroke = (KeyStroke) anAction.getValue(Action.ACCELERATOR_KEY);
		this.tbSearchResults.registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		this.tbSearchResults.registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	// Adds some actions.
	private void initActionMapForSelectedPage() {
		IDocumentViewer dokumentViewer = getView().getDocumentViewer();
		if (dokumentViewer == null) {
			return;
		}
		ActionMap viewerActionMap = dokumentViewer.getActionMapForPage(dokumentViewer.getSelectedpage());
		ActionListener anAction = viewerActionMap.get("rotate90"); //$NON-NLS-1$
		KeyStroke aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.ALT_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		anAction = viewerActionMap.get("rotate180"); //$NON-NLS-1$
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		anAction = viewerActionMap.get("rotate270"); //$NON-NLS-1$
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.ALT_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		anAction = viewerActionMap.get("originalSize"); //$NON-NLS-1$
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		anAction = viewerActionMap.get("zoomIn"); //$NON-NLS-1$
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.ALT_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.ALT_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ADD, InputEvent.ALT_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		anAction = viewerActionMap.get("zoomInFast"); //$NON-NLS-1$
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, InputEvent.ALT_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		anAction = viewerActionMap.get("zoomOut"); //$NON-NLS-1$
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.ALT_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.ALT_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		anAction = viewerActionMap.get("fitWidth"); //$NON-NLS-1$
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		ActionMap parentActionMap = getView().getActionMap();
		Action saveAction = parentActionMap.get("save"); //$NON-NLS-1$

		// Wenn die Save-Action noch die Originale aus dem
		// ExtraktionValidationView ist, dann muss sie hier auf die spezifische
		// umgerüstet werden:
		if (!(saveAction instanceof SaveMeAction)) {
			anAction = new SaveMeAction(saveAction);
			parentActionMap.put("save", (Action) anAction);
		} else {
			anAction = saveAction;
		}
		aKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK);
		registerKeyboardAction(anAction, aKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	// Initializes key codes.
	protected void initKeyCodes() {
		// Set up Key-events
		final InputMap inputMap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		final ActionMap actionMap2 = this.getActionMap();

		Action action = new NextPageAction();
		inputMap.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), action.getValue(Action.NAME));
		actionMap2.put(action.getValue(Action.NAME), action);

		action = new PreviousPageAction();
		inputMap.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), action.getValue(Action.NAME));
		actionMap2.put(action.getValue(Action.NAME), action);

		Action fuzzyAction = new FuzzySearchAction();
		inputMap.put((KeyStroke) fuzzyAction.getValue(Action.ACCELERATOR_KEY), fuzzyAction.getValue(Action.NAME));
		actionMap2.put(fuzzyAction.getValue(Action.NAME), fuzzyAction);

		action = new FocusTableAction();
		inputMap.put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), action.getValue(Action.NAME));
		actionMap2.put(action.getValue(Action.NAME), action);

		initActionMapForSelectedPage();

		// Initialiseren der FocusTraversalkeys
		Set<KeyStroke> forward = new HashSet<KeyStroke>();
		forward.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
		forward.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_MASK));
		forward.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));

		// Set<KeyStroke> forwardWithEnter = new HashSet<KeyStroke>();
		// forwardWithEnter.addAll(forward);
		// forwardWithEnter.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

		Set<KeyStroke> backward = new HashSet<KeyStroke>();
		backward.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK));
		backward.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK));
		backward.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.ALT_MASK));
		backward.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
	}
	
	/**
	 * Initializes the button that cleans the view elements.
	 * 
	 * @param parent
	 *            The parent to add the button
	 * @param gbc
	 *            The layout
	 */
	public void initCleanTagsButton(JPanel parent, GridBagConstraints gbc) {
		gbc.gridx = 1;
		final Action action = new AbstractAction() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("CleanTags")) {
					SkyLogger.getClientLogger().info("CleanTagsAction: actionPerformed()");
					for (TagMatchViewElement tmve : viewElements.values()) {
						final TagMatch tagmatch = tmve.getTagmatch();
						tagmatch.setTagValue("");
						tmve.setTagMatch(tagmatch);
					}
				}
			}
		};
		action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK));
		action.putValue(Action.ACTION_COMMAND_KEY, "CleanTags"); //$NON-NLS-1$
		action.putValue(Action.NAME, "CleanTags");
		parent.add(new JButton(action), gbc);
	}

	public static Boolean getLastOp() {
		return lastOp;
	}

	public static void setLastOp(Boolean lastOp) {
		ValidationFormtypePanel_Sky_Lager_EZM.lastOp = lastOp;
	}
}
