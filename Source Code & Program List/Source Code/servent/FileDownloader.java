package servent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileDownloader extends Thread {

	ServerSocket serverSocket = null;
	Socket downloaderSocket = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	FileOutputStream fos = null;
	String fileToDownload = null;

	public FileDownloader(String fileName) {
		System.out.println("File downloader activated");
		fileToDownload = fileName;
		try {
			serverSocket = new ServerSocket(15000);
			while (true) {
				downloaderSocket = serverSocket.accept();
				System.out.println("accepted");
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			// create a new file object to be written
			File file = new File(Gnutella.sharedDirectoryPath + "/" + fileToDownload);

			fos = new FileOutputStream(file);
			dis = new DataInputStream(downloaderSocket.getInputStream());

			int length;
			int maximumSize = 35536; // maximum size
			byte[] data = new byte[maximumSize];
			System.out.println("display file" + fileToDownload);
			while ((length = dis.read(data)) != -1) {
				fos.write(data, 0, length);
			}
			fos.close();
			dis.close();
			System.out.println("Download Complete");
			serverSocket.close();
			Thread.currentThread().interrupt();
		} catch (IOException e) {
		} catch (Exception e) {
		} finally {
			
		}
	}
}
