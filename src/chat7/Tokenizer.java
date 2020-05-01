package chat7;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class Tokenizer {
	/*
	 * 문자열을 잘라주는 클래스 StringTokeniozer입니다. 비슷한 메소드로 String 클래스에 split()메소드가 있죠
	 * 
	 * StringTokenizer생성자 2개를 소개한다.
	 * 
	 * StringTokenizer(String str) //str문자열을 자르기 위한 토크나이저 객체를 생성한다. 자르는 기준을 기입하지
	 * 않았으므로 '스페이스바'를 기준으로 문자열을 자른다.
	 * 
	 * StringTokenizer(String str, String split) // str문자열을 자르기 위한 토크나이저 객체를 생성한다.
	 * split에 입력된 문자열을 기준으로 문자를 잘라냄 기준 문자는 문자열에서 소멸된다.
	 */

	public static void main(String[] args) {

//		String str = "1101,한송이,45,67,89,100";
//		StringTokenizer st = new StringTokenizer(str, ",");
//		String[] array = new String[st.countTokens()]; // countTokens() :문자열을 잘라낸 결과로 가지게될 토근의 객수를 리턴합ㄴ디ㅏ. 즉 몇개로 짤리는지 볼
//														// 수있음
//		int i = 0;
//		while (st.hasMoreElements()) { // 현재 토크나이저 안에 받아낼 토근이 더 있는지 체크한다.
//			array[i++] = st.nextToken(); // 토크나이저는 넥스트 토큰을 통해 순차적으로 토근을 받아냅니다. 앞에서부터 1토큰을 짤라낸다 다음번 nexToken()에서는 이번에 받은
//											// 문자열의
//			// 다음 문자열을 리턴한다.
//
//		}
//		for (int j = 0; j < array.length; j++) {
//			System.out.println(array[j]);
//		}
		
//		String msg = "/to 윤정현 ㄴㅁㅇㄹㄴㅇㅁㄹ ㅇ마른마이르ㅏ 니으리 ㅏㄴ음 ㄻㄴㅇㄹ";
//		int startIdx = msg.indexOf(" ") + 1;		// 첫번쨰 띄어쓰기
//		int endIdx = msg.indexOf(" ",startIdx); //startIdx이후에  ""를 찾음 
//		System.out.println(startIdx + "  end : " + endIdx);
//		String sub = msg.substring(startIdx, endIdx);
//		System.out.println(sub);
		
		 
		//db
		
//		String sub = msg.substring(msg.indexOf(" ") + 1, msg.indexOf(" ", msg.indexOf(" ") + 1));
//		System.out.println(msg.indexOf(sub));
//		System.out.println(msg.indexOf(" ", msg.indexOf(sub)));
//		System.out.println(msg.substring(msg.indexOf(" ", msg.indexOf(sub))+1 , msg.length()));
//		System.out.println(sub);
////		System.out.println(msg.substring(4,7));
//		
//		ConnectDB db = new ConnectDB(); 
//		String query = "select * from CHATTING_TD";
//		List<HashMap<String, Object>> listAll = db.getSelectList(query);
//		for(int i=0; i<listAll.size(); i++) {
//			System.out.println(listAll.get(i).toString());
//		}
//		
//		String query2 = "select chat_name from chatting_td where chat_seq = 10";
//		String data = db.getSelectOne(query2);
//		System.out.println(data);
//		
		
		
		String a = "이은석";
		System.out.println(a.substring(0,2));
		
		
		
		
		
		
		
		
		
		
		
		
		
//		List<String> blackList = db.getBlackList(myName);
//		for(balckList) {
//			if(afdmkldsamf.equasl(name2)) {
//				
//			}else {
//				
//			}
//		}
	}

}
