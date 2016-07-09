package net.sharksystem.sharknet.javafx.controller.interest;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;

import static net.sharksystem.sharknet.javafx.i18n.I18N.getString;

import java.util.Arrays;

public class InterestEntryController {

	private enum SubscriptionType {
		ACTIVATED(getString("interest.subscription.activated")),
		DEACTIVATED(getString("interest.subscription.deactivated"));

		private String caption;

		SubscriptionType(String caption) {
			this.caption = caption;
		}

		public String getCaption() {
			return caption;
		}

		@Override
		public String toString() {
			return caption;
		}
	}

	public static class LinkEntry {
		private final SimpleStringProperty link;

		public LinkEntry(String link) {
			this.link = new SimpleStringProperty(link);
		}

		public String getLink() {
			return link.get();
		}

		public void setLink(String link) {
			this.link.setValue(link);
		}

		@Override
		public String toString() {
			return link.get();
		}
	}

	/******************************************************************************
	 *
	 * FXML Fields
	 *
	 ******************************************************************************/
	@FXML private AnchorPane interestEditorRoot;
	@FXML private TextField interestNameTextbox;
	@FXML private TableView<LinkEntry> interestLinkTable;
	@FXML private ComboBox<SubscriptionType> subscriptionChooser;
	@FXML private Button addLinkButton;
	@FXML private Button removeLinkButton;
	@FXML private TextField linkToAddTextfield;

	/******************************************************************************
	 *
	 * Constructors
	 *
	 ******************************************************************************/

	public void initialize() {

		subscriptionChooser.getItems().addAll(SubscriptionType.ACTIVATED, SubscriptionType.DEACTIVATED);

		TableColumn<LinkEntry, String> linkColumn = new TableColumn<>(getString("interest.si.header"));
		linkColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		linkColumn.setCellValueFactory(new PropertyValueFactory<>("link"));
		linkColumn.setEditable(true);
		linkColumn.prefWidthProperty().bind(interestLinkTable.widthProperty().multiply(0.99));

		interestLinkTable.setEditable(true);
		interestLinkTable.getColumns().add(linkColumn);

		linkToAddTextfield.setOnKeyReleased(event -> {
			if (KeyCode.ENTER.equals(event.getCode())) {
				addLinkButton.fire();
			}
		});
		addLinkButton.setOnAction(this::onAddLinkClicked);
		removeLinkButton.setOnAction(this::onRemoveLinkClicked);

		interestNameTextbox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !"".equals(newValue.trim())) {
				getInterestTag().setName(newValue);
			}
        });

		/**
		 * Handle Link edits
		 */
		linkColumn.addEventHandler(TableColumn.editCommitEvent(), new EventHandler<TableColumn.CellEditEvent<LinkEntry, String>>() {

			@Override
			public void handle(TableColumn.CellEditEvent<LinkEntry, String> event) {
				TableColumn.CellEditEvent cellEvent = (TableColumn.CellEditEvent) event;
				String newValue = (String) cellEvent.getNewValue();
				if (newValue != null) {
					String oldValue = event.getOldValue();

					try {
						getInterestTag().addSI(newValue);
						if (oldValue != null) {
							getInterestTag().removeSI(oldValue);
						}
					} catch (SharkKBException e) {
						e.printStackTrace();
					}

				}
			}
		});

		interestTagProperty().addListener(this::onTagChanged);
		interestEditorRoot.visibleProperty().bind(visibilityProperty());

	}

	/******************************************************************************
	 *
	 * Methods
	 *
	 ******************************************************************************/
	/**
	 * Loads the data of tag from interestTagProperty when its value is changed
	 *
	 * @param observableValue
	 * @param oldTag
	 * @param newTag
     */
	private void onTagChanged(ObservableValue<? extends TXSemanticTag> observableValue, TXSemanticTag oldTag, TXSemanticTag newTag) {
		interestNameTextbox.clear();
		interestLinkTable.getItems().clear();
		subscriptionChooser.setValue(null);
		linkToAddTextfield.clear();

		interestNameTextbox.setText(newTag.getName());
		Arrays.asList(newTag.getSI())
			.stream()
			.map(LinkEntry::new)
			.forEach(linkEntry -> interestLinkTable.getItems().add(linkEntry));

	}

	/**
	 * Called when a user request to add a link to a interest
	 *
	 * @param event
     */
	private void onAddLinkClicked(ActionEvent event) {
		String newURL = linkToAddTextfield.getText().trim();
		linkToAddTextfield.clear();
		if (! "".equals(newURL)) {
			interestLinkTable.getItems().add(new LinkEntry(newURL));
			try {
				getInterestTag().addSI(newURL);
			} catch (SharkKBException e) {
				throw new RuntimeException("Failed to save si to " + getInterestTag().getName());
			}
		}
	}

	/**
	 * Called when a user requested to remove si identifier
	 *
	 * @param event
     */
	private void onRemoveLinkClicked(ActionEvent event) {
		TableView.TableViewSelectionModel<LinkEntry> selectionModel = interestLinkTable.getSelectionModel();
		if (! selectionModel.isEmpty()) {
			for (LinkEntry items:  selectionModel.getSelectedItems()) {
				if (interestLinkTable.getItems().size() > 1) {
					interestLinkTable.getItems().remove(items);
					try {
						getInterestTag().removeSI(items.getLink());
					} catch (SharkKBException e) {
						throw new RuntimeException("Failed to remove si from " + getInterestTag().getName(), e);
					}
				}
			}
		}
	}

	/******************************************************************************
	 *
	 * Properties
	 *
	 ******************************************************************************/

	private ObjectProperty<TXSemanticTag> interestTag;

	/**
	 * Specifies which TXSemanticTag should be displayed by this view.
	 *
	 * @return a property
     */
	public  ObjectProperty<TXSemanticTag> interestTagProperty() {
		if (interestTag == null) {
			interestTag = new SimpleObjectProperty<>(this, "interestTag");
		}
		return interestTag;
	}

	/**
	 * Returns which TXSemanticTag should be displayed by this view.
	 *
	 * @return the currently specified TXSemanticTag
     */
	public TXSemanticTag getInterestTag() {
		return interestTag == null ? null : interestTag.get();
	}

	/**
	 * Specifies which TXSemanticTag should be displayed by this view.
	 *
	 * @param tag the tag to display
     */
	public void setInterestTag(TXSemanticTag tag) {
		interestTagProperty().setValue(tag);
	}

	private BooleanProperty visibility;

	/***
	 * Determines if this view should be visible
	 *
	 * @return visibility property
     */
	public BooleanProperty visibilityProperty() {
		if (visibility == null) {
			visibility = new SimpleBooleanProperty(false);
		}
		return visibility;
	}

	public boolean isVisible() {
		return visibilityProperty().get();
	}

	public void setVisible(boolean visible) {
		visibilityProperty().set(visible);
	}
}
