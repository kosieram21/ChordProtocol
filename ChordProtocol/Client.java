package ChordProtocol;

import java.rmi.Naming;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        String url = args[1];

        String service_name = String.format("//%s:%d/%s", url, port, "Chord");
        INode node = (INode) Naming.lookup(service_name);

        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("Enter word to lookup (type exit to escape): ");
            String input = scanner.nextLine();
            if(input.equals("exit")) break;
            System.out.printf("Result: %s%n", node.lookup(input));
        }

        /*for(int i = 1; i < args.length; i++) {
            String service_name = String.format("//%s:%d/%s", args[i], port, "Chord");
            System.out.println(service_name);
            INode node = (INode) Naming.lookup(service_name);
            System.out.println("----------------------------------------------------");
            System.out.println(node.printFingerTable());
        }*/
    }
}