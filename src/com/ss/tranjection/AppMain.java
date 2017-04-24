package com.ss.tranjection;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class AppMain extends JFrame {
	JTextField t_name, t_age, t_weight, t_height;
	JButton bt;

	DBManager manager = DBManager.getInstance();
	Connection con;

	public AppMain() {
		setLayout(new FlowLayout());
		t_name = new JTextField(13);
		t_age = new JTextField(13);
		t_weight = new JTextField(13);
		t_height = new JTextField(13);
		bt = new JButton("등록");
		con = manager.getConnection();

		add(t_name);
		add(t_age);
		add(t_weight);
		add(t_height);
		add(bt);

		bt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regist();
			}
		});

		setSize(200, 180);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

	}

	// 사원등록시 두개의 테이블로 분리되어 있으므로, 만일의 사태(데이터의 무결성이 깨짐)에 대비해
	// 트랜잭션을 적용한 프로그램을 작성해본다.
	// "사원등록" 이라는 업무는 몇개의 세부업무로 이루어진 트랜잭션인가? 2개
	public void regist() {
		StringBuffer sql = new StringBuffer();
		PreparedStatement pstmt = null;

		// 둘중에 하나라도 입력에 실패하면, 처음부터 없었던 일로 되돌려 놓자!
		// 트랜잭션의 rollback!
		try {
			//Connection객체에는 setAutocommit();가 있다.
			//con.setAutoCommit(true);이 메소드가 디폴트로 true로 되어 있기 때문에
			//jdbc를 이용한 dml은 개발자가 별도의 commit를 하지 않아도 되었었다.
			
			//아래 메소드는 commit를 자동으로하지 않겠다는 의미이기도 하지만 트랜잭션이 시작된다는 더 중요한 의미를 갖는다.
			con.setAutoCommit(false);//트랜잭션을 시작
			
			// staff테이블 insert
			sql.append("insert into staff(staff_id, name, age)");
			sql.append(" values(seq_staff.nextVal, ?, ?)");
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, t_name.getText());
			pstmt.setInt(2, Integer.parseInt(t_age.getText()));
			pstmt.executeUpdate();
			
			// bio테이블에 insert
			sql.delete(0, sql.length());// 버퍼지우기
			sql.append("insert into bio(staff_id, weight, height)");
			sql.append(" values(seq_staff.currval, ?, ?)"); // currVal은 방금입력된 seq를 입력해준다.
			pstmt=con.prepareStatement(sql.toString());
			pstmt.setInt(1, Integer.parseInt(t_weight.getText()));
			pstmt.setInt(2, Integer.parseInt(t_age.getText()));
			pstmt.executeUpdate();
			
			//원래 jdbc에서는 2번째에서 에러가 나면 자동으로 롤백하고 catch문으로 간다. -->finally에서 commit
			//EJB java에서 개발해서 tranjection관리하도록 했지만 복잡해 --> spring
			
			JOptionPane.showMessageDialog(this, "insert완료");
			try {
				con.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			try {
				System.out.println("롤백한다.");
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		} finally {//여기서 commit을 한다!
			//Connection 객체의 autoCommit 속성을 다시 돌려놓자
			//con을 다른 메소드에서 사용할 때는 트랜잭션을 적용하지 않을 경우도 있으므로,,
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public static void main(String[] args) {
		new AppMain();
	}
}
