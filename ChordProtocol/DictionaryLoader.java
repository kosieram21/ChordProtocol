package ChordProtocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DictionaryLoader {
    public static List<String[]> load(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        List<String[]> definitions = new ArrayList<String[]>();
        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" : ");
            definitions.add(parts);
        }

        return definitions;
    }
}