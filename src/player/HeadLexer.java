package player;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class HeadLexer {

    private final Map<Character,List<String>> headerMap;
    
    private int fieldNum = 0; // keep track of the filed order, to ensure proper header formatting
    
    /**
     * Lexes the Header by populating a hash map where the keys the header fields and the value 
     * is the the field's value
     * @param headerString a String representing the header portion of an abc music file
     */
    public HeadLexer (String headerString) {
        Map<Character, List<String>> headerMap = new HashMap<Character,List<String>>();
        String[] lines = headerString.split("\n"); // header fields separated by new line

        for (String line: lines) {
            if (line.length() == 0) continue;     // skip blank lines
            if (line.indexOf("%") == 0) continue; // ignore commented lines

            if (line.length() < 2)
                throw new IllegalArgumentException(
                        "Invalid header. The following Header Line is too short: " + line);

            String headerField = line.substring(0, 1); // extract the first letter in the field.
            String headerText;
            if(line.indexOf("%") != -1){ // check if a comment exists and where it is located.
                if (line.indexOf("%") <= 2)
                    throw new RuntimeException(
                            "Badly formed header field. Comment declaration appears too soon on this line: " + line);
                headerText = line.substring(2,line.indexOf("%"));
            }
            else {
                if (!line.substring(1,2).equals(":"))
                    throw new IllegalArgumentException("Unrecognized header field: " + line);
                headerText = line.substring(2);
            }
            
            if (isValid(headerField.charAt(0))) {
                
                // Check that the first field in the header is the index number ('X')
                if (fieldNum == 0 && headerField.charAt(0) != 'X')
                    throw new RuntimeException(
                            "The first field in the header must be the index number ('X')");
                
                // Check that the second field in the header is the title ('T')
                if (fieldNum == 1 && headerField.charAt(0) != 'T')
                    throw new RuntimeException(
                            "The second field in the header must be the title ('T')");
                
                // Check that the last field in the header is the key ('K')"
                if (fieldNum == (lines.length - 1) && headerField.charAt(0) != 'K')
                    throw new RuntimeException(
                            "The last field in the header must be the key ('K')");
                
                List<String> l = headerMap.get(headerField.charAt(0));
                if (l == null) {
                    headerMap.put(headerField.charAt(0), l = new ArrayList<String>());	
                } 
                else if (headerField.charAt(0) != 'V') {  // MultiMap functionality so we can chain multiple voices
                    throw new RuntimeException(
                            "Duplicate instances of the header field " +  headerField.charAt(0));
                }
                l.add(headerText);
                
               fieldNum++;
            }		
            else
                throw new IllegalArgumentException("Unrecognized field type: "+ headerField);
        }

        this.headerMap = headerMap;		
    }
    
    /**
     * Getter method for the hash map with header fields and values
     * @return headerMap a Hash map where the keys the header fields and the value 
     * is the the filed's value
     */
    public Map<Character,List<String>> getHeaderMap() {
        return headerMap;
    }

    /**
     * Helper method to determine if a character is a valid header character
     * @param field Character representing a header field of an abc music file
     * @return True if the character is a valid header character. Otherwise, returns False.
     */
    public boolean isValid(Character field) { 
        //  checks if the header field is valid, by checking if it is one of the below
        return (   field == 'T' 
                || field == 'K' 
                || field == 'X' 
                || field == 'V' 
                || field == 'M' 
                || field == 'C' 
                || field == 'L' 
                || field == 'K' 
                || field == 'Q');
    }
}
