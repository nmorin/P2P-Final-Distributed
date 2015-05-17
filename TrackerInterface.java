/*
 * TrackerInterface for P2P System
 * 
 * Holds the remote methods that we can call on the Tracker class
 * 
 * Megan Maher and Nicole Morin
 * Bowdoin College Class of 2016
 * Distributed Systems, Spring 2015
 * Last Modified: May 16, 2015
 *
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface TrackerInterface extends Remote {
        public ArrayList<String> query(String fileName, String name, int portNum, String host) throws RemoteException;
        public void seedFile(String fileName, String peerName, int peerPort, String host, int fileSize, int numPieces) throws RemoteException;
}