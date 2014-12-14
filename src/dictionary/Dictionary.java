package dictionary;
import java.io.*;
import java.util.*;

import filemanager.FileManager;

public class Dictionary {
	private ArrayList<String> mainDictionaryWords;  //ArrayList for the mainDictionaryWords
	private ArrayList<String> paragraphList;				//ArrayList for the list of paragraphs
	private FileManager filemanager;								//Declare FileManager object
	/** Create the Map with a string as the key and a reference to paragraphs for the value **/
	private Map<String,String<String>> mapOfWords = new TreeMap<String,ArrayList<String>>(String.CASE_INSENSITIVE_ORDER);  
	
	/** First Constructor for Dictionary object , takes references to a FileManager object **/
	public Dictionary(FileManager filemanager) {
		this.filemanager = filemanager;		
	}
	
	/** Second Constructor for Dictionary object, takes the two fileName's and creates a FileManager object **/
	public Dictionary(String fileName, String textName) {
		filemanager = new FileManager(fileName,textName);
	}

	/**
	 * Loads the dictionary into its respective Collection class. In this case a the selected file is parsed into a TreeSet and later
	 * converted to an ArrayList. A timer is also present to represent how long it takes to run the method.
	 * Uses the predefined filename to open a file and populate the collection created here.
	 * @throws FileNotFoundException
	 */
	public long loadDictionary() throws FileNotFoundException {
		/*Choose our Collection Class to parse the file with, in this case a TreeSet. We are also calling CASE_INSENSITIVE_ORDER to make
		 * the case insensitive.
		 */
		TreeSet<String> treeSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		/* Create scanner object to read in Main Dictionary */
		Scanner scanInput = new Scanner(new File(filemanager.getFileName()));
		/* Take current time in miliseconds to test how long the method takes to run */
		long startTime = System.currentTimeMillis();
		/* While loop to scan in file and add to the TreeSet */
		while (scanInput.hasNext()) {
			String word = scanInput.nextLine();
			treeSet.add(word); // virtual method to add another word to the collection
		}
		scanInput.close();
		/* Declare collection words as an observableArrayList, converting the TreeSet into an ArrayList */
		mainDictionaryWords = new ArrayList<String>(treeSet); 
		/* Stop timer and calculate how long the method took to run */
		long elapseTime = System.currentTimeMillis() - startTime;
		System.out.printf("Collection Type:%s loadDictionary() Word Count:%d Elapse Time:%.3f seconds\n", mainDictionaryWords.getClass().getName(), mainDictionaryWords.size(), (double)elapseTime/1000.0);
		return elapseTime;
	} // end loadDictionary(

	/** Load the chosen text from the users. Reads in the text line by line and inswerts it into the collection textWords.
	 * @return elapseTime
	 * @throws FileNotFoundException
	 */
	public long loadText() throws FileNotFoundException {
		//Read in the desired file
		Scanner scanInput = new Scanner(new File(filemanager.getTextName()));
		long startTime = System.currentTimeMillis();
		//Delimiter to split the text with. Split text at every new paragraph.
		scanInput.useDelimiter("\r\n");
		//Initialize into the ArrayList to hold the paragraphs.
		paragraphList = new ArrayList<String>();
		//Read in the file line by line
		while(scanInput.hasNext()) {
			String paragraph = scanInput.nextLine();
			paragraph.trim();
			paragraphList.add(paragraph);
		}
		scanInput.close();
		long elapseTime = System.currentTimeMillis() - startTime;
		System.out.printf("Collection Type:%s loadText() Elapse Time:%.3f seconds\n", paragraphList.getClass().getName(), (double)elapseTime/1000.0);
		return elapseTime;
	}
	
	/** Return size of main dictionary 
	 * @return collectionWords.size()
	 */
	public int getSize() { return mainDictionaryWords.size(); }
	
	/**Return size of text in paragraphs
	 * @return textWords.size()
	 */
	public int getTextSize() { return paragraphList.size(); }
	
	/** Return a reference to a unmodifiable ObservableList of the main dictionary 
	 * @return unmodifableObservableList(collectionWords)
	 */
	public List<String> getMainDictionary() {
		return mainDictionaryWords;
	}
	
	/** Return a reference to a unmodifiable ObservableList of the text
	 * @return unmodifiableObservableList(textWords)
	 */
	public List<String> getParagraphList() {
		return paragraphList;
	}
	
