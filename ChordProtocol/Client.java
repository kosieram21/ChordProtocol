package ChordProtocol;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        String url = args[1];

        String service_name = String.format("//%s:%d/%s", url, port, "Chord");
        INode node = (INode) Naming.lookup(service_name);

        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("Enter 1 to lookup, 2 to exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();
            if(choice.equals("1")) {
                System.out.print("Enter a word: ");
                String word = scanner.next();
                System.out.printf("Result: %s%n", node.lookup(word));
            }
            else if(choice.equals("2")) {
                System.out.print("Exiting....");
                break;
            }
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