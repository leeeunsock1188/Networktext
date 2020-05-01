package chat8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class IConnectImpl implements IConnect {
	// 동적쿼리 처리를 위한 객체
	public PreparedStatement psmt;
	public Connection con;
	protected Statement stmt;
	public ResultSet rs;

	public IConnectImpl() {
		System.out.println("IConnectImpl 기본생성자 호출");
	}

	public IConnectImpl(String user, String pass) {
		System.out.println("IConnectImpl 인자생성자 호출");
		try {
			// 드라이버 로드
			Class.forName(ORACLE_DRIVER);
			// DB연결
			connect(user, pass);
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("알수 없는 예외발생");
		}
	}

	@Override
	public void connect(String user, String pass) {
		try {
			con = DriverManager.getConnection(ORACLE_URL, user, pass);
		} catch (SQLException e) {
			System.out.println("데이터베이스 연결 오류");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("알수 없는 예외발생");
		}
	}

	//객체반환
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

}