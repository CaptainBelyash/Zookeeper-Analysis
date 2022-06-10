package Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ArrayUtils {
    public static String[] GetReminder(String[] source, String[] part){
        var result = new ArrayList<String>();
        for (var element: source) {
            if (Arrays.stream(part).noneMatch(element::equals))
                result.add(element);
        }
        return result.toArray(String[]::new);
    }

    public static String[] GetRandomSlice(String[] source, int count) {
        var givenList = new ArrayList<>(Arrays.asList(source));
        Random rand = new Random();
        for (var i = 0; i < source.length - count; i++)
            givenList.remove(rand.nextInt(givenList.size()));
        return givenList.toArray(String[]::new);
    }
}
