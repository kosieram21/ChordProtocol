package ChordProtocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.*;

public class Node implements INode {
    static class Finger {
        private String _nodeURL;
        private ModuloInteger _nodeId;

        public String getNodeURL() { return _nodeURL; }
        public void setNodeURL(String nodeURL) { _nodeURL = nodeURL; }

        public ModuloInteger getNodeId() { return _nodeId; }
        public void setNodeId(ModuloInteger nodeId) { _nodeId = nodeId; }
    }

    public static String SERVICE_NAME = "Chord";

    private final ModuloInteger _nodeId;
    private final String _nodeURL;
    private final int _m;
    private final int _modulo;
    private final int _port;

    private String _successorURL;
    private String _predecessorURL;
    private final Finger[] _fingers;

    private final HashMap<String, String> _dictionary;
    private final Semaphore _semaphore;

    private final Logger _logger;

    public Node(int nodeId, int m, int port) throws IOException {
        LogManager.getLogManager().reset();
        _logger = Logger.getLogger("ChordNode");

        CustomLogFormatter formatter = new CustomLogFormatter();

        FileHandler fileHandler = new FileHandler(String.format("%s-%d.txt", _logger.getName(), nodeId));
        fileHandler.setFormatter(formatter);
        _logger.addHandler(fileHandler);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        _logger.addHandler(consoleHandler);

        _nodeURL = InetAddress.getLocalHost().getHostName();
        _port = port;
        _m = m;
        _modulo = (int)Math.pow(2, _m);
        _nodeId = new ModuloInteger(nodeId, _modulo);

        _fingers = new Finger[m + 1];
        for(int i = 1; i <= m; i++) _fingers[i] = new Finger();

        _dictionary = new HashMap<String, String>();
        _semaphore = new Semaphore(1, true);
    }

    @Override
    public String findSuccessor(ModuloInteger key) throws RemoteException, MalformedURLException, NotBoundException {
        _logger.info(String.format("COMMAND [key = %d]" , key.getUnModdedValue()));

        String nPrimeURL = findPredecessor(key);
        INode nPrime = getNode(nPrimeURL);
        String successorURL = nPrime.getSuccessorURL();

        _logger.info(String.format("RESPONSE [successorURL = %s]", successorURL));
        return successorURL;
    }

    @Override
    public String findPredecessor(ModuloInteger key) throws RemoteException, MalformedURLException, NotBoundException {
        _logger.info(String.format("COMMAND [key = %d]", key.getUnModdedValue()));

        String nPrimeURL = _nodeURL;
        INode nPrime = getNode(nPrimeURL);

        String nPrimeSuccessorURL = nPrime.getSuccessorURL();
        INode nPrimeSuccessor = getNode(nPrimeSuccessorURL);

        while ( !key.inRange(
                Inclusivity.Exclusive, nPrime.getNodeId().getUnModdedValue(),
                Inclusivity.Inclusive, nPrimeSuccessor.getNodeId().getUnModdedValue()) )
        {
            _logger.info(String.format("CURRENT-N-PRIME [nPrimeURL = %s]" , nPrimeURL));
            _logger.info(String.format("CURRENT-N-PRIME-SUCCESSOR [nPrimeSuccessorURL = %s]" , nPrimeSuccessorURL));

            nPrimeURL = nPrime.closestPrecedingFinger(key);
            nPrime = getNode(nPrimeURL);

            nPrimeSuccessorURL = nPrime.getSuccessorURL();
            nPrimeSuccessor = getNode(nPrimeSuccessorURL);
        }

        _logger.info(String.format("RESPONSE [nPrimeURL = %s]", nPrimeURL));
        return nPrimeURL;
    }

    @Override
    public String closestPrecedingFinger(ModuloInteger key) throws RemoteException {
        _logger.info(String.format("COMMAND [key = %d]", key.getUnModdedValue()));
        String closestPrecedingFingerURL = _nodeURL;

        for (int i = _m; i >= 1; i--) {
            Finger finger = _fingers[i];

//            int correctedFingerID = moduloFingerCorrection(finger.getNodeId(), getNodeId());
//            _logger.info(String.format("FINGER-CORRECTION [fingerID = %d | correctedFingerID = %s]", finger.getNodeId(), correctedFingerID));

            if ( finger.getNodeId().inRange(
                    Inclusivity.Exclusive, getNodeId().getUnModdedValue(),
                    Inclusivity.Exclusive, key.getUnModdedValue()) )
            {
                closestPrecedingFingerURL = finger.getNodeURL();
                break;
            }

//            _logger.info(String.format("CURRENT-FINGER [fingerID = %d | fingerURL = %s]", finger.getNodeId(), finger.getNodeURL()));
        }

        _logger.info(String.format("RESPONSE [closestPrecedingFingerURL = %s]", closestPrecedingFingerURL));
        return closestPrecedingFingerURL;
    }

    @Override
    public String getNodeURL() throws RemoteException { return _nodeURL; }

    @Override
    public ModuloInteger getNodeId() throws RemoteException { return _nodeId; }

