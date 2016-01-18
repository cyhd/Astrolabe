package etma.navigator.control.network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import etma.navigator.control.Navigator;


public class PilotageServerSocket extends Thread {

	protected int port ;
	protected String ipAddress ;
	protected Pilotage pilotage ;
	protected final int xMax = 1920 ;
	protected final int yMax = 1200 ;
	protected final int xMin = 0 ;
	protected final int yMin = 0 ;
	
	public PilotageServerSocket (Navigator navigator) {
      this (navigator, 1234, "10.29.227.224") ;
      //this (navigator, 1234, "10.29.230.241") ;
		
	}

	public PilotageServerSocket (Navigator navigator, int port, String ipAddress) {
		pilotage = new Pilotage (navigator) ;
		this.port = port ;
		this.ipAddress = ipAddress ;
	}
	
	@SuppressWarnings ("resource")
   @Override
	public void run () {
		try {
			ServerSocket serverSocket = new ServerSocket (port, 0, InetAddress.getByName (ipAddress)) ;
			while (true) {
				Socket socket = serverSocket.accept () ;
				Reception service = new Reception (socket) ;
				service.start () ;
			}
		} catch (Exception e) {
			e.printStackTrace () ;
		}
	}
	
	class Reception extends Thread {
		
		protected Socket socket ;
		
		public Reception (Socket s) {
			socket = s ;
			System.out.println ("Connection") ;
		}
		
		public void run () {
			try {
				ObjectInputStream ois = new ObjectInputStream (socket.getInputStream()) ;
				boolean theEnd = false ;
				while (! theEnd) {
					String name = (String)ois.readObject () ;
					String command = (String)ois.readObject () ;
					//System.out.println ("received: " + command + " for object " + name) ;
					if (name.equals("server") && command.equals("disconnect")) {
						theEnd = true ;
					} else {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> hm = (HashMap<String, Object>)ois.readObject () ;
						pilotage.navigate (command, name, hm) ;
					}
				}
				socket.close () ;
			} catch (IOException e) {
				System.out.println ("Disconnection") ;
			} catch (ClassNotFoundException e) {
			} catch (Exception e) {
			}
		}
	}
}

