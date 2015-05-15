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


	public TrackerFile(String fileName_, String peerName, int peerPort, int fileSize, int numPieces_) {
		peersWithFile = new ArrayList<String>();
		fileName = fileName_;
		String size = Integer.toString(fileSize);
		String temp = peerName + ":" + Integer.toString(peerPort);
		peersWithFile.add(size);
		peersWithFile.add(temp);
		numPieces = numPieces_;
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

	public void addPeer(String name, int portNum) { 
		String temp = name + ":" + Integer.toString(portNum);
		peersWithFile.add(temp);
	}


}

