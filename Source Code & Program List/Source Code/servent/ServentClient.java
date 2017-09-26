package servent;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
/*
public class ServentClient extends Thread:
 * this class is responsible for interacting with all of neighbors of this servent.
 * 1. Make a new query to search the file in the netowrk
 * 2. Request to obtain the identified file and activate file downloader
 */
public class ServentClient extends Thread {
	
	private static Scanner sc;
	public static long start;
	public static long end;
	
	public ServentClient() {
		sc = new Scanner(System.in);
	}
	
	public void run() {
		while (true) {
			
			System.out.println("Select Menu");
			System.out.println("1. Search a file");
			System.out.println("2. Obtain a file");
			System.out.println("q. exit");
			String option = sc.nextLine();
			
			// Search a file
			if(option.equals("1")) {
				
				System.out.println("Please write down a file name to search");
				String fileName = sc.nextLine();
				
				// have the values ready for query
				MessageID messageID= new MessageID();
				messageID.setSequenceNumber(Gnutella.globalSequenceNumber);
				messageID.setIpAddress(Gnutella.myServerIP);
				messageID.setPortNumber(Gnutella.myServerPort);
				
				// invoke query to neighbors
				Query(messageID, Gnutella.TTL, fileName);
				
			// Sbtain the identified file	
			} else if(option.equals("2")) {
				System.out.println("Please write down a file name you want to download");
				String fileName = sc.nextLine();
				obtain(fileName);
				
			// Exit
			} else if(option.equals("q")) {
				System.out.println("Thank you");
				System.exit(0);
			}

			// Check any update in the shared file directory
			if (Gnutella.checkUpdated()) {
				Gnutella.listup();
			}
		}
	}

	/*
	 * public void Query(MessageID messageID, int TTL, String fileName):
	 * creates a new query to search a file in this network
	 */
	public void Query(MessageID messageID, int TTL, String fileName) {
		if(Arrays.asList(Gnutella.stringFileList).contains(fileName)) {
			System.out.println("File exists in local shared directory");
		} else {
			String initialQuery = Integer.toString(messageID.getSequenceNumber()) + "/" 	
						+ messageID.getIpAddress() + ":" 
						+ messageID.getPortNumber() + "/" 
						+ Gnutella.myServerIP  + ":" 
						+ Gnutella.myServerPort + "/" 
						+ "neighbor" + "/" 
						+ fileName + "/"
						+ Integer.toString(TTL);
			System.out.println("initialQuery: " + initialQuery);
			Gnutella.requests.add(initialQuery);

			//start = System.currentTimeMillis();
			
			new ServentServer().broadcast();
			//end = System.currentTimeMillis();
			//long timetaken = end - start;
			//System.out.println("time taken: " + timetaken);
			// increase global sequence number
			Gnutella.globalSequenceNumber++;	
		}
	}
	
	/*
	 * obtain(String fileName):
	 * 1. Gets file name as a parameter to download.
	 * 2. Select a peer from the peer list to connect and download.
	 * 3. Starts a downloader thread.
	 */
	public static void obtain(String fileName) {
		DataOutputStream dos;
		sc = new Scanner(System.in);
		Socket obtainSocket;
		try {
			int select;
			ArrayList<String> fileList = new ArrayList<String>();
			
			// get the list of servents that hold the file
			fileList = Gnutella.downloadableList.get(fileName);
			System.out.println("Select the peer to connect for obtain");
			System.out.println("(Please enter 0 if you want to select first, enter 1 if you want to select second)");
			System.out.println("Peer List: " + fileList);
			
			// get selection of servents from user			
			while (true) {
				select = sc.nextInt();
				if(select < 0 || (select > fileList.size())) {
					System.out.println("Invalid input");
				} else {
					break;
				}
			}
			
			// set the target servent information to download
			String destination[] = fileList.get(select).split(":");
			String destIP = destination[0];
			int destPort = Integer.parseInt(destination[1]); 
			
			// connect to the target servent and information of file transfer
			obtainSocket = new Socket(destIP, destPort); 
			dos = new DataOutputStream(obtainSocket.getOutputStream());
			dos.writeUTF("obtain," + fileName + "," + Gnutella.myServerIP + "," +Gnutella.myServerPort);
			
			// create a new thread to download
			Thread fileDownloader = new Thread(new FileDownloader(fileName));
			fileDownloader.start();
			fileList.clear();
		} catch (Exception e) {
			
		} finally {
		}
	}
}
