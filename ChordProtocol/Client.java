package ChordProtocol;

import java.rmi.Naming;

public class Client {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        for(int i = 1; i < args.length; i++) {
            String service_name = String.format("//%s:%d/%s", args[i], port, "Chord");
            System.out.println(service_name);
            INode node = (INode) Naming.lookup(service_name);
            System.out.println("----------------------------------------------------");
            System.out.println(node.printFingerTable());
        }
    }
}