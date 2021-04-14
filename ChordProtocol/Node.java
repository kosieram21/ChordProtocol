package ChordProtocol;

import java.rmi.RemoteException;

public class Node implements INode {
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
    public String successor() throws RemoteException {
        return null;
    }

    @Override
    public String predecessor() throws RemoteException {
        return null;
    }

    @Override
    public boolean join(String nodeURL) throws RemoteException {
        // use node url to get INode peer
        // nodeURL should point to peer 0 which will act as look manage
        INode nPrime = null;
        if(nPrime != null) {
            init_finger_table(nPrime);
            update_others();
            return false;
        }
        else {
            return true;
        }
    }

    private void init_finger_table(INode nPrime) {

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