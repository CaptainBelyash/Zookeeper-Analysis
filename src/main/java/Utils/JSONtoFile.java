package Utils;

import com.alibaba.fastjson.JSON;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class JSONtoFile {
    public static void PrintToFile(Object object, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(JSON.toJSONString(object));

        writer.close();
    }
}