    @Override
    public String getSuccessorURL() throws RemoteException { return _successorURL; }

    @Override
    public void setSuccessorURL(String successorURL) throws RemoteException { _successorURL = successorURL; }

    @Override
    public String getPredecessorURL() throws RemoteException { return _predecessorURL; }

    @Override
    public void setPredecessorURL(String predecessorURL) throws RemoteException { _predecessorURL = predecessorURL; }

    @Override
    public void join(String nodeURL) throws RemoteException, MalformedURLException, NotBoundException, InterruptedException {
        _logger.info(String.format("COMMAND [nodeURL = %s]", nodeURL));

        if(!nodeURL.equals("")) {
            INode nPrime = getNode(nodeURL);
            nPrime.acquireLock();
            initFingerTable(nPrime);
            updateOthers();
            nPrime.releaseLock();
        }
        else {
            for(int i = 1; i <= _m; i++) _fingers[i].setNodeURL(_nodeURL);
            setSuccessorURL(_nodeURL);
            setPredecessorURL(_nodeURL);
        }
    }

    private void initFingerTable(INode nPrime) throws RemoteException, MalformedURLException, NotBoundException {
        _logger.info(String.format("COMMAND [nPrimeURL = %s]", nPrime.getNodeURL()));

        String finger1NodeURL = nPrime.findSuccessor(getFingerStart(1));
        INode finger1Node = getNode(finger1NodeURL);
        _logger.info(String.format("FINGER [finger1NodeURL = %s]", finger1NodeURL));

        _fingers[1].setNodeURL(finger1NodeURL);
        _fingers[1].setNodeId(finger1Node.getNodeId());

        _logger.info(String.format(
                "UPDATE-FINGER [fingerIndex = %d | fingerURL = %s |  fingerID = %s]",
                1, finger1NodeURL, finger1Node.getNodeId()));

        setSuccessorURL(finger1NodeURL);
        String successorURL = getSuccessorURL();
        INode successor = getNode(successorURL);
        _logger.info(String.format("GET-SUCCESSOR [successorURL = %s | successorID = %s]", successorURL, successor.getNodeId()));

        _predecessorURL = successor.getPredecessorURL();
        INode predecessor = getNode(_predecessorURL);
        successor.setPredecessorURL(_nodeURL);
        predecessor.setSuccessorURL(_nodeURL);
        _logger.info(String.format("GET-PREDECESSOR [predecessorURL = %s | predecessorID = %s]", _predecessorURL, predecessor.getNodeId()));

        for(int i = 1; i < _m; i++) {
            ModuloInteger fingerStart = getFingerStart(i + 1);
            Finger finger = _fingers[i];
//            int correctedFingerStart = moduloFingerCorrection(fingerStart, getNodeId());
//            _logger.info(String.format("FINGER-CORRECTION [fingerStart = %d | correctedFingerStart = %s]", fingerStart, correctedFingerStart));

            if( fingerStart.inRange(
                    Inclusivity.Exclusive, getNodeId().getUnModdedValue(),
                    Inclusivity.Inclusive, finger.getNodeId().getUnModdedValue()) )
            {
                String finger_iPlus1_NodeURL = finger.getNodeURL();
                ModuloInteger finger_iPlus1_NodeID = finger.getNodeId();

                _fingers[i + 1].setNodeURL(finger_iPlus1_NodeURL);
                _fingers[i + 1].setNodeId(finger_iPlus1_NodeID);

                _logger.info(String.format(
                        "UPDATE-FINGER [fingerIndex = %d | fingerURL = %s |  fingerID = %s]",
                        i + 1, finger_iPlus1_NodeURL, finger_iPlus1_NodeID));
            }
            else {
                String finger_iPlus1_NodeURL = nPrime.findPredecessor(fingerStart);
                INode finger_iPlus1_Node = getNode(finger_iPlus1_NodeURL);
                _fingers[i + 1].setNodeURL(finger_iPlus1_NodeURL);
                _fingers[i + 1].setNodeId(finger_iPlus1_Node.getNodeId());

                _logger.info(String.format(
                        "UPDATE-FINGER [fingerIndex = %d | fingerURL = %s |  fingerID = %s]",
                        i + 1, finger_iPlus1_NodeURL, finger_iPlus1_Node.getNodeId()));
            }
        }
    }

    private void updateOthers() throws RemoteException, MalformedURLException, NotBoundException {
        _logger.info("COMMAND");
        _logger.setLevel(Level.ALL);
        for(int i = 1; i <= _m; i++) {

            ModuloInteger key = new ModuloInteger(
                    (int) (getNodeId().getUnModdedValue() - Math.pow(2, i - 1) + 1),
                    _modulo);

            String predecessorURL = findPredecessor(key);
            INode predecessor = getNode(predecessorURL);
            _logger.info(String.format("CURRENT-PREDECESSOR [predecessorURL = %s]", predecessorURL));

            predecessor.updateFingerTable(_nodeURL, getNodeId(), new ModuloInteger(i, _modulo));
        }
        _logger.setLevel(Level.INFO);
    }

