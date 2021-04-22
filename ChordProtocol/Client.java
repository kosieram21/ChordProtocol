package ChordProtocol;

import java.rmi.Naming;
import java.util.List;

public class Client {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        List<String> urls = URL_Loader.load(args[1]);
        List<String[]> definitions = DictionaryLoader.load(args[2]);

        for(String url : urls)
            System.out.println(url);

        System.out.println("----------------------------------------------------");

        assert definitions != null;
        for(String[] definition : definitions)
            System.out.printf("%s: %s%n", definition[0], definition[1]);

        /*for(int i = 1; i < args.length; i++) {
            String service_name = String.format("//%s:%d/%s", args[i], port, "Chord");
            System.out.println(service_name);
            INode node = (INode) Naming.lookup(service_name);
            System.out.println("----------------------------------------------------");
            System.out.println(node.printFingerTable());
        }*/
    }
}