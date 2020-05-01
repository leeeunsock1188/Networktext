package chat7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MultiServer extends ConnectDB {

	static ServerSocket serverSocket = null;
	static Socket socket = null;
	// 클라이언트 정보 저장을 위한 Map컬렉션 정의
	// 키(key)와 값(value)의 쌍으로 이루어진 데이터의 집합
	// 순서는 유지되지 않고, 키는 중복을 허용하지 않으며 값의 중복을 허용
	Map<String, PrintWriter> clientMap;

	// 생성자
	public MultiServer() {
		super();

		// 클라이언트의 이름과 출력스트림을 저장할 HashMap생성
		clientMap = new HashMap<String, PrintWriter>(); // HashMap (중복과 순서가 허용되지 않으며 null값이 올 수 있다.)

		// HashMap동기화 설정. 쓰레드가 사용자정보에 동시에 접근하는것을 차단한다.
		Collections.synchronizedMap(clientMap);
	}

	// 서버 초기화
	public void init() { //////////////////// init 서버 초기화 메소드 ////////////////////////

		try { ///// 소켓은 무조건 트라이문에서 사용 9999번 포트를 열어놓고 기다리고있다가
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다..");

			while (true) {// 도스 창으로 client를 몇개를 여는지
				// 클라이언트에 내용을 받아들인다.
				socket = serverSocket.accept();
//            System.out.println(socket.getInetAddress()+":"+socket.getPort());
				/*
				 * 클라이언트의 메세지를 모든 클라이언트에게 전달하기 위한 쓰레드 생성 및 start.
				 */
				// 내부클래스 안에 멤메변수 Socket으로 넘어감
				Thread mst = new MultiServerT(socket);
				mst.start(); // Thread 시작
			} // while end...
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				serverSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 메인메소드 : Server객체를 생성한후 초기화한다.
	public static void main(String[] args) {
		MultiServer ms = new MultiServer();
		ms.init();
	}

	// 접속된 모든 클라이언트에게 메세지를 전달하는 역활의 메소드
	public void sendAllMsg(String name, String msg) { // 처음들어오는건 " " , 이름+님이 입장하셧습니다.

		// Map에 저장된 객체의 키값(이름)을 먼저 얻어온다.
		// 보낸메세지가 list라면 if문으로 들어감
//	   if(msg.equals("/lest")) {
//		   //it라는 변수에다가 clientMap안에 있는 keyset값은 이름 만큼돌린다.
//		   Iterator<String> it = clientMap.keySet().iterator();
//		   //Map에 저장된 이름수만큼 반복한다.
//		   while(it.hasNext()) {
//			   //clientMap안에있는 이름을 다가져옴 
//		PrintWriter it_out = (PrintWriter)clientMap.get(it.next());
//			it_out.println("접속자수"+it.next()); //클라이언트에ㅔ 이름수많큼 접속자수를 보냄 
//		
//		   }//while ..end
//	   }//if.. end  (/list) 를 입력했을떄 작동

		Iterator<String> it = clientMap.keySet().iterator();
		// 저장된 객체(클라이언트)의 갯수만큼 반복한다.

		while (it.hasNext()) { // name == "이은석" /// name2 = 클라이언트 3개 ex ) 이은석 / 김선진 /윤정현
			// 내가 입력한 이름이 저장됨
			String name2 = it.next(); // 1. 이은석
			try {
				// 각 클라이언트의 PrintWriter객체를 얻어온다.
				PrintWriter it_out = (PrintWriter) clientMap.get(name2);
				// 각각에 클라이언트 (it_out)라는 변수 만듬 PrintWriter로 형변환함.
				// clientMap.get(name2=map에 들어있는 key전부 불러옴)

				// 클라이언트에게 메세지를 전달한다.
				/*
				 * 매개변수 name이 있는 경우에는 이름+메세지 없는경우에는 메세지만 클라이언트로 전달한다.
				 */

				if (name.equals("")) { // name /이은석 == ""(아무입력값이없을떈)메세지만 띄운다
					it_out.println(URLEncoder.encode(msg, "UTF-8"));
				}

				else if (name2.equals(name)) { // name2(hashMap에 담겨있는 이름 총) == 이은석 같으면 아무메세지도 안띄워준다.
//            	it_out.println(msg);
//            		if(msg.contains("/list")) {
//              		Iterator<String> iterable = clientMap.keySet().iterator();
//              		while(iterable.hasNext()) {
//              			it_out.println("접속자명: ["+iterable.next()+"");
//              		}
//             	 }

				} else {
					// db에서 블랙리스트를 조회(db에서 내이름넣고 black리스트 멤버 이름을 가져온다)
//            	List<String> myBlackMembers = conn.daskmfsadl(myName)
//            	for(myBlackmembers) {
//            		if(!name2.equals(myBlackmembers)) {
//            			it_out.println("["+name+"]:"+msg);
//            		}
//            	}

//					if (msg.contains("/to ")) {// /to 이은석 ㅇㅇㅇㅇㅇ /to 이은석
//
////            		System.out.println("subString 하기위한  msg : " + msg);
//						int startIdx = msg.indexOf(" ") + 1; // to << to(3)
//						int endIdx = msg.indexOf(" ", startIdx); // (3)index이후 "" 을 찾음 /to 이은석 은석아 (3)+1
//																	// 4.index~7.index잘른다.
//						String toName = msg.substring(startIdx, endIdx); // 4~7 (이름)
//
//						if (name2.equals(toName)) { // 클라이언트에 입력한 이름들을 전부 돌려서 같은 이름이있으면
//
//							String toMsg = msg.substring(endIdx + 1, msg.length()); // 7.index+1 (8)부터 메세지의 길이만큼 잘라낸다
//
//							// 메세지를 이제 여기로 바로 넘어오게
//
//							it_out.println("[" + name + "]:" + toMsg); // 그 해당 클라이언트에게만
//
//						}
//
//					}
//
//					else {
					it_out.println("[" + name + "]:" + msg);
//					}

				}

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();

			} catch (Exception e) {
				System.out.println("예외:" + e);

			}

		}
	}

	// 내부클래스
	class MultiServerT extends Thread {

		// 멤버변수
		Socket socket;
		PrintWriter out = null;
		BufferedReader in = null;

		// 생성자 : Socket을 기반으로 입출력 스트림을 생성한다.
		public MultiServerT(Socket socket) {
			this.socket = socket; // scoket = serverSocket.accept();
			try {
				// 현재 나의 소켓 serverIP = "localhost" , port: 9999라는 socket.getOutputStream연다.
				out = new PrintWriter(this.socket.getOutputStream(), true);
				// 받아온다
				in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));

			} catch (Exception e) {
				System.out.println("예외:" + e);
			}
		}// MultiServerT 생성자 end...

		@Override
		public void run() {
			// 클라이언트로부터 전송된 "대화명"을 저장할 변수
			String name = "";
			// 메세지 저장용 변수
			String s = "";

			try {
				// 클라이언트의 이름을 읽어와서 저장
				// in = new BufferedReader(new InputStreamReader
				// (this.socket.getInputStream(),"UTF-8"));
				name = in.readLine();// 클라이언트에서 처음으로 보내는 메세지는
				name = URLDecoder.decode(name, "UTF-8");// 클라이언트가 사용할 이름이다.
				// 접속한 클라이언트에게 새로운 사용자의 입장을 알림.
				// 접속자를 제외한 나머지 클라이언트만 입장메세지를 받는다.
				// 처음에 입력한 값만 보내는거
				sendAllMsg("", name + "님이 입장하셨습니다."); // 첫번째 if문 에 전달

				// 현재 접속한 클라이언트를 HashMap에 저장한다.ㄴ
				clientMap.put(name, out);// key는 name // value는 Client에서 읽어오는 msg가된다.

				// HashMap에 저장된 객체의 수로 접속자수를 파악할수 있다.
				System.out.println(name + " 접속");
				System.out.println("현재 접속자 수는" + clientMap.size() + "명 입니다.");

				// 입력한 메세지는 모든 클라이언트에게 Echo된다.

				while (in != null) { // 이름의 수만큼 돌림

					s = in.readLine(); // 내용
					s = URLDecoder.decode(s, "UTF-8");

					if (s == null)
						break; // 공백없이 엔터를 치면 break처리됨

					// 일반대화
					// **db처리는 여기서 진행
					String query = "INSERT INTO chatting_td VALUES (seq_chatting.nextval,?,?,sysdate)";

					psmt = con.prepareStatement(query);

					psmt.setString(1, name);
					psmt.setString(2, s);
					psmt.executeUpdate();
					///////////////////////// 쿼리작성문

					// 읽어온 메세지를 콘솔에 출력하고...
					System.out.println(name + " >> " + s);
					// 클라이언트에게 Echo해준다.
					// StringTokenizer " " 스페이스바 를 기준으로 문자열을 자른다.
					StringTokenizer st = new StringTokenizer(s, " "); // /list 이은석 (5index)짜름?

					String[] st_array = new String[st.countTokens()];// contTokens(): 문자열을 잘라낸 결과로 가지게 될 토큰의 객수를 리턴한다.
																		// 몇개로 짤리는지 보는거
					int i = 0;
					while (st.hasMoreElements()) { // 현재 노트나이저 안에 받아낼 토근이 더 있는지 체크한다.
						st_array[i++] = st.nextToken(); // 토크나이저는 넥스트 토큰을 통해 순차적으로 토큰을 받아낸다.
					} // end .. while
					Iterator<String> iterator = clientMap.keySet().iterator();
					// s.charAt == > 내가 입력한 첫번쨰인덱스가 / 면 switch문으로 들어간다.
					if (s.charAt(0) == '/') {
						// list를 짤라냄
						switch (st_array[0].substring(1)) { // st_array[0] 0번째 인덱스에 ex) 1234 있으면 2부터 짤라낸다는 의미 그뒤에 숫자하나더
															// 넣으면 2~몇까지 지정가능함.
						case "list":
							while (iterator.hasNext()) {// 참고 할값이 있으면 실행 클라이언트 수만큼 반복한다.
								out.println("접속자명:" + iterator.next());// 클라이언트들에게 보내준다.

							}

							break;
						case "to":
							Iterator<String> it = clientMap.keySet().iterator();
							String u = it.next(); //총 사용자
							int startIdx = s.indexOf(" ") + 1;
							int endIdx = s.indexOf(" ", startIdx);
							
							String toName = s.substring(startIdx, endIdx);//보낼자 이름 
							String toMsg = s.substring(endIdx + 1, s.length()); //보내는사람 메세지
							
							if (i >= 3) {


								while(it.hasNext()) {

									if (u.equals(toName)) {
										PrintWriter to_out = (PrintWriter) clientMap.get(toName);
										to_out.println("[" + name + "]:" + s);
										break;
									}
								}
							}
							else if(i < 2) {
								PrintWriter to_out = (PrintWriter) clientMap.get(toName);
								to_out.println("[" + name + "ㄴㄴ]:" + s);
								
							}
						default:
							break;
						}// switch .. end
					} // if .. end
					else {// 일반대화
						sendAllMsg(name, s);
					}
				} // while ..end
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				/*
				 * 클라이언트가 접속을 종료하면 예외가 발생하게 되어 finally로 넘어오게된다. 이때 "대화명"을 통해 remove()시켜준다.
				 */
				clientMap.remove(name);
				sendAllMsg("", name + "님이 퇴장하셨습니다.");
				// 퇴장하는 클라이언트의 쓰레드명을 보여준다.
				System.out.println(name + " [" + Thread.currentThread().getName() + "] 퇴장");
				System.out.println("현재 접속자 수는" + clientMap.size() + "명 입니다.");

				try {
					socket.close();
					in.close();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}// MultiServer end...

}