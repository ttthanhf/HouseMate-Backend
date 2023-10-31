package housemate.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtil {

	public static String removeDiacriticalMarks(String string) {
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(string).replaceAll("");
	}

	/**
	 * @return A String Normalization for comparing which normalize by using 
	 * <blockquote><pre>
	 * strip
	 * removeDiacriticalMarks
	 * removeMultiWhiteSpaceWithOneBlank
	 * lowerCase
	 * </pre></blockquote>
	 * @author HOANGANH
	 */

	public static String stringNormalizationForCompare(String string) {
		string = removeDiacriticalMarks(string.strip().replaceAll("\\s+", " ").toLowerCase());
		return string;
	}
	
	/**
	 * @return A String Normalization for comparing which normalize by using 
	 * <blockquote><pre>
	 * strip
	 * removeMultiWhiteSpaceWithOneBlank
	 * </pre></blockquote>
	 * @author HOANGANH
	 */
	public static String formatedString(String string) {
		string = string.strip().replaceAll("\\s+", " ");
		return string;
	}
}
