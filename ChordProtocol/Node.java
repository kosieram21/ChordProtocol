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
        return false;
    }

    @Override
    public boolean joinFinished(String nodeURL) throws RemoteException {
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