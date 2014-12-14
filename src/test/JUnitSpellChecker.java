package test;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;



import org.junit.Before;
import org.junit.Test;

import dictionary.Dictionary;

/**
 * @author Brodie
 *
 */
public class JUnitSpellChecker {
	private static Dictionary dictionary;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dictionary = new Dictionary("main.txt","text.txt");
	}

	/** Test to see if the Dictionary we load is not null, a null dictionary would be useless
	 */
	@Test
	public void loadDictionary() {
		try {
			dictionary.loadDictionary();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(dictionary.getMainDictionary());
	}
	
	/** Test to see if the dictionary has been sorted properly. It first checks to see if there are no duplicates in the dictionary, and also
	 * checks to see the the dictionary is in correct alpha order
	 */
	@Test
	public void sortDictionary() {
		try {
			dictionary.loadDictionary();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String previous = null;
		for(String current : dictionary.getMainDictionary()) {
			if(previous == null) {
				previous = current;
				continue;
			}
			assertTrue("Duplicates", previous != current);
			assertTrue(String.format("Not in alpha order, previous: %s  current: %s",previous,current), previous.compareToIgnoreCase(current) <0);
			previous = current;
		}
	}
	
	/** Checks to see if the text we loaded is not Null, a null text would be useless.
	 */
	@Test
	public void loadText() {
		try {
			dictionary.loadText();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		assertNotNull(dictionary.getParagraphList());
	}

}
