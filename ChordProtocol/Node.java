package ChordProtocol;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Node implements INode {
    static class Finger {
        private String _nodeURL;
        private int _nodeId;

        public String getNodeURL() { return _nodeURL; }
        public void setNodeURL(String nodeURL) { _nodeURL = nodeURL; }

        public int getNodeId() { return _nodeId; }
        public void setNodeId(int nodeId) { _nodeId = nodeId; }
    }

    public static String SERVICE_NAME = "Chord";

    private final int _nodeId;
    private final String _nodeURL;
    private final int _m;
    private final int _port;

    private String _successorURL;
    private String _predecessorURL;
    private Finger[] _fingers;

    public Node(int nodeId, int m, int port) throws UnknownHostException {
        _nodeId = nodeId;
        _nodeURL = InetAddress.getLocalHost().getHostName();
        _m = m;
        _port = port;
        _fingers = new Finger[m + 1];
    }

    @Override
    public String findSuccessor(int key) throws RemoteException, MalformedURLException, NotBoundException {
        String nPrimeURL = findPredecessor(key);
        INode nPrime = getNode(nPrimeURL);
        return nPrime.getSuccessorURL();
    }

    @Override
    public String findPredecessor(int key) throws RemoteException, MalformedURLException, NotBoundException {
        String nPrimeURL = _nodeURL;
        INode nPrime = getNode(nPrimeURL);

        String nPrimeSuccessorURL = nPrime.getSuccessorURL();
        INode nPrimeSuccessor = getNode(nPrimeSuccessorURL);
        while ( !(nPrime.getNodeId() < key &&
                  nPrimeSuccessor.getNodeId() >= key)) {

            nPrimeURL = nPrime.closestPrecedingFinger(key);
            nPrime = getNode(nPrimeURL);

            nPrimeSuccessorURL = nPrime.getSuccessorURL();
            nPrimeSuccessor = getNode(nPrimeSuccessorURL);
        }

        return nPrimeURL;
    }

    @Override
    public String closestPrecedingFinger(int key) throws RemoteException {
        int thisNodeID = getNodeId();
        for (int i = _m; i >= 1; i--) {
            int nodeID = _fingers[i].getNodeId();
            if (nodeID > thisNodeID && nodeID < key)
                return _fingers[i].getNodeURL();
        }

        return _nodeURL;
    }

    @Override
    public int getNodeId() throws RemoteException {
        return _nodeId;
    }

    @Override
    public String getSuccessorURL() throws RemoteException {
        return _successorURL;
    }

    @Override
    public void setSuccessorURL(String successorURL) throws RemoteException {
        _successorURL = successorURL;
    }

    @Override
    public String getPredecessorURL() throws RemoteException {
        return _predecessorURL;
    }

    @Override
    public void setPredecessorURL(String predecessorURL) throws RemoteException {
        _predecessorURL = predecessorURL;
    }

    @Override
    public void join(String nodeURL) throws RemoteException, MalformedURLException, NotBoundException {
        // use node url to get INode peer
        // nodeURL should point to peer 0 which will act as look manage
        if(!nodeURL.equals("")) {
            INode nPrime = getNode(nodeURL);
            initFingerTable(nPrime);
            updateOthers();
        }
        else {
            for(int i = 1; i <= _m; i++)
                _fingers[i].setNodeURL(_nodeURL);
            _predecessorURL = _nodeURL;
        }
    }

    private void initFingerTable(INode nPrime) throws RemoteException, MalformedURLException, NotBoundException {
        String finger1NodeURL = nPrime.findSuccessor(getFingerStart(1));
        INode finger1Node = getNode(finger1NodeURL);
        _fingers[1].setNodeURL(finger1NodeURL);
        _fingers[1].setNodeId(finger1Node.getNodeId());

        String successorURL = nPrime.getSuccessorURL();
        INode successor = getNode(successorURL);
        _predecessorURL = successor.getPredecessorURL();
        successor.setPredecessorURL(_nodeURL);

        for(int i = 1; i < _m; i++) {
            if( getNodeId() < getFingerStart(i + 1) &&
                    getFingerStart(i + 1) <= _fingers[i + 1].getNodeId()) {

                _fingers[i + 1].setNodeURL(_fingers[i].getNodeURL());
                _fingers[i + 1].setNodeId(_fingers[i].getNodeId());
            }
            else {
                String finger_iPlus1_NodeURL = nPrime.findPredecessor(getFingerStart(i + 1));
                INode finger_iPlus1_Node = getNode(finger_iPlus1_NodeURL);
                _fingers[i + 1].setNodeURL(finger_iPlus1_NodeURL);
                _fingers[i + 1].setNodeId(finger_iPlus1_Node.getNodeId());
            }
        }
    }

    private void updateOthers() throws RemoteException, MalformedURLException, NotBoundException {
        for(int i = 1; i <= _m; i++) {
            String predecessorURL = findPredecessor((int)(getNodeId() - (Math.pow(2, i) + 1)));
            INode predecessor = getNode(predecessorURL);
            predecessor.updateFingerTable(_nodeURL, getNodeId(), i);
        }
    }

    @Override
    public void updateFingerTable(String url, int s, int i) throws RemoteException, MalformedURLException, NotBoundException {
        if( getFingerStart(i) <= s &&
            s <= _fingers[i].getNodeId()) {
            _fingers[i].setNodeURL(url);
            _fingers[i].setNodeId(s);

            String predecessorURL = getPredecessorURL();
            INode predecessor = getNode(predecessorURL);
            predecessor.updateFingerTable(url, s, i);
        }
    }

    @Override
    public void joinFinished(String nodeURL) throws RemoteException {
        // tell nodeURL (should be peer 0) to release the join lock
    }

    @Override
    public boolean insert(String word, String definition) throws RemoteException {
        return false; // Shane implement
    }

    @Override
    public String lookup(String word) throws RemoteException {
        return null; // Shane implement
    }

    @Override
    public String printFingerTable() throws RemoteException {
        return null;
    }

    @Override
    public String printDictionary() throws RemoteException {
        return null;
    }

    private int getFingerStart(int i) {
        return (_nodeId + (int)Math.pow(2, i - 1)) % (int)Math.pow(2, _m);
    }

    private INode getNode(String nodeURL) throws RemoteException, NotBoundException, MalformedURLException {
        String service_name = String.format("//%s:%d/%s", nodeURL, _port, SERVICE_NAME);
        return (INode) Naming.lookup(service_name);
    }

    public static void main(String[] args) throws UnknownHostException, RemoteException, AlreadyBoundException, MalformedURLException, NotBoundException {
        if(args.length != 3 && args.length != 4) throw new RuntimeException("Syntax: Server node-id m port [bootstrap-url]");
        final int node_id = Integer.parseInt(args[0]);
        final int m = Integer.parseInt(args[1]);
        final int port = Integer.parseInt(args[2]);
        final String bootstrapURL = args.length == 4 ? args[3] : "";

        Node node = new Node(node_id, m, port);
        INode stub = (INode) UnicastRemoteObject.exportObject(node, 0);

        Registry registry;
        try {
            LocateRegistry.createRegistry(port);
            registry = LocateRegistry.getRegistry(port);
        }
        catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(port);
        }
        registry.bind(SERVICE_NAME, stub);

        node.join(bootstrapURL);
    }
}