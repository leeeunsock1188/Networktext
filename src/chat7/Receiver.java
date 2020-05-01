package chat7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;

public class Receiver extends Thread{

	Socket socket;
	BufferedReader in = null;
	
	public Receiver(Socket socket) {
		this.socket = socket;
				
		try {
			in = new BufferedReader(new
					InputStreamReader(this.socket.getInputStream(),"UTF-8"));
		} catch (Exception e) {
			System.out.println("예외>Receiver>생성자:"+e);
			
		}
		
	}
	@Override
	public void run() {
		//소켓이 종료되면 while()문을 벗어나서 input스트림을 종료한다.
		while(in != null) {
			try {
				String msg = URLDecoder.decode(in.readLine(),"UTF-8");
				if(!msg.equals("/list") && msg.contains("/list")) { //msg받은메세지가 list가 아니고 list를 포함만 한다. 
					
				}else {
				
					System.out.println(">>"+ msg);
				}
				
				
			} catch (SocketException ne) {
				System.out.println("SocketException 발생됨");
				break;
			}
			catch (Exception e) {
				System.out.println("예외>Receiver>run1:"+e);
			}
		}
			try {
				in.close();
			} catch (Exception e) {
				System.out.println("예외>Receiver>run2:"+e);
			}
	}
	

}
