package servent;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class FileUploader extends Thread {

	Socket uploaderSocket;
	DataOutputStream dos;
	DataInputStream dis;
	FileInputStream fis;
	BufferedInputStream bis;
	String fileToSend;

	public FileUploader(String fileName, String requestorIP, int requestorPort) {
		this.fileToSend = fileName;
		System.out.println("File Uploader Activated");
		try {
			uploaderSocket = new Socket(requestorIP, 15000);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			dos = new DataOutputStream(uploaderSocket.getOutputStream());
			String filePath = Gnutella.sharedDirectoryPath + "/" + fileToSend;
			File file = new File(filePath);

			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			byte[] fileToByte = new byte[(int) file.length()];
			bis.read(fileToByte, 0, fileToByte.length);
			dos.write(fileToByte, 0, fileToByte.length);

			// clear streams
			dos.flush();
			dos.close();
			bis.close();
			fis.close();
			System.out.println("Upload Complete");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
