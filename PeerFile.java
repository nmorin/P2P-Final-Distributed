/*
 * PeerFile in P2P system
 * 		A data structure for a specific file, that holds which pieces of the
 *		file have been downloaded, which pieces are currently being downloaded
 * 		and which pieces are still needed. It also keeps track of how many pieces
 *		the file is broken up into, according to the size specified in Peer.java.
 * 
 * Megan Maher and Nicole Morin
 * Bowdoin College Class of 2016
 * Distributed Systems, Spring 2015
 * Last Modified: May 16, 2015
 *
 */

import java.util.*;

public class PeerFile {

	private static int NOPIECE = 0;				// This piece has not been downloaded
	private static int HASPIECE = 1;			// This piece has been downloaded
	private static int DOWNLOADING = 2;			// This piece is currently being downloaded

	private static String fileName;
	private static int numPieces;
	private static ArrayList<Integer> completePieces;
	private static ArrayList<Integer> downloadingPieces;
	private static ArrayList<Integer> neededPieces;
	private static ArrayList<String> currentlyDownloadingFrom;
	private static int size;
	private static int[] fileBitPieces;
	private static int piecesComplete;

	// Called after initiating a file request
	public PeerFile(String fileName, int numPieces, int size, boolean downloadingFile) {
		this.fileName = fileName;
		this.size = size;
		this.numPieces = numPieces;
		this.completePieces = new ArrayList<Integer>();
		this.neededPieces = new ArrayList<Integer>();
		this.downloadingPieces = new ArrayList<Integer>();
		this.currentlyDownloadingFrom = new ArrayList<String>();
		this.fileBitPieces = new int[numPieces];
		if (downloadingFile) {
			for (int i = 0; i < numPieces; i++) {
				this.neededPieces.add((Integer)i);
				this.fileBitPieces[i] = NOPIECE;
			}
		} else {
			// Assume seeding
			for (int i = 0; i < numPieces; i++) {
				this.completePieces.add((Integer)i);
				this.fileBitPieces[i] = HASPIECE;
			}
		}
	}

	// Returns peerNames of who we are currently downloading from
	public ArrayList<String> getCurrentlyDownloadingFrom() { return currentlyDownloadingFrom; }

	// Returns integer list of which file pieces we still need
	public ArrayList<Integer> getPiecesNeeded() { return neededPieces; }

	// Returns integer list of file pieces we have already downloaded
	public ArrayList<Integer> getCompletePieces() { return completePieces; }

	// Returns the number of pieces we have finished downloading
	public int getNumComplete() { return completePieces.size(); }

	// Returns integer list of the file pieces we are currently downloading
	public ArrayList<Integer> getDownloadingPieces() { return downloadingPieces; }

	// Returns 0 if piece not downloaded, 1 if downloaded, 2 if currently downloading
	public int getPieceValue(int pieceNum) { return fileBitPieces[pieceNum]; }

	// Returns size of the file, in bytes
	public int getSize() { return size; }

	// Returns the number of pieces we have split this file up into
	public int getNumPieces() { return numPieces; }

	// Returns true if we are currently downloading this piece
	public boolean isDownloadingPiece(int pieceNum) { return (fileBitPieces[pieceNum] == DOWNLOADING); }

	// Returns true if we have finished downloading this piece
	public boolean hasFinishedPiece(int pieceNum) { return (fileBitPieces[pieceNum] == HASPIECE); }

	// Returns true if we still need this piece
	public boolean needsPiece(int pieceNum) { return (fileBitPieces[pieceNum] == NOPIECE); }

	// When we have finished downloading a piece from a Peer, we must update our data structures
	public void finishedDownloadingPiece(int pieceNum, String peerName) {
		downloadingPieces.remove((Integer)pieceNum);
		completePieces.add((Integer)pieceNum);
		fileBitPieces[pieceNum] = HASPIECE;
		currentlyDownloadingFrom.remove(peerName);
	}

	// When we start downloading a piece, update data structures
	public void startDownloadingPiece(int pieceNum, String peerName) {
		fileBitPieces[pieceNum] = DOWNLOADING;
		neededPieces.remove((Integer)pieceNum);
		downloadingPieces.add((Integer)pieceNum);
		currentlyDownloadingFrom.add(peerName);
	}
 
 	// When we do not completely download a piece, but have determined that we cannot download it anymore
 	public void noLongerDownloadingPiece(int pieceNum, String peerName) {
		fileBitPieces[pieceNum] = NOPIECE;
		neededPieces.add((Integer)pieceNum);
		downloadingPieces.remove((Integer)pieceNum);
		currentlyDownloadingFrom.remove(peerName);
 	}

 	// Will print all lists of data structures
 	public void printLists() {
 		printSList(currentlyDownloadingFrom, "currentDownloadingFrom");
 		printIList(neededPieces, "neededPieces");
 		printIList(completePieces, "completePieces");
 		printIList(downloadingPieces, "downloadingPieces");
 	}

 	// Prints a list of Strings
	private void printSList(ArrayList<String> list, String name) {
		System.out.print(name + ": ");
		for (String elem : list) {
			System.out.print(elem + ", ");
		}
		System.out.println("\n");
	}

	// Prints a list of Integers
	private void printIList(ArrayList<Integer> list, String name) {
		System.out.print(name + ": ");
		for (Integer elem : list) {
			System.out.print(elem + ", ");
		}
		System.out.println("\n");

	}
}

