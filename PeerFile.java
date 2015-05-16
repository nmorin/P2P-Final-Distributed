import java.util.*;

public class PeerFile {

	private static int NOPIECE = 0;
	private static int HASPIECE = 1;
	private static int DOWNLOADING = 2;

	private static String fileName;
	private static int numPieces;
	private static ArrayList<Integer> completePieces;
	private static ArrayList<Integer> downloadingPieces;
	private static ArrayList<Integer> neededPieces;
	private static ArrayList<String> currentlyDownloadingFrom;
	private static int size;
	private static int[] fileBitPieces;

	// Called after initiating a file request
	public PeerFile(String fileName, int numPieces, int size, boolean downloadingFile) {
		this.fileName = fileName;
		this.size = size;
		this.numPieces = numPieces;
		this.completePieces = new ArrayList<Integer>();
		this.neededPieces = new ArrayList<Integer>();
		this.downloadingPieces = new ArrayList<Integer>();
		this.currentlyDownloadingFrom = new ArrayList<String>();
		fileBitPieces = new int[numPieces];
		if (downloadingFile) {
			for (int i = 0; i < numPieces; i++) {
				this.neededPieces.add((Integer)i);
				fileBitPieces[i] = NOPIECE;
			}
		} else {
			// Assume seeding
			for (int i = 0; i < numPieces; i++) {
				this.completePieces.add((Integer)i);
				fileBitPieces[i] = HASPIECE;
			}
		}
	}

	public ArrayList<String> getCurrentlyDownloadingFrom() { return currentlyDownloadingFrom; }

	public ArrayList<Integer> getPiecesNeeded() { return neededPieces; }

	public ArrayList<Integer> getCompletePieces() { return completePieces; }

	public int getNumComplete() { return completePieces.size(); }

	public ArrayList<Integer> getDownloadingPieces() { return downloadingPieces; }

	public int getPieceValue(int pieceNum) { return fileBitPieces[pieceNum]; }

	public int getSize() { return size; }

	public int getNumPieces() { return numPieces; }

	public boolean isDownloadingPiece(int pieceNum) { return (fileBitPieces[pieceNum] == DOWNLOADING); }

	public boolean hasFinishedPiece(int pieceNum) { return (fileBitPieces[pieceNum] == HASPIECE); }

	public boolean needsPiece(int pieceNum) { return (fileBitPieces[pieceNum] == NOPIECE); }

	public void finishedDownloadingPiece(int pieceNum, String peerName) {
		downloadingPieces.remove((Integer)pieceNum);
		completePieces.add((Integer)pieceNum);
		fileBitPieces[pieceNum] = HASPIECE;
		currentlyDownloadingFrom.remove(peerName);
	}

	public void startDownloadingPiece(int pieceNum, String peerName) {
		System.out.println("Needed pieces before:");
		printIList(neededPieces, "neededPieces");
		fileBitPieces[pieceNum] = DOWNLOADING;
		neededPieces.remove((Integer)pieceNum);
		downloadingPieces.add((Integer)pieceNum);
		currentlyDownloadingFrom.add(peerName);
		System.out.println("removed piece. Here is neededPieces now:");
		printIList(neededPieces, "neededPieces");
	}
 
 	public void noLongerDownloadingPiece(int pieceNum, String peerName) {
		fileBitPieces[pieceNum] = NOPIECE;
		neededPieces.add((Integer)pieceNum);
		downloadingPieces.remove((Integer)pieceNum);
		currentlyDownloadingFrom.remove(peerName);
 	}

 	public void printLists() {
 		printSList(currentlyDownloadingFrom, "currentDownloadingFrom");
 		printIList(neededPieces, "neededPieces");
 		printIList(completePieces, "completePieces");
 		printIList(downloadingPieces, "downloadingPieces");
 	}

	private void printSList(ArrayList<String> list, String name) {
		System.out.println(name + ": ");
		for (String elem : list) {
			System.out.print(elem + ", ");
		}
	}

	private void printIList(ArrayList<Integer> list, String name) {
		System.out.println(name + ": ");
		for (Integer elem : list) {
			System.out.print(elem + ", ");
		}
	}
}

