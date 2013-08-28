package auxiliary;

import java.util.List;
import java.util.Map;

import player.HeadLexer;

public class PrintMap {
    /**
     * Helper function to print keys and values of a Map
     * @param String s
     */
    public static void printMap(String s) {
        HeadLexer h = new HeadLexer(s);
        Map<Character,List<String>> test = h.getHeaderMap();
        for (Character k : test.keySet()) {
            String key = k.toString();
            String value = test.get(k).toString();
            System.out.println(key + " " + value);
        }
    }
}
