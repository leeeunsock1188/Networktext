package chat1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiServer {

	public static void main(String[] args) {
		ServerSocket serverSocket= null;//서비스를 열어놓고 기다림
		Socket socket=null; //서버는 소켓을 통해서 접속한다.
		PrintWriter out =null;
		BufferedReader in= null;
		String s= "";

		try {
			//포트번호가9999번으로 포트번호를 설정하여  
			//서버를 생성하고 클라이언트의 접속을 기다린다.
			serverSocket= new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");
			
			////....접속대기중..... 커서가 껌벅거림
			
			//클라이언트가 접속요청을 하면 accept()메소드를 통해 받아들인다.
 			socket = serverSocket.accept(); //소켓 : ip주소와 port번호가 합쳐진 네트워크 상에서 서버 프로그램과 클라이언트 프로그램이 양방향 통신을 할수 있도록 해주는 소프트웨어 장치이다. 
			System.out.println(socket.getInetAddress()+ ":"+
					socket.getPort());
			
			
			/*
			 getInetAddress():소켓이 연결되어있는 원격IP주소를 얻어옴.
			 getPort():소켓이 연결되어있는 원격 포트번호를 얻어옴.
			 */
			
			//서버 -> 클라이언트 측으로 메세지를 전송(출력)하기위한 스트림을 생성
			out = new PrintWriter(socket.getOutputStream(), true);
			//클라이언트로부터 메세지를 받기위한 스트림을 생성 
			in =new BufferedReader(new 
					InputStreamReader(socket.getInputStream()));
			//클라이언트가 보낸 메세지를 라인단위로 읽어옴
			s=in.readLine();
			//읽어온 내용을 콘솔에 즉시 출력
			System.out.println("Client에서 읽어옴:"+s);
			//클라이언트로 응답메세지(Echo)를 보냄 
			out.println("Client에서 읽어옴:"+s);
			//콘솔에 종료메세지를 출력 
			out.println("Bye..!!!");
		} catch (Exception e) {
			e.printStackTrace();
		
		}
		
		finally {
			try {
				//입출력스트림 종료
				in.close();
				out.close();
				//소켓 종료 (자원반납)
				socket.close();
				serverSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			
			}
		}
	}
}