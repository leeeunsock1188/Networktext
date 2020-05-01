package chat8;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MulitiClient {

	public static void main(String[] args) {
		String s_name = null;
	
		// Sender가 기능을 가져가므로 여기서는 필요없음
		// PrintWriter out = null;
		// 서버가 에코해주는 메세지를 읽어오는 기능이 Receiver로 옮겨짐.
		// BufferedReader in = null;

		try {
			/*
			 * c:/> java 패키지명.MultiClient 접속할IP주소 => 위와같이 하면 해당 IP주소로 접속할 수 있다. 만약 IP주소가 없다면
			 * localhost(127.0.0.1)로 접속된다.
			 */
			String ServerIP = "localhost";
			if (args.length > 0) {
				ServerIP = args[0];
			}
			Socket socket = new Socket(ServerIP, 9999);
			System.out.println("서버와 연결되었습니다...");

			while (true) {
				try {
					System.out.print("이름을 입력하세요:");
					Scanner scanner = new Scanner(System.in);
					s_name = scanner.next();
					if (s_name.isEmpty()) { //배열이 비어있는지 확인하는 메세지
						continue;
					}
					break;
				} catch (Exception e) {

				}
			} //while ..emd 
			
			// 서버에서 보내는 Echo메세지를 클라이언트에 출력하기 위한 쓰레드 생성
			Thread receiver = new Receiver(socket);
			// setDaemon(true)가 없으므로 독립쓰레드로 생성됨.
			receiver.start();

			// 클라이언트의 메세지를 서버로 전송해주는 쓰레드 생성
			Thread sender = new Sender(socket, s_name);
			sender.start();
		} catch (Exception e) {
			
			System.out.println("예외발생[MultiClient]" + e);
		}
	}
}