package ChordProtocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DictionaryLoader {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        String url = args[1];
        List<String[]> definitions = DictionaryLoader.load(args[2]);

        String service_name = String.format("//%s:%d/%s", url, port, "Chord");
        INode node = (INode) Naming.lookup(service_name);

        for(String[] definition : definitions)
            node.insert(definition[0], definition[1]);
    }

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