package chat8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class MultiServer extends IConnectImpl {

	static ServerSocket serverSocket = null;
	static Socket socket = null;
	// 클라이언트 정보 저장을 위한 Map컬렉션 정의
	Map<String, PrintWriter> clientMap;

	// 생성자
	public MultiServer() {
		super("study", "1234");
		delete_chat_TB();
		make_chat_TB();
		delete_Table();
		make_Table();
		// 클라이언트의 이름과 출력스트림을 저장할 HashMap생성
		clientMap = new HashMap<String, PrintWriter>();
		// HashMap동기화 설정. 쓰레드가 사용자정보에 동시에 접근하는것을 차단한다.
		Collections.synchronizedMap(clientMap);
	}

	// 서버의 초기화를 담당할 메소드
	public void init() { ///////////////////////////////////////
		try {
			// 9999포트를 열고 클라이언트의 접속을 대기
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");

			/*
			 * 1명의 클라이언트가 접속할때마다 접속을 허용(accept())해주고 동시에 MultiServerT 쓰레드를 생성한다. 해당 쓰레드는 1명의
			 * 클라이언트가 전송하는 메세지를 읽어서 Echo해주는 역할을 담당한다.
			 */
			while (true) {
				socket = serverSocket.accept();
				/*
				 * 클라이언트의 메세지를 모든 클라이언트에게 전달하기 위한 쓰레드 생성 및 start.
				 */
				Thread mst = new MultiServerT(socket);
				mst.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}/////////////////////////////////////////////init() 메소드 end...

	// 메인메소드 : Server객체를 생성한후 초기화한다.
	public static void main(String[] args) {
		MultiServer ms = new MultiServer();
		ms.init();
		ms.close();
	}

	// 일반채팅시 클라이언트 이름, 메세지를 DB에 저장하는 메소드
	public void input_data(String name, String msg) {
		String query1 = "insert into chatting_tb values "
				+ " (seq_serial_num.nextval ,?, ?, to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'))";
		try {
			psmt = con.prepareStatement(query1);
			psmt.setString(1, name);
			psmt.setString(2, msg);
			psmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 일반채팅 테이블삭제 메소드
	public void delete_chat_TB() {
		try {
			String query = "drop table chatting_tb";

			stmt = con.prepareStatement(query);
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("쿼리오류발생");
		}
		System.out.println("테이블 삭제완료");
	}

	// 일반채팅 테이블생성 메소드
	public void make_chat_TB() {
		try {
			String query = "create table chatting_tb( " + " seq_num number not null primary key, "
					+ " nickname varchar2(100), " + " talk_content varchar2(100), " + " now_time varchar2(100)" + ")";
			stmt = con.prepareStatement(query);
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("asdasfaf");
		}
		System.out.println("테이블 생성완료");
	}

	// 이름중복,블랙리스트여부를 관리하기 위한 테이블삭제 메소드  
	public void delete_Table() {
		try {
			String query = "drop table user_tb";

			stmt = con.prepareStatement(query);
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("쿼리오류발생");
		}
		System.out.println("테이블 삭제완료");
	}

	// 이름중복,블랙리스트여부를 관리하기 위한 테이블생성 메소드  
	public void make_Table() {
		try {
			String query = "create table user_tb( " + " name varchar2(100) primary key, " + " black_stat varchar2(100) "
					+ ")";

			stmt = con.prepareStatement(query);
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("쿼리오류발생");
		}
		System.out.println("테이블 생성완료");
	}

	// 내부클래스
	class MultiServerT extends Thread {

		// 멤버변수
		Socket socket;
		PrintWriter out = null;
		BufferedReader in = null;

		// 생성자 : Socket을 기반으로 입출력 스트림을 생성한다.
		public MultiServerT(Socket socket) { ////////접속하는 클라이언트가 접속됨
			this.socket = socket;
			try {
				out = new PrintWriter(this.socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
			} catch (Exception e) {
				System.out.println("예외:" + e);
			}

		} //////////////////////////////MultiServerT 생성자 end...

		// 일반채팅 메소드
		public void sendAllMsg(String name, String msg) {

			// Map에 저장된 객체의 키값을 먼저 얻어온다.
			Iterator<String> it = clientMap.keySet().iterator();
			while (it.hasNext()) {
				try {
					// 각 클라이언트의 PrintWriter객체를 얻어온다.
					PrintWriter it_out = (PrintWriter) clientMap.get(it.next());

					// 클라이언트에게 메세지를 전달한다.
					/*
					 * 매개변수 name이 있는 경우에는 이름+메세지 없는경우에는 메세지만 클라이언트로 전달된다.
					 */
					if (name.contentEquals("")) {
						it_out.println(URLEncoder.encode(msg, "UTF-8"));
					} else {
						it_out.println("[" + URLEncoder.encode(name, "UTF-8") + "]:" + URLEncoder.encode(msg, "UTF-8"));
						input_data(name, msg);
					}
				} catch (UnsupportedEncodingException e) {

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// 리스트 출력 메소드
		public void sendList() {
			try {
				Iterator<String> it = clientMap.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					out.println("접속자명 : " + key);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 비밀메세지 처리 메소드
		public void sendSecretMsg(String[] msg, String name2) {
			String to_name = msg[1]; // 사용자이름
			String to_msg = msg[2]; // 보낼메세지

			for (int i = 3; i < msg.length; i++) {
				to_msg += " " + msg[i];
			}

			Iterator<String> it = clientMap.keySet().iterator();
			while (it.hasNext()) {
				try {
					// Map에 저장된 객체의 키값을 먼저 얻어온다.
					String key = it.next();
					// 각 클라이언트의 PrintWriter객체를 얻어온다.
					PrintWriter it_out = (PrintWriter) clientMap.get(key);
					if (to_name.equals(key)) {
						it_out.println(
								"[" + URLEncoder.encode(name2, "UTF-8") + "]:" + URLEncoder.encode(to_msg, "UTF-8"));
					}
				} catch (UnsupportedEncodingException e) {

				} catch (Exception e) {
					e.printStackTrace();
				}

			} // end of while
		}

		// 고정메세지 처리 메소드
		public void sendFixMsg(String[] msg, String name3) {
			String to_name = msg[1];
			boolean flag = true;
			while (flag) {
				// 클라이언트의 메세지을 읽어와서 저장
				String msg2 = null;
				try {
					in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
					msg2 = in.readLine();
					msg2 = URLDecoder.decode(msg2, "UTF-8");
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				Iterator<String> it = clientMap.keySet().iterator();
				while (it.hasNext()) {
					try {
						if (msg2.equals("x")) {
							flag = false;
							break;
						}
						// Map에 저장된 객체의 키값을 먼저 얻어온다.
						String key = it.next();
						// 각 클라이언트의 PrintWriter객체를 얻어온다.
						PrintWriter it_out = (PrintWriter) clientMap.get(key);
						if (to_name.equals(key)) {
							it_out.println(
									"[" + URLEncoder.encode(name3, "UTF-8") + "]:" + URLEncoder.encode(msg2, "UTF-8"));
						}
					} catch (UnsupportedEncodingException e) {

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} // end of while
		}

		// 블랙리스트 처리 메소드
		public void black_add(String black_name) {
			String query = "update user_tb set black_stat = ? where name = ?";
			try {
				psmt = con.prepareStatement(query);
				psmt.setString(1, "black");
				psmt.setString(2, black_name);
				psmt.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 블랙리스트 해제 메소드 - 미완성 하던중 
		public void black_removal(String black_name) {
			String query = "update user_tb set black_stat = ? where name = ?";
			try {
				psmt = con.prepareStatement(query);
				psmt.setString(1, "");
				psmt.setString(2, black_name);
				psmt.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// JDBC연동을 위해 사용됬던 자원 반납 메소드
		public void close() {
			try {
				if (con != null)
					con.close();
				if (psmt != null)
					psmt.close();
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
				System.out.println("자원 반납 완료");
			} catch (Exception e) {
				System.out.println("자원 반납시 오류 발생");
				e.printStackTrace();
			}
		}

		// 서버 작동부분 메소드
		@Override
		public void run() {

			// 클라이언트로부터 전송된 "대화명"을 저장할 변수
			String name = "";
			// 클라이언트로부터 전송된 "메세지"를 저장할 변수
			String s = "";
			// 중복저장 처리변수
			int flag = 0;
			try {
				// 클라이언트의 이름을 읽어와서 저장
				name = in.readLine();
				name = URLDecoder.decode(name, "UTF-8");

				while (true) {
					try {
						if (flag == 1) {
							name = in.readLine();
							name = URLDecoder.decode(name, "UTF-8");
						}
						String query2 = "insert into user_tb values (?, ?) ";
						psmt = con.prepareStatement(query2);
						psmt.setString(1, name);
						psmt.setString(2, null);
						psmt.executeUpdate();

					} catch (Exception e) {
						String ms = "이름을 다시 입력하세요.";
						out.println(URLEncoder.encode(ms, "UTF-8"));
						flag = 1;
						continue;
					}
					break;
				}

				// 접속한 클라이언트에게 새로운 사용자의 입장을 알림.
				// 접속자를 제외한 나머지 클라이언트만 입장메세지를 받는다.
				sendAllMsg("", name + "님이 입장하셨습니다.");

				// 현재 접속한 클라이언트를 HashMap에 저장한다.
				clientMap.put(name, out);

				// HashMap에 저장된 객체의 수로 접속자수를 파악할 수 있다.
				System.out.println(name + " 접속");
				System.out.println("현재 접속자 수는" + clientMap.size() + "명 입니다.");

				// 입력한 메세지는 모든 클라이언트에게 Echo된다.
				while (in != null) {
					s = in.readLine();
					s = URLDecoder.decode(s, "UTF-8");

					int se = 0;
					try {
						System.out.println("test1");
						String query3 = "select black_stat from user_tb where name = ?";
						psmt = con.prepareStatement(query3);
						psmt.setString(1, name);
						rs = psmt.executeQuery();
						while (rs.next()) {
							System.out.println("test2");
							String bname = rs.getString(1);
							if (bname.isEmpty()) {

							} else {
								se = 1;
								break;
							}
						}

					} catch (Exception e) {

					}

//					if (s.equals("xxx")) {
//						System.out.println("블랙리스트 처리를 하시겠습니까?(예:0 || 아니오:1)");
//						Scanner sc = new Scanner(System.in);
//						int opt = sc.nextInt();
//						if (opt == 0) {
//							System.out.println("블랙리스트 처리할 이름:");
//							String black_name = sc.next();
//							try {
//								String query = "update user_tb set black_stat = ? where name = ?";
//								psmt = con.prepareStatement(query);
//								psmt.setString(1, black_name);
//								psmt.setString(2, name);
//								psmt.executeUpdate();
//								break;
//							} catch (Exception e) {
//							}
//						}
//					}

					if (s == null)
						break;

					if (s.startsWith("/list")) {
						sendList();
					}

					else if (s.startsWith("/to")) {
						String[] username = s.split(" ");
						if (s.equals(username[0] + " " + username[1])) {
							sendFixMsg(username, name);

						} 
						else if (s.equals(username[0] + " " + username[1] + " " + username[2])) {
							sendSecretMsg(username, name);
						}

					}

					/*
					블랙리스트 기능을 선택 하는 부분
					/blacklist 명령어 상대방
					*/
					else if (s.startsWith("/blacklist")) {
						String[] blackname = s.split(" ");
						if (blackname[1].equals("add")) {
							black_add(blackname[2]);

						} else if (blackname[1].equals("removal")) {
							black_removal(blackname[2]);
						}
					}

					else {
						if (se == 1) {
							break;
						}
						System.out.println(name + " >> " + s);
						sendAllMsg(name, s);
					}
				}
			} catch (Exception e) {

			} finally {
				/*
				 * 클라이언트가 접속을 종료하면 예외가 발생하게 되어 finally로 넘어오게된다. 이때 "대화명"을 통해 remove() 시켜준다.
				 */
				clientMap.remove(name);
				sendAllMsg("", name + "님이 퇴장하셨습니다.");
				// 퇴장하는 클라이언트의 쓰레드명을 보여준다.
				System.out.println(name + "[" + Thread.currentThread().getName() + "] 퇴장");
				System.out.println("현재 접속자 수는" + clientMap.size() + "명 입니다.");

				try {
					in.close();
					out.close();
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} // end of finally

		}// end of run

	}// end of MultiServerT
}