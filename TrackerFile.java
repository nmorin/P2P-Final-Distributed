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
	private static List<String> peersWithFile;
	private static int fileSize;
	private static int numPieces;


	public TrackerFile(String fileName_, String peerName_, int peerPort_, int fileSize_, int numPieces_) {
		peersWithFile = new ArrayList<String>();
		fileName = fileName_;
		String size = Integer.toString(fileSize_);
		String temp = peerName_ + ":" + Integer.toString(peerPort_);
		peersWithFile.add(temp);
		fileSize = fileSize_;
		numPieces = numPieces_;
    }

	public int getNumPieces() { return numPieces; }

	public int getFileSize() { return fileSize; }

	public List<String> getPeerList() { return peersWithFile; }


}

