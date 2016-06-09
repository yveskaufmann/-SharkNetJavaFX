package net.sharksystem.sharknet.javafx.controller;

import com.google.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.sharksystem.sharknet.api.SharkNet;
import net.sharksystem.sharknet.javafx.App;
import net.sharksystem.sharknet.javafx.actions.ActionEntry;
import net.sharksystem.sharknet.javafx.actions.annotations.Action;
import net.sharksystem.sharknet.javafx.context.ViewContext;
import net.sharksystem.sharknet.javafx.controller.chat.ChatController;
import net.sharksystem.sharknet.javafx.controller.inbox.InboxController;
import net.sharksystem.sharknet.javafx.controls.ActionBar;
import net.sharksystem.sharknet.javafx.controls.Workbench;
import net.sharksystem.sharknet.javafx.i18n.I18N;
import net.sharksystem.sharknet.javafx.utils.controller.AbstractController;
import net.sharksystem.sharknet.javafx.utils.controller.AbstractWindowController;
import net.sharksystem.sharknet.javafx.utils.FontAwesomeIcon;
import net.sharksystem.sharknet.javafx.utils.controller.ControllerMeta;
import net.sharksystem.sharknet.javafx.utils.controller.Controllers;
import net.sharksystem.sharknet.javafx.utils.controller.annotations.Controller;
import net.sharksystem.sharknet.javafx.utils.controller.annotations.FXMLViewContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

/**
 * The Root Controller of the application
 * which is responsible to assemble and manage
 * all child controllers.
 */
public class FrontController extends AbstractWindowController {

	private static final Logger Log  = LoggerFactory.getLogger(FrontController.class);

	/******************************************************************************
	 *                                                                             
	 * Fields
	 *                                                                              
	 ******************************************************************************/
	
	/**
	 * Currently active controller
	 */
	private ObjectProperty<AbstractController> activeController;

	/**
	 * Sidebar Controller
	 */
	private SidebarController sidebarController;

	/**
	 * A Toolbar which contains AbstractContext sensitive actions
	 * these action can be provided by implementing a action Provider
	 * Interface.
	 */
	@FXML
	private ActionBar toolbar;

	/**
	 * Container for the sidebar menu view
	 */
	@FXML
	private StackPane sidebarPane;

	/**
	 * Container for the main views like contacts, chats and so on.
	 */
	@FXML
	private StackPane mainPane;

	@FXML
	private Workbench workbench;

	@FXMLViewContext
	private ViewContext<FrontController> context;

	/******************************************************************************
	 *                                                                             
	 * Constructors                                                                   
	 *                                                                              
	 ******************************************************************************/
	
	public FrontController(Stage stage) {
		super(App.class.getResource("views/appView.fxml"), stage);
		setTitle(I18N.getString("app.title"));
		addMainControllers();
	}

	/******************************************************************************
	 *
	 * Properties
	 *
	 ******************************************************************************/

	/**
	 * Defines which controller is currently active inside the
	 * workbench view.
	 *
	 * @defaultValue null
     */
	public ReadOnlyObjectProperty<AbstractController> activeControllerProperty() {
		if (activeController == null) {
			activeController = new SimpleObjectProperty<>(this, "activeController");
		}
		return activeController;
	};

	/******************************************************************************
	 *                                                                             
	 * Methods
	 *                                                                              
	 ******************************************************************************/
	
	private void addMainControllers() {
		sidebarController = new SidebarController(this);
		Controllers.getInstance().registerController(HomeworkController.class);
		Controllers.getInstance().registerController(InboxController.class);
		Controllers.getInstance().registerController(ProfileController.class);
		Controllers.getInstance().registerController(ChatController.class);
		Controllers.getInstance().registerController(ContactController.class);
		Controllers.getInstance().registerController(RadarController.class);
		Controllers.getInstance().registerController(SettingsController.class);
	}


	public void goToView(Class<? extends AbstractController> controllerType) {
		goToView(Controllers.getInstance().get(controllerType));
	}

	public void goToView(AbstractController controller) {
		ViewContext<AbstractController> context = controller.getContext();
		ControllerMeta meta = context.getMeta();

		if (activeControllerProperty().get() != null) {
			activeController.get().onPause();
		}

		toolbar.setTitle(meta.getTitle());
		toolbar.actions().setAll(meta.actionEntriesProperty());
		mainPane.getChildren().setAll(controller.getRoot());
		activeController.set(controller);
		controller.onResume();
	}



	/******************************************************************************
	 *
	 * Event-Handlers
	 *
	 ******************************************************************************/

	/**
	 * Will be called when the window is requested to be closed.
	 *
	 * @param windowEvent
	 */
	@Override
	public void onCloseRequest(WindowEvent windowEvent) {

	}

	/**
	 * Called when the scene was created
	 */
	@Override
	protected void onSceneCreated() {
		getScene().getStylesheets().add(App.class.getResource("css/style.css").toExternalForm());
	}

	/**
	 * Called when the stage was created
	 */
	@Override
	protected void onStageCreated() {
		getStage().sizeToScene();
		getStage().centerOnScreen();
	}

	/**
	 * Called when the fxml file was loaded
	 */
	@Override
	protected void onFxmlLoaded() {

		sidebarPane.getChildren().add(sidebarController.getRoot());
		toolbar.setNavigationNode(ActionBar.createActionButton(new ActionEntry(
			FontAwesomeIcon.NAVICON, (action) -> {
				workbench.toggleSidebar();
			}
		)));
		goToView(InboxController.class);
	}

}
