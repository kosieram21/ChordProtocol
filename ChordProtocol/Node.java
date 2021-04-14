package ChordProtocol;

import java.rmi.RemoteException;

public class Node implements INode {
    static class Finger {
        private int _start;
        private String _nodeURL;
        private int _nodeId;

        public int getStart() { return _start; }
        public void setStart(int start) { _start = start; }

        public String getNodeURL() { return _nodeURL; }
        public void setNodeURL(String nodeURL) { _nodeURL = nodeURL; }

        public int getNodeId() { return _nodeId; }
        public void setNodeId(int nodeId) { _nodeId = nodeId; }
    }

    private int _m;
    private String _nodeURL;
    private String _successorURL;
    private String _predecessorURL;
    private Finger[] _fingers;

    // TODO: need constructor

    @Override
    public String findSuccessor(int key, boolean traceFlag) throws RemoteException {
        return null; // Shane implement
    }

    @Override
    public String findPredecessor(int key) throws RemoteException {
        return null; // Shane implement
    }

    @Override
    public String closestPrecedingFinger(int key) throws RemoteException {
        return null; // Shane implement
    }

    @Override
    public int getNodeId() throws RemoteException {
        return 0; // TODO: implement
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
    public boolean join(String nodeURL) throws RemoteException {
        // use node url to get INode peer
        // nodeURL should point to peer 0 which will act as look manage
        INode nPrime = null;
        if(nPrime != null) {
            init_finger_table(nPrime);
            update_others();
        }
        else {
            for(int i = 1; i <= _m; i++)
                _fingers[i].setNodeURL(_nodeURL);
            _predecessorURL = _nodeURL;
        }

        return true; // return false if we can't find node URL?
    }

    private void init_finger_table(INode nPrime) throws RemoteException {
        _fingers[1].setNodeURL(nPrime.findSuccessor(_fingers[1].getStart(), false)); // TODO: what to do about trace flag?

        String successorURL = nPrime.getSuccessorURL();
        INode successor = null; // TODO: use successor URL to get INode successor
        _predecessorURL = successor.getPredecessorURL();
        successor.setPredecessorURL(_nodeURL);

        for(int i = 1; i < _m; i++) {
            if( getNodeId() < _fingers[i + 1].getStart() &&
                _fingers[i + 1].getStart() <= _fingers[i + 1].getNodeId())
                _fingers[i + 1].setNodeURL(_fingers[i].getNodeURL());
            else
                _fingers[i + 1].setNodeURL(nPrime.findSuccessor(_fingers[i + 1].getStart(), false)); // TODO: what to do about trace flag/
        }
    }

    private void update_others() {

    }

    @Override
    public boolean joinFinished(String nodeURL) throws RemoteException {
        // tell nodeURL (should be peer 0) to release the join lock
        return false;
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
}