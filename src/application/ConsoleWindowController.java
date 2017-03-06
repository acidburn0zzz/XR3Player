/**
 * 
 */
package application;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.ActionTool;
import tools.InfoTool;
import xplayer.presenter.XPlayerController;

/**
 * @author GOXR3PLUS
 *
 */
public class ConsoleWindowController extends StackPane {

    @FXML
    private BorderPane borderPane;

    @FXML
    private JFXTextField commandTextField;

    @FXML
    private Button help;

    @FXML
    private MaskerPane maskerPane;

    //--------------------------------------------------------

    InlineCssTextArea cssTextArea = new InlineCssTextArea();

    /** The logger. */
    private Logger logger = Logger.getLogger(getClass().getName());

    /**
     * The Console Window
     */
    public Stage window = new Stage();

    /**
     * Constructor
     */
    public ConsoleWindowController() {

	// ------------------------------------FXMLLOADER
	FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.fxmls + "ConsoleWindowController.fxml"));
	loader.setController(this);
	loader.setRoot(this);

	try {
	    loader.load();
	} catch (IOException ex) {
	    logger.log(Level.SEVERE, "", ex);
	}

	window.setTitle("XR3Player Console");
	window.initStyle(StageStyle.UTILITY);
	window.setScene(new Scene(this));
	this.focusedProperty().addListener((observable, oldValue, newValue) -> {
	    if (window.isFocused())
		commandTextField.requestFocus();
	});
    }

    /**
     * Called as soon as .fxml is initialized
     */
    @FXML
    private void initialize() {

	//CSS TEXT AREA	
	cssTextArea.setStyle("-fx-background-color:black;");
	cssTextArea.setWrapText(true);
	cssTextArea.setEditable(false);
	cssTextArea.setFocusTraversable(false);
	String t = "Click Help to check the manual\n";
	cssTextArea.appendText(t);
	cssTextArea.setStyle(0, t.length(), "-fx-fill:green; -fx-font-weight:bold; -fx-font-size:15;");

	VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(cssTextArea);
	vsPane.setMaxWidth(Double.MAX_VALUE);
	vsPane.setMaxHeight(Double.MAX_VALUE);
	borderPane.setCenter(vsPane);

	//help
	help.setOnAction(
		a -> ActionTool.openFile(InfoTool.getBasePathForClass(ActionTool.class) + "XR3Player Manual.pdf"));

	//commandTextField
	commandTextField.setOnAction(a -> procceedCommand(commandTextField.getText()));

    }

    /**
     * Show the Window
     */
    public void show() {
	if (window.isShowing())
	    window.requestFocus();
	else
	    window.show();
    }

    Pattern pattern1 = Pattern.compile("player:[-|+]?\\d+:\\w+");
    Pattern pattern2 = Pattern.compile("player:[-|+]?\\d+:\\w+:[-|+]?\\d+");
    Pattern pattern3 = Pattern.compile("player:[-|+]?\\d+:\\w+:[-|+]?\\d+:[s|m|h]");

    /**
     * This method is procceeding the commands for the ConsoleWindow
     * 
     * @param command
     */
    public void procceedCommand(String command) {
	boolean success = false;

	//Print something to the user
	String message = "Procceeding...->[" + command + "]";

	//Proceed the command
	command = command.trim().replaceAll("[ ]+", ":").toLowerCase();

	//Check if it is null
	if (command.isEmpty()) {
	    Notifications.create().text("You have to type something..").showWarning();
	    return;
	}

	//Player command?
	if (command.startsWith("player")) {
	    System.out.println(command);
	    String[] array = command.split(":");

	    //It must be 3 [format = "player"+"key"+"action"]
	    if (array.length == 3 && pattern1.matcher(command).matches()) {
		System.out.println("Yes it does match format [\"player\"+\"key\"+\"action\"] ");
		XPlayerController player = Main.xPlayersList.getXPlayerController(Integer.parseInt(array[1]));

		String action = array[2];
		if ("stop".equals(action)) { //stop
		    player.stop();
		    success = true;
		} else if ("play".equals(action)) { //play
		    player.playOrReplay();
		    success = true;
		} else if ("resume".equals(action)) { //resume
		    player.resume();
		    success = true;
		} else if ("pause".equals(action)) { //pause
		    player.pause();
		    success = true;
		} else if ("replay".equals(action)) { //replay
		    player.replaySong();
		    success = true;
		} else if ("mute".equals(action)) { //replay
		    player.mute();
		    success = true;
		} else if ("unmute".equals(action)) { //replay
		    player.unMute();
		    success = true;
		} else if ("open".equals(action)) { //open file chooser
		    player.openFileChooser();
		    success = true;
		}

	    }

	    //It must be 4 [format = "player"+"key"+"action"+"value"]   
	    else if (array.length == 4 && pattern2.matcher(command).matches()) {
		System.out.println("Yes it does match \"player\"+\"key\"+\"action\"+\"value\"] ");
		XPlayerController player = Main.xPlayersList.getXPlayerController(Integer.parseInt(array[1]));

		String action = array[2];
		int value = Integer.parseInt(array[3]);

		if ("volume".equals(action)) { //adjust volume
		    player.adjustVolume(value);
		    success = true;
		} else if ("setvolume".equals(action)) { //setvolume
		    player.setVolume(value);
		    success = true;
		}
	    }

	    //It must be 4 [format = "player"+"key"+"action"+"value"+"s|m|h"]   
	    else if (array.length == 5 && pattern3.matcher(command).matches()) {
		System.out.println("Yes it does match \"player\"+\"key\"+\"action\"+\"value\"+\"s|m|h\"] ");
		XPlayerController player = Main.xPlayersList.getXPlayerController(Integer.parseInt(array[1]));

		String action = array[2];
		int value = Integer.parseInt(array[3]);

		if ("seek".equals(action)) { //seek
		    //by default it will be seconds
		    if ("m".equals(array[4])) //make it minutes
			value = value * 60;
		    else if ("h".equals(array[4])) //make it hours
			value = value * 60 * 60;

		    //Go seek
		    player.seek(value);

		    success = true;
		} else if ("seekto".equals(action)) { //goto a specific moment
		    //by default it will be seconds
		    if ("m".equals(array[4])) //make it minutes
			value = value * 60;
		    else if ("h".equals(array[4])) //make it hours
			value = value * 60 * 60;

		    //Then seek
		    player.seekTo(value);

		    success = true;
		}
	    }

	} else if ("clear".equals(command) || "cls".equals(command)) { //Clear 
	    cssTextArea.clear();
	    cssTextArea.clear();
	    success = true;
	} else if ("help".equals(command)) { //help
	    ActionTool.openFile(InfoTool.getBasePathForClass(ActionTool.class) + "XR3Player Manual.pdf");
	    success = true;
	}

	System.out.println(command);

	//-----------------------------Message for the user-------------------------------------------
	cssTextArea.appendText(message);
	cssTextArea.setStyle(cssTextArea.getText().length() - message.length(), cssTextArea.getLength(),
		"-fx-font-weight:bold; -fx-font-size:14; -fx-fill:white;");

	String result = "  " + (success ? "Succeeded" : "Error");
	cssTextArea.appendText(result);
	cssTextArea.setStyle(cssTextArea.getText().length() - result.length() + 2, cssTextArea.getLength(),
		"-fx-font-weight:bold; -fx-font-size:14; -fx-fill:" + (success ? "green" : "red") + ";");
	cssTextArea.appendText("\n");

	cssTextArea.moveTo(cssTextArea.getLength());
	cssTextArea.requestFollowCaret();
    }

}
