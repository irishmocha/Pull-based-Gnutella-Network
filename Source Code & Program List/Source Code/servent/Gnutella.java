package servent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Gnutella {
	
	public static int globalSequenceNumber = 0;
	
	// Queries to be broadcasted are stored in this queue
	public static ConcurrentLinkedQueue<String> requests = new ConcurrentLinkedQueue<String>();
	
	// Maximum of Time to Live
	public static int TTL = 10;
	
	// HashMap to store the identified(searched) file name and the servent that hold the file
	public static HashMap<String, ArrayList<String>> downloadableList = new HashMap<String, ArrayList<String>>();
	
	// neighbor of this servent list
	public static ArrayList<ServerInfo> neighborServerList = new ArrayList<ServerInfo>();
	
	// information of this server, used to access network topology
	public static int myServerID;
	public static int myServerIndex;
	public static String myServerPort;
	public static String myServerIP;
	
	// myNeighbors
	public static int[] myNeighbors;
	
	// this array list stores the list of servers and the information of network
	public static ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
	
	// lists for maintaining valid files to be shared
	private static File[] fileList;
	protected static String[] stringFileList;
			
	// a path of shared directory and an actual directory
	protected static String sharedDirectoryPath = "/Users/Sevas/incoming";
	public static File sharedDirectory;
	
	private static Scanner sc;
	
	// the location of configuration file
	private static String configFilePath = "configuration.xml";
		
	/*
	 * checkUpdated():
	 * If a user adds or removes some files which are already registered to the indexing server,
	 * this method detect the change and register new list to the indexing server. 
	 */
	public static boolean checkUpdated() {
		File newSharedDirectory = new File(sharedDirectoryPath);
		
		File[] tempFileList = newSharedDirectory.listFiles();
		String[] tempStringFileList = newSharedDirectory.list();
				
		if(stringFileList.length == tempStringFileList.length && fileList.length == tempFileList.length) {
			return false;
		} else {
			fileList = newSharedDirectory.listFiles();
			stringFileList = newSharedDirectory.list();	
			return true;
		}
	}
	
	/*
	 * listup():
	 * enlists the files to 'fileList' and 'stringFileList' to maintain the list of files.
	 * The result (list of files) is used to maintain shareable files of this peer and register to the
	 * indexing server as well.
	 */
	public static boolean listup() {
		sharedDirectory = new File(sharedDirectoryPath);
		
		// check whether the path is valid shared directory or not
		if(!sharedDirectory.isDirectory()) {
			System.out.println("Warning: " + sharedDirectoryPath + " is not a directory");
			return false;
		}
		// store list of files to the local
		fileList = sharedDirectory.listFiles();
		stringFileList = sharedDirectory.list();	
		
		System.out.println("Shared Directory is Ready");
		return true;
	}
	
	public static void main(String[] args) {
		
		new XMLParser(configFilePath);
		
		
		System.out.println("Your server ID is ? (1-10)");
		sc = new Scanner(System.in);
		
		while(true) {
			myServerID = sc.nextInt();
			
			if(myServerID < 1 || myServerID > 10) {
				System.out.println("Invalid Server ID");	
				continue;
			} else { 
				myServerIndex = myServerID-1;
				myServerPort = servers.get(myServerIndex).getServerPort();
				myServerIP = servers.get(myServerIndex).getServerIP();
				break;	
			}
		}
		
		sharedDirectoryPath = sharedDirectoryPath + myServerID;
		System.out.println(sharedDirectoryPath);
		
		listup();
		
		
		myNeighbors = new int[10];
		
		for(int i=0; i<10; i++) {
			System.out.println("Enter your neighbors. 99 to break");
			int temp = sc.nextInt();
			if(temp == 99) {
				break;
			}
			myNeighbors[i] = temp;
		}
		//myNeighbors = getMyNeighbors();
		for(int j=0; j<10; j++) {
			if (myNeighbors[j] != 0) {
				System.out.println(j+1 + "st neighbor: " + myNeighbors[j]);
				
				int neighborIndex = myNeighbors[j] - 1;
				String neighborIP = servers.get(neighborIndex).getServerIP();
				String neighborPort = servers.get(neighborIndex).getServerPort();
				
				ServerInfo serverInfo = new ServerInfo();
				serverInfo.setServerIP(neighborIP);
				serverInfo.setServerPort(neighborPort);
				
				neighborServerList.add(serverInfo);
			}
		}
		
		ServentClient serventClient = new ServentClient();
		serventClient.start();
		Thread serventServer = new Thread(new ServentServer());
		serventServer.start();
	}
}
