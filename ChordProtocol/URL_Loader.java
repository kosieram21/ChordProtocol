package ChordProtocol;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;

public class URL_Loader {
    public static List<String> load(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        List<String> urls = new ArrayList<String>();
        while(scanner.hasNext())
            urls.add(scanner.nextLine());

        return urls;
    }
}
