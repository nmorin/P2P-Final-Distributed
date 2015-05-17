/*
 * Peer file for P2P System
 * Implements RMI
 *
 * 
 * Megan Maher and Nicole Morin
 * Bowdoin College Class of 2016
 * Distributed Systems, Spring 2015
 * Last Modified: May 16, 2015
 *
 */

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.*;

import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.util.ArrayList.*;
import java.util.Timer;
import java.util.concurrent.*;
import java.util.concurrent.TimeoutException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements PeerInterface {

    private static String myName;
    private static String myHost;
    private static int myPortNum;
    private static boolean rareTest = false;        // for testing only
    private static Timer timer;

    private static final int PIECE_SIZE = 6400;

    private static boolean alreadyConnectedToTracker = false;
    // private static final String TRACKER_IP = "localhost";
    // private static final String TRACKER_IP = "52.5.152.108";    // Virginia, 1
    // private static final String TRACKER_IP = "52.7.97.172";     // Virginia, 2
    private static final String TRACKER_IP = "52.7.147.25";     // Virginia, 3
    private static final String TRACKER_NAME = "Tracker";
    private static final int TRACKER_PORT = 6666;

    private static TrackerInterface leadTrackerStub;
    private static Map<String, PeerInterface> peerStubs;

    private static Map<String, PeerFile> myFiles;

    private static void print(String s) {
        System.out.println(s);
    }

	public Peer() {
        super();
        peerStubs = new HashMap<String, PeerInterface>();
        myFiles = new HashMap<String, PeerFile>();
	}

    // RMI Method -> handles another peer asking for a file piece
    public byte[] requestFile(String fileName, int piece) {
        try {
            RandomAccessFile file = new RandomAccessFile(fileName, "r");
            int size = myFiles.get(fileName).getSize();
            int numPieces = myFiles.get(fileName).getNumPieces();

            int offset = piece * PIECE_SIZE;
            int amountToRead = getThisPieceSize(myFiles.get(fileName), piece);
            byte[] fileBytes = new byte[amountToRead];

            // print("Length: " + size + " Piece size:" + PIECE_SIZE);
            // print("Numpieces: " + numPieces + " Offset: " + offset);
            // print("amountToRead: " + amountToRead);
            // print("\nGiving away " + fileName);

            file.seek((long) offset);
            file.read(fileBytes);

            String s = new String(fileBytes);
            // print("File bytes: " + s);
            file.close();
            return fileBytes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* The last piece of a file is smaller. Get the correct piece size */
    private static int getThisPieceSize(PeerFile file, int pieceNum) {
        if ((pieceNum+1)*PIECE_SIZE > file.getSize()) {
            return file.getSize() - (pieceNum)*PIECE_SIZE ;
        }
        return PIECE_SIZE;
    }

    /* (RMI METHOD) Returns a list of the pieces this peer has of the specified file */
    public ArrayList<Integer> requestPieceInfo(String fileName) {

        return myFiles.get(fileName).getCompletePieces();
    }

    /* Creates and binds an instance of the peer's registry so it can
    be accessed by others and make connections */
    private static void createAndBindSelf() {
         try {
            Peer peer = new Peer();
            PeerInterface peerStub = (PeerInterface) UnicastRemoteObject.exportObject(peer, 0);
            Registry registry = LocateRegistry.createRegistry(myPortNum);
            registry.bind(myName, peerStub);
            System.out.println("Binding complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Connects to a peer */
    private static void connectToPeer(String peerName, int peerPort, String host) {
        try {
            // Checks if peer is already bound or not
            PeerInterface temp = peerStubs.get(peerName);
            if (temp != null) { return; }

            System.out.println("\n Peername: " + peerName);
            System.out.println("\n Port: " + peerPort);

            Registry theirReg = LocateRegistry.getRegistry(host, peerPort);
            PeerInterface boundPeerStub = (PeerInterface) theirReg.lookup(peerName);
            peerStubs.put(peerName, boundPeerStub);
            System.out.println("Found peer " + peerName);

        } catch (Exception e) {
            // System.out.println("Peer doesn't exist right now. They have left the system!");
        }
    }

    /* Given a file size, returns the number of pieces of the constant
    piece size are in the file */
    private static int getNumPieces(int fileSize) {
        int size = fileSize / PIECE_SIZE;
        if ((double)fileSize / (double)PIECE_SIZE != 0) { size++; }
        return size;
    }

    // Connects to the Tracker with hardcoded IP address
    private static void connectToTracker() {
        if (alreadyConnectedToTracker) { return; }
        try {
            Registry trackerReg = LocateRegistry.getRegistry(TRACKER_IP, TRACKER_PORT);
            leadTrackerStub = (TrackerInterface) trackerReg.lookup(TRACKER_NAME);
            System.out.println("Found tracker!");
            alreadyConnectedToTracker = true;
        } catch (Exception e) {
            System.out.println("Exception occurred connecting to tracker");
            e.printStackTrace();
        }
    }

    /* Method to seed a file on the network */
    private static void seedFile(String fileName) {
        connectToTracker();
        File file = new File(fileName);
        if (!file.exists()) { 
            System.out.println("Error, file does not exist");
            return; 
        }
        int lengthInBytes = (int) file.length();
        int numPieces = getNumPieces(lengthInBytes);
        try {
            leadTrackerStub.seedFile(fileName, myName, myPortNum, myHost, lengthInBytes, numPieces);
            System.out.println("Seeding complete");
        } catch (Exception e) {
            System.out.println("Exception in seeding file");
            e.printStackTrace();
        }
        PeerFile newFile = new PeerFile(fileName, numPieces, lengthInBytes, false);
        myFiles.put(fileName, newFile);
    }

    // Just a helper method to print out a List of Lists!
    private static void printDoubleList(ArrayList<ArrayList<String>> list) {
        int counter = 0;
        for (ArrayList<String> firstList : list) {
            System.out.print("LIST " + counter + ": ");
            for (String element : firstList) {
                System.out.print(element + ", ");
            }
            System.out.println();
            counter++;
        }
    }

    /*
     * Connects to head tracker and asks for peers that have a certain file.
     * Tracker returns a list of peerInfo strings with "peerName,portNo"
     * I parse this list, and establish connections to peers with whom I have
     *     not already established a connection, then ask them what pieces
     *     they have that I still need.
     * Based on what 
     */
    private static void makeRequest(String fileName) {
        long startTime = System.nanoTime();
        try {
            connectToTracker();
            ArrayList<String> peersWithFile;
            peersWithFile = leadTrackerStub.query(fileName, myName, myPortNum, myHost);

            int fileSize = 0; //bytes
            if (peersWithFile != null) {
                // System.out.println("Found peer list!");
                fileSize = Integer.parseInt(peersWithFile.get(0));
                peersWithFile.remove(0);
            } else {
                System.out.println("File not found");
                return;
            }

            // returns num totalpieces 
            int numFilePieces = getNumPieces(fileSize);

            // Check if already have record of that file in myFiles; if not, add
            if (myFiles.get(fileName) == null) {
                PeerFile temp = new PeerFile(fileName, numFilePieces, fileSize, true);
                myFiles.put(fileName, temp);
            }

            // PieceBreakdown is a 2d list, that holds lists of strings;
            // Each index corresponds to a piece of the file
            // The elements of the inner array lists are peer information of peers with that pieces
            ArrayList<ArrayList<String>> pieceBreakdown = new ArrayList<ArrayList<String>>();
            pieceBreakdown.addAll(getFilePieces(fileName, numFilePieces, peersWithFile));

            long startDownloadTime = System.nanoTime();
            downloadFile(fileName, pieceBreakdown);
            long endDownloadTime = System.nanoTime();
            long downloadDuration = (endDownloadTime - startDownloadTime) / 1000000;
            print("\nDOWNLOADING " + fileName + " TOOK: " + downloadDuration + " MILLISECONDS");


        } catch (Exception e) {
            System.out.println("Exception in placing request");
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        print("\nTOTAL REQUEST FOR " + fileName + " TOOK: " + duration + " MILLISECONDS");
    }

    // Returns a list of lists: Which peers have which pieces of the file. Queries peers who have the file.
    private static ArrayList<ArrayList<String>> getFilePieces(String fileName, int numFilePieces, ArrayList<String> peersWithFile){
        try {
            ArrayList<ArrayList<String>> pieceBreakdown = new ArrayList<ArrayList<String>>();
            // Init placeholder for outside arraylist
            for (int i = 0; i < numFilePieces; i++) {
                ArrayList<String> temp = new ArrayList<String>();
                pieceBreakdown.add(temp);
            }

            // For every peer who the tracker says the file has, request information on how many pieces they have
            for (String peerInfo : peersWithFile) {
                ArrayList<Integer> peerHasMe = new ArrayList<Integer>();

                // String in format:
                // peerName:portNumber;host
                // Divide it up ->
                int colonIndex = peerInfo.indexOf(":");
                int semiColonIndex = peerInfo.indexOf(";");
                if (colonIndex == -1) { return null; } //error

                String peerName = peerInfo.substring(0, colonIndex);
                String portNo = peerInfo.substring(colonIndex+1, semiColonIndex);
                String host = peerInfo.substring(semiColonIndex+1, peerInfo.length());
                connectToPeer(peerName, Integer.parseInt(portNo), host); //establishes connections

                if (peerName.equals(myName)) { continue; } // don't want to ask myself for file pieces!

                int askCounter = 0;             // How many times have I asked for the file pieces?
                boolean didGetPieces = false;   // Did I get the pieces from the peer?

                // Sometimes there is a small error, so we retry requesting twice
                while (askCounter < 2) {
                    try {
                        peerHasMe.addAll(peerStubs.get(peerName).requestPieceInfo(fileName));
                        askCounter = 2;
                        didGetPieces = true;
                    } catch (Exception e) {
                        // System.out.println("catching exception in piece info");
                        askCounter++;
                    }
                }

                if (!didGetPieces) {
                    // System.out.println("One peer is unresponsive, moving to next");
                    if (peerStubs.containsKey(peerName)) { peerStubs.remove(peerName); }
                    continue;
                }

                for (Integer piece : peerHasMe) {
                    if (!pieceBreakdown.get((int)piece).contains(peerName))
                        pieceBreakdown.get((int)piece).add(peerName);
                }
            }

            return pieceBreakdown;

        } catch (Exception e) {
            // System.out.println("Exception in 'getFilePieces'");
            e.printStackTrace();
        }
        return null;
    }

    // Sorts the array of piece numbers by the length of the 'piece list'
    private static int[] sortArrayOfIndices(int numPieces, ArrayList<ArrayList<String>> pieceBreakdown) {
        int[] numsInList = new int[numPieces];
        int[] indexArray = new int[numPieces];

        for (int i = 0; i < numPieces; i++) {
            numsInList[i] = pieceBreakdown.get(i).size();
            indexArray[i] = i;
        }

        // This is a VERY DUMB BUBBLE SORT way to sort... sorry!
        for (int i = 0; i < numPieces; i++) {
            for (int j = i+1; j < numPieces; j++) {
                if (numsInList[i] > numsInList[j]) {
                    int temp = numsInList[i];
                    numsInList[i] = numsInList[j];
                    numsInList[j] = temp;

                    temp = indexArray[i];
                    indexArray[i] = indexArray[j];
                    indexArray[j] = temp;
                }
            }
        }
        return indexArray;
    }


    // Downloads file in pieces, dividing download amongst peers
    private static void downloadFile(String fileName, ArrayList<ArrayList<String>> pieceBreakdown) {
        try {
            // Opens up a Random Access File to output to
            RandomAccessFile outFile = new RandomAccessFile(fileName, "rw");

            int numPieces = myFiles.get(fileName).getNumPieces();   // Number of pieces we have broked the file into
            int[] indexArray = new int[numPieces];                  // Will hold an array of piece numbers, sorted by
                                                                    //      the number of peers who have that piece
                                                                    //      number. Smallest to largest. (Rarest first)
            // Copies these sorted indices into the indexArray
            System.arraycopy(sortArrayOfIndices(numPieces, pieceBreakdown), 0, indexArray, 0, numPieces);

            int counter = -1;   // Keeps track of the current piece we are talking about (will loop within piece range)
            int reAskTrackerForPiece = -1;  // Holds which piece last had no peers in its list
            int continueCounter = 0;    // Keeps track of the number of times we are unable to place a request

            // Loop through all pieces in order until we have downloaded everything
            while (!myFiles.get(fileName).getPiecesNeeded().isEmpty() ||
                   !myFiles.get(fileName).getDownloadingPieces().isEmpty()) {

                counter++;
                if (counter >= numPieces) { counter = 0; } // this ensures we continuously loop through the indices of the index array
                int currentPiece = indexArray[counter];     // local variable for readability 

                if (!myFiles.get(fileName).needsPiece(currentPiece)) {
                    continue; // I am already processing this piece, don't try to download again
                }

                if (pieceBreakdown.get(currentPiece).isEmpty() || continueCounter>=10) {
                    if (continueCounter >= 10) { continueCounter = 0; } // reset continueCounter
                    else { reAskTrackerForPiece = currentPiece; }       // otherwise we have gotten here because there was empty list

                    if (reAskTrackerForPiece == currentPiece){
                        System.out.println("Nobody has piece " + currentPiece + " so I am not downloading file!");
                        return;
                    }

                    // re-ask tracker for list of peers
                    ArrayList<String> peersWithFile = leadTrackerStub.query(fileName, myName, myPortNum, myHost);
                    if (peersWithFile != null) { peersWithFile.remove(0); }     // first index is file size
                    else { return; }    // error getting list from Tracker -> stop downloading

                    int askCounter = 0; // How many times have we asked for file pieces?
                    boolean didGetPieceBreakdown = false; // Have we actually gotten the piece breakdown?

                    // Sometimes there is a small error, so we retry requesting file pieces up to 3 times
                    while (askCounter < 3) {
                        try {
                            ArrayList<ArrayList<String>> tempBreakdown = new ArrayList<ArrayList<String>>();
                            tempBreakdown.addAll(getFilePieces(fileName, numPieces, peersWithFile));

                            pieceBreakdown.clear();
                            pieceBreakdown.addAll(tempBreakdown);

                            // resort "rarest first" array
                            System.arraycopy(sortArrayOfIndices(numPieces, pieceBreakdown), 0, indexArray, 0, numPieces);
                            askCounter = 3; // end loop
                            didGetPieceBreakdown = true;
                        } catch (Exception e) {
                            // System.out.println("catching exception");
                            askCounter++;
                        }
                    }

                    if (!didGetPieceBreakdown) {
                        // System.out.println("Peers are unresponsive");
                        continue;
                    }
                }

                // Loops throgh peers who have this piece of the file: will start a new thread if we want to request
                for (int indexOfPeerName = 0; indexOfPeerName < pieceBreakdown.get(currentPiece).size(); indexOfPeerName++) {
                    String peerName = pieceBreakdown.get(currentPiece).get(indexOfPeerName);

                    if (myFiles.get(fileName).getCurrentlyDownloadingFrom().contains(peerName)) {
                        continueCounter++;
                        continue;   // don't want to download from someone we are already downloading from
                    }

                    //otherwise download from k
                    myFiles.get(fileName).startDownloadingPiece(currentPiece, peerName);
                    int sizeOfThisPiece = getThisPieceSize(myFiles.get(fileName), currentPiece);
                    // System.out.println("size of this piece = " + sizeOfThisPiece);

                    // Used to test only -> stops downloads after halfway completed
                    if (rareTest && myFiles.get(fileName).getNumComplete() > numPieces/2) {
                        break;
                    }

                    ConcurrentPieceRequest newPieceRequest = new ConcurrentPieceRequest(fileName, peerName, currentPiece, sizeOfThisPiece, outFile);
                    newPieceRequest.start();
                    break; // Don't ask more than one peer for same piece of the file
                }
                if (rareTest && myFiles.get(fileName).getNumComplete() >= numPieces/2) {
                    break;
                }
            }

            

        } catch (Exception e) {
            // System.out.println("Exception in 'downloadFile'");
            e.printStackTrace();
        }
    }

    private static void writeBytes(byte[] data, RandomAccessFile fileName, int piece, String peerName, String fileString) {
        try {
            // System.out.println("Wryting bytes");
            String s = new String(data);
            // System.out.println(s);
            // first get offset with piece:
            int offset = piece * PIECE_SIZE;
            fileName.seek(offset);
            fileName.write(data);
            int numDone = myFiles.get(fileString).getNumComplete();
            int numPieces = myFiles.get(fileString).getNumPieces();
            double percent = (double)numDone / (double)numPieces;
            percent = percent * 100.;

            System.out.print("Downloaded piece " + piece + " from " + peerName + ": ");
            System.out.printf("%%%.2f\n", percent);

        } catch (Exception e) {
            System.out.println("Exception in writing file");
            e.printStackTrace();
        }
    }

    /*
     * Asks a peer for a specific piece of the file. We have already determined
     * that this peer has the file, but we will use a timeout and check on the 
     * peer end to make sure this information is correct.
     */
    private static byte[] askForFilePiece(String peerName, String fileName, int piece) {
        // print("I, " + myName + " am requesting " + fileName + " frm " + peerName + "\n");
        try {
            byte[] answer = peerStubs.get(peerName).requestFile(fileName, piece);
            return answer;
        } catch (Exception e) {
            // System.out.println("Exception");
            e.printStackTrace();
        }
        return null;
    }

    /* Infinite loop that will query the user to input commands. To
    exit, type 'exit'. */
    private static void parseInput() {
        Scanner keyboard = new Scanner(System.in);
        String fileName = "YOLO.txt";

        // while (true && keyboard.hasNextLine()) {
        while (true) {
            System.out.println("Enter a command");
            // if (keyboard.nextLine() == null) { 
            //     print("why");
            //     break; }
            String command = keyboard.nextLine();
            String parsedRequest[] = command.split(" ");
            String secondPartOfRequest = "";

            // Seed a file
            if (parsedRequest[0].equals("seed")) {
                String name = parsedRequest[1];
                seedFile(name);
            }

            // Initiates a peer with only every other piece of a file
            else if (parsedRequest[0].equals("rare")) {
                rareTest = true;
                fileName = parsedRequest[1];
                makeRequest(fileName);

                print("------- Rare Piece State -------");
                myFiles.get(fileName).printLists();
                print("------- Rare Piece State -------");
            }

            // Request a file frm peer directly
            else if (parsedRequest[0].equals("request")) {
                fileName = parsedRequest[1];
                makeRequest(fileName);
            }

            else if (parsedRequest[0].equals("exit")) {
                System.out.println("Goodbye!");
                break;
            }
        }
    }

    /* to run: 
    java Peer localhost <your port> <your name> 
    to connect to more peers: first start them on their ports, then enter command:
    connect <their name> <their port>
    to request a file directly, enter command:
    request <their name>
    to seed a file, enter command: 
    seed <file name>
    */
	public static void main(String[] argv) {
        myHost = (argv.length < 1) ? "localhost" : argv[0];
        myPortNum = (argv.length < 2) ? 5000 : Integer.parseInt(argv[1]);
        myName = (argv.length < 3) ? "Howard" : (argv[2]);

        // try {
        //     myHost = InetAddress.getLocalHost().getHostAddress();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        
        System.out.println("My portnum: " + myPortNum + "\nMy name: " + myName + "\nMy host: " + myHost + "\n");

        createAndBindSelf();
        parseInput();
    }


    /* Class used to run multiple concurrent threads, when calculating
     * the time it takes for each request. */
    private static class ConcurrentPieceRequest extends Thread {
        private Thread t;
        private String requestType;
        private String fileName;
        private String peerName;
        private int currentPiece;
        private int sizeOfThisPiece;
        private RandomAccessFile outFile;
       
        ConcurrentPieceRequest(String fileName, String peerName, int currentPiece, int sizeOfThisPiece, RandomAccessFile outFile ) {
            this.sizeOfThisPiece = sizeOfThisPiece;
            this.peerName = peerName;
            this.fileName = fileName;
            this.currentPiece = currentPiece;
            this.outFile = outFile;
        }

        public void run() {

            try {
                byte[] answer = new byte[sizeOfThisPiece];
                System.arraycopy(askForFilePiece(peerName, fileName, currentPiece), 0, answer, 0, sizeOfThisPiece);
                // System.out.println("Anaswer = " + answer);

                if (answer == null) {
                    myFiles.get(fileName).noLongerDownloadingPiece(currentPiece, peerName);
                } else {
                    myFiles.get(fileName).finishedDownloadingPiece(currentPiece, peerName);
                    writeBytes(answer, outFile, currentPiece, peerName, fileName);
                }

            } catch (Exception e) {
                System.out.println("Thread interrupted.");
            }
        }
       
        public void start()
        {
            if (t == null) {
                t = new Thread (this, peerName);
                t.start(); 
            }
        }
    }




}






