/*
 * PeerInterface for P2P System
 * 
 * Holds the remote methods that we can call on the Peer class
 * 
 * Megan Maher and Nicole Morin
 * Bowdoin College Class of 2016
 * Distributed Systems, Spring 2015
 * Last Modified: May 16, 2015
 *
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PeerInterface extends Remote {
        public byte[] requestFile(String fileName, int piece) throws RemoteException;
        public ArrayList<Integer> requestPieceInfo(String fileName) throws RemoteException;
}