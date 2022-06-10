package Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayUtils {
    public static String[] GetReminder(String[] source, String[] part){
        var result = new ArrayList<String>();
        for (var element: source) {
            if (Arrays.stream(part).noneMatch(element::equals))
                result.add(element);
        }
        return result.toArray(String[]::new);
    }
}