	/**
	 * Returns the reference to Map 
	 * @return spellCheck
	 */
	public Map<String,ArrayList<String>> getSpellCheck() {
		return mapOfWords;
	}
	/**
	 * Returns the number of references to paragraphs in each Map value
	 * @param word
	 * @return numberOfParagraphs.size()
	 */
	public int getNumberOfParagraphs(String word) {
		ArrayList<String> numberOfParagraphs =  mapOfWords.get(word);
		return numberOfParagraphs.size();
		
	}
	
	/** Checks to see if every word in the selected text is correctly spelled. If the word is not found
	 * in the main dictionary the word is displayed as a spelling error. Includes the same checking to see
	 * if the current word is already found in the paragraphsWhereWordResides ArrayList, if not it is added
	 */
	public void spellChecker() {
		//Iterate through the list of paragraphs getting each paragraph
		for(String paragraphWord : paragraphList) {
			//Split the paragraph on the desired chars
			String[] arrayWords = paragraphWord.split("[^a-zA-Z']+");
			//Iterate through the String array after the paragraph has been split
			for(String word: arrayWords) {
				//Binary Search the main dictionary to see if it includes the desired word
				if(Collections.binarySearch(mainDictionaryWords,word, String.CASE_INSENSITIVE_ORDER) <=0 && !word.isEmpty()) {
					//Use the map to see if the current word is a key
					ArrayList<String> paragraphsWhereWordResides = mapOfWords.get(word);
						//If the word is not found, create a new key entry
						if(paragraphsWhereWordResides == null) {
							//Initialize the paragraphsWhereWordResides ArrayList
							paragraphsWhereWordResides = new ArrayList<String>();
							//Enter the current key and resulting reference to array of paragraphs into the map
							mapOfWords.put(word, paragraphsWhereWordResides);
							//Add the reference to paragraph into the Map's selected value field
							paragraphsWhereWordResides.add(paragraphWord);
						}	
						else {
							//If the word is already found, checks to see if the word has already been entered for the current paragraph
							//Very inefficient .contains on an ArrayList is BigO(n)
							if(!paragraphsWhereWordResides.contains(paragraphWord)) {
								paragraphsWhereWordResides.add(paragraphWord);
							}
						}
				}
			}	
		}
	}
	
	/** Iterates through the text and adds all word's found to the Map , spellCheck.
	 * Checks to see if the current word is in the map, if it isn't it adds it as a new value
	 * if found, it searches the paragraphsWhereWordResides ArrayList to see if the current paragraph
	 * is included, if it isn't it adds a reference to said paragraph. 
	 */
	public void concordanceChecker() {
		long startTime = System.currentTimeMillis();
		//Iterate through the paragraphList to return each paragraph
		for(String paragraphWord : paragraphList) {
			//Split the paragraph into a String Array on the selected regex
			String[] arrayWords = paragraphWord.split("[^a-zA-Z']+");
			//Keep a reference to the current paragraph
			String current = paragraphWord;
			//Iterate through each word in the String array of the paragraph
			for(String word: arrayWords) {
				if(!word.isEmpty()) {
					//Initialize the pargraphsWhereWordResides to see if the current word exists in the Map as a key
					ArrayList<String> paragraphsWhereWordResides = mapOfWords.get(word);
					//If the word wasn't found add the word and reference to paragraph to the Map
					if(paragraphsWhereWordResides == null) {
						paragraphsWhereWordResides = new ArrayList<String>();
						//Add the word as key, and the reference to paragraph ArrayList as the value
						mapOfWords.put(word, paragraphsWhereWordResides);
						//Add the current paragraph to the references of paragraphs
						paragraphsWhereWordResides.add(paragraphWord);
					}	
					else {
						//If the word was found, check to see if the current pargraph is in the references to paragraphs
						//Very inefficent, ArrayList with .contains() BigO(n)
						if(!paragraphsWhereWordResides.contains(current)) {
							paragraphsWhereWordResides.add(paragraphWord);
						}
					}
				}
			}
			
		} // end for each paragraph
		long elapseTime = System.currentTimeMillis() - startTime;
		System.out.printf("Collection Type:%s loadText() Elapse Time:%.3f seconds\n", paragraphList.getClass().getName(), (double)elapseTime/1000.0);

	} // end concordanceChecker
} // end class Dictionary