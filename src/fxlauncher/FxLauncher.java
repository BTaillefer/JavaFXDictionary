package fxlauncher;
import java.io.File;

import dictionary.Dictionary;
import filemanager.FileManager;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FxLauncher extends Application {
	private HBox root; //Main HBox
	private VBox wordVBox; //VBox of word's inside the HBox (root)
	private WebView resultsWebView; //Webview to show the references to paragraphs where the word/error resides
	private Dictionary dictionary;
	private static int counter = 0; //Keep track of what the user choose, spellchecker or concordance
	
	/** Method provided by Rex to create a FileChooser in the directory where the program was launched
	 * to choose what text file to use 
	 * @param fileChooser
	 * @return fileChooser
	 */
	private static FileChooser setInitialFileChooserDirectory(FileChooser fileChooser) {
		String absolutePathString = new File(".").getAbsolutePath();
		absolutePathString = absolutePathString.substring(0, absolutePathString.length() - 2);
		File currentDirectory = new File(absolutePathString);
		fileChooser.setInitialDirectory(currentDirectory);
		return fileChooser;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		//Choose whether to declare a concordance or a spellchecker
		concordanceOrSpellChecker();
		primaryStage.setTitle("Dictionary Word Viewer");
		FileManager filemanager = new FileManager();
		FileChooser filechooser = new FileChooser();
		setInitialFileChooserDirectory(filechooser);
		File file = filechooser.showOpenDialog(primaryStage);
		filemanager.setTextName(file.getName());filemanager.setFileName("Main.txt");
		dictionary = new Dictionary(filemanager);
		dictionary.loadDictionary();
		dictionary.loadText();
		
		//Check to see whether to be a spellchecker or concordance, needs to be reworked
		if(counter == 1) {
			dictionary.spellChecker();
		}
		else {
			dictionary.concordanceChecker();
		}
		wordVBox = new VBox();
		// Iterate through the collection of words (String objects)
		// create a Node.Text object with suitable font
		// create a CALLBACK event handler for the onMouseEntered event which pours content into the WebView object.
		for (final String word : dictionary.getSpellCheck().keySet()) {
			// The "final" keyword is important.
			// The variable "text" is a local, stack-oriented variable that will disappear after each iteration of the for-loop
			// The "final" keyword is a signal to the compiler to make a copy of the "Constant" value ("final" makes something constant).
			// The copy is then inserted into the anonymous inner class built from the EventHandler interface 
			final Text text = new Text(word); text.setFont(new Font("Times New Roman", Math.sqrt(dictionary.getNumberOfParagraphs(word) + 200))); // variable font sizing could be used to identify the relative significance of words
			final String sRegExSearch = "\\W(?i:" + word + ")\\W";
			text.setOnMouseEntered(new EventHandler<MouseEvent>() {
				//************************************************
				// Start: EventHandler CALLBACK 
				// NOT EXECUTED WHEN CREATED IN THIS LOOP
				// Executed much later, in response to a user drifting the mouse over a target text object.
				@Override	public void handle(MouseEvent event) {
					resultsWebView.getEngine().loadContent("<h1>Paragraph's : \n </h1><i>" + dictionary.getSpellCheck().get(word )+ "</i></h1>");
					
					 final String HTMLBoundingBlueBoxCSSstyle = "<style>.border{border-style:solid;border-color:#287EC7;}</style>";
					  StringBuilder stringBuilder = new StringBuilder(HTMLBoundingBlueBoxCSSstyle);
					  for (String paragraph : dictionary.getSpellCheck().get(word)) {
					    String highlightedString = paragraph.toString().replaceAll(sRegExSearch,"<span class=\"border\">$0</span>");
					    stringBuilder.append(highlightedString).append("<br><br>"); // two blank lines between each paragraph in the WebView
					   } // end for(processing all paragraphs in collection)
					   resultsWebView.getEngine().loadContent(stringBuilder.toString());
				}});
				// End: EventHandler CALLBACK 
				//************************************************
			wordVBox.getChildren().add(text);
		}
		resultsWebView = new WebView(); // WebView object is created and captured here, this will occur BEFORE resultsWebView is used in the EventHandler.handle() CALLBACK.
		resultsWebView.setPrefWidth(1800); //Set the webview width
		root = new HBox(new ScrollPane(wordVBox), resultsWebView); // Java 8 compliant
		root.setPrefWidth(2100); //Set the HBox width
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
	}

	public static void main(String[] args) { launch(args);  }
	
	/** Method used to choose whether you want to run concordance or spellchecker, needs to be reworked
	 * 
	 */
	public static void concordanceOrSpellChecker() {
		//Create a new scene to get user input
		javafx.application.Platform.runLater(new Runnable() {
			@Override
			public void run() {
				HBox root = new HBox();
				VBox vbox = new VBox();
				final Stage stage = new Stage();
				stage.setTitle("What would you like to search?");
				vbox.setPrefWidth(500);vbox.setPrefHeight(400);
				//Create two new buttons one for spellcheck, one for concordance
				Button spellchecker = new Button("SpellCheck");Button concordance = new Button("Concordance");
				spellchecker.setPrefWidth(500); spellchecker.setPrefHeight(200); //Set button Dimensions
				concordance.setPrefWidth(500); concordance.setPrefHeight(200); //Set button Dimensions
				vbox.getChildren().addAll(spellchecker,concordance);
				root.getChildren().add(vbox);
				stage.setScene(new Scene(root));
				stage.show();
				
				//Mouse click event to tell the program that the user has clicked the spellcheck button
				spellchecker.addEventHandler(MouseEvent.MOUSE_CLICKED,
						new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
									counter = 1;
									stage.close();
							}
				});
				
				//Mouse click event to tell the program that the user has clicked the concordance button
				concordance.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						counter = 2;
						stage.close();
					}});
				}
			});
		}
		
	}
