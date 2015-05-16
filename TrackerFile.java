/* 
 * Class TrackerFile
 * Megan Maher and Nicole Morin
 * Bowdoin College Class of 2016
 * Distributed Systems
 *
 * Created: April 27, 2015
 * Last Modified: April 27, 2015
 *
 * Holds a list with the same number of elements as the number of pieces
 * of that file. AKA the number of pieces we break the file up into.
 * Then, for each of these elements, we have a list of userIDs that have
 * that piece of the file.
 */


import java.util.*;

public class TrackerFile {

	private static String fileName;
	private static ArrayList<String> peersWithFile;
	private static int numPieces;

	public TrackerFile(String fileName, String peerName, int peerPort, String peerHost, int fileSize, int numPieces) {
		this.peersWithFile = new ArrayList<String>();
		this.fileName = fileName;
		String size = Integer.toString(fileSize);
		String temp = peerName + ":" + Integer.toString(peerPort) + ";" + peerHost;
		this.peersWithFile.add(size);
		this.peersWithFile.add(temp);
		this.numPieces = numPieces;
    }

	public int getNumPieces() { return numPieces; }

	public String peerListToString() {
		String listString = "";
		for (String s : peersWithFile) {
			    listString += s + "\t";
		}
		return listString;
	}

	public ArrayList<String> getPeerList() { return peersWithFile; }

	public void addPeer(String name, int portNum, String host) { 
		String temp = name + ":" + Integer.toString(portNum) + ";" + host;
		peersWithFile.add(temp);
	}


}