    @Override
    public void updateFingerTable(String url, ModuloInteger nodeId, ModuloInteger fingerIndex) throws RemoteException, MalformedURLException, NotBoundException {
        _logger.info(String.format("COMMAND [URL = %s | nodeID = %d | fingerIndex = %d]",
                url, nodeId.getUnModdedValue(), fingerIndex.getUnModdedValue()));

        Finger finger = _fingers[fingerIndex.getUnModdedValue()];
        if( nodeId.inRange(Inclusivity.Inclusive, getFingerStart( fingerIndex.getUnModdedValue() ).getUnModdedValue(),
                Inclusivity.Exclusive, finger.getNodeId().getUnModdedValue()) )
        {
            _logger.info(String.format("UPDATE-OCCURRED [fingerID = %d, fingerURL = %s]",
                    finger.getNodeId().getUnModdedValue(), finger.getNodeURL()));

            finger.setNodeURL(url);
            finger.setNodeId(nodeId);

            String predecessorURL = getPredecessorURL();
            if (!predecessorURL.equals(url)) {
                INode predecessor = getNode(predecessorURL);
                predecessor.updateFingerTable(url, nodeId, fingerIndex);
            }

        }
    }

    @Override
    public void acquireLock() throws RemoteException, InterruptedException { _semaphore.acquire(); }

    @Override
    public void releaseLock() throws RemoteException { _semaphore.release(); }

    @Override
    public void insert(String word, String definition) throws RemoteException, MalformedURLException, NotBoundException {
        _logger.info(String.format("COMMAND [word = %s | definition = %s]", word, definition));

        int hash = FNV1aHash.hash32(word) % _modulo;
        _logger.info(String.format("HASH [hash32 = %d]", hash));

        String successorURL = findSuccessor( new ModuloInteger(hash, _modulo) );
        INode successor = getNode(successorURL);

        if(successorURL.equals(_nodeURL)) _dictionary.put(word, definition);
        else successor.insert(word, definition);
    }

    @Override
    public String lookup(String word) throws RemoteException, MalformedURLException, NotBoundException {
        _logger.info(String.format("COMMAND [word = %s]", word));

        int hash = FNV1aHash.hash32(word) % _modulo;
        _logger.info(String.format("HASH [hash32 = %d]", hash));

        String successorURL = findSuccessor( new ModuloInteger(hash, _modulo) );
        INode successor = getNode(successorURL);

        String definition;
        if(successorURL.equals(_nodeURL)) definition = _dictionary.get(word);
        else definition = successor.lookup(word);

        _logger.info(String.format("RESPONSE [definition = %s]", definition));
        return definition;
    }

    @Override
    public String printFingerTable() throws RemoteException {
        StringBuilder text = new StringBuilder();
        text.append("=================================\n");

        text.append("Node ID | Start Finger | Node URL\n");
        for (int i = 1; i <= _m; i++) {
            int nodeId = _fingers[i].getNodeId().getUnModdedValue();
            int start = getFingerStart(i).getUnModdedValue();
            String nodeURL = _fingers[i].getNodeURL();
            text.append(String.format("%d | %d | %s\n", nodeId, start, nodeURL));
        }
        text.append("\n");
        text.append("Successor | Predecessor \n");
        text.append(String.format("%s | %s\n", getSuccessorURL(), getPredecessorURL()));

        text.append("=================================\n");
        return text.toString();
    }

    @Override
    public String printDictionary() throws RemoteException {
        StringBuilder text = new StringBuilder();
        text.append("=================================\n");

        text.append("Word | Definition\n");
        for (HashMap.Entry<String,String> entry : _dictionary.entrySet()) {
            String word = entry.getKey();
            String definition = entry.getValue();
            text.append(String.format("%s | %s\n", word, definition));
        }

        text.append("=================================\n");
        return text.toString();
    }

    private ModuloInteger getFingerStart(int i) throws RemoteException {
        _logger.info(String.format("COMMAND [nodeID = %d | fingerIndex = %d]",
                getNodeId().getUnModdedValue(), i));

        ModuloInteger start = new ModuloInteger(
                _nodeId.getUnModdedValue() + (int)Math.pow(2, i - 1),
                _modulo);

        _logger.info(String.format("RESPONSE [fingerStart = %d]", start.getModdedValue()));
        return start;
    }

    private INode getNode(String nodeURL) throws RemoteException, NotBoundException, MalformedURLException {
        String service_name = String.format("//%s:%d/%s", nodeURL, _port, SERVICE_NAME);
        return (INode) Naming.lookup(service_name);
    }

    public static void main(String[] args) throws IOException, AlreadyBoundException, NotBoundException, InterruptedException {
        if(args.length != 3 && args.length != 4) throw new RuntimeException("Syntax: Server node-id m port [bootstrap-url]");
        final int node_id = Integer.parseInt(args[0]);
        final int m = Integer.parseInt(args[1]);
        final int port = Integer.parseInt(args[2]);
        final String bootstrapURL = args.length == 4 ? args[3] : "";

        System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostName());
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
        System.out.println(node.printFingerTable());
    }
}