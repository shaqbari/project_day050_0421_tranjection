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
		bt = new JButton("���");
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

	// �����Ͻ� �ΰ��� ���̺�� �и��Ǿ� �����Ƿ�, ������ ����(�������� ���Ἲ�� ����)�� �����
	// Ʈ������� ������ ���α׷��� �ۼ��غ���.
	// "������" �̶�� ������ ��� ���ξ����� �̷���� Ʈ������ΰ�? 2��
	public void regist() {
		StringBuffer sql = new StringBuffer();
		PreparedStatement pstmt = null;

		// ���߿� �ϳ��� �Է¿� �����ϸ�, ó������ ������ �Ϸ� �ǵ��� ����!
		// Ʈ������� rollback!
		try {
			//Connection��ü���� setAutocommit();�� �ִ�.
			//con.setAutoCommit(true);�� �޼ҵ尡 ����Ʈ�� true�� �Ǿ� �ֱ� ������
			//jdbc�� �̿��� dml�� �����ڰ� ������ commit�� ���� �ʾƵ� �Ǿ�����.
			
			//�Ʒ� �޼ҵ�� commit�� �ڵ��������� �ʰڴٴ� �ǹ��̱⵵ ������ Ʈ������� ���۵ȴٴ� �� �߿��� �ǹ̸� ���´�.
			con.setAutoCommit(false);//Ʈ������� ����
			
			// staff���̺� insert
			sql.append("insert into staff(staff_id, name, age)");
			sql.append(" values(seq_staff.nextVal, ?, ?)");
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, t_name.getText());
			pstmt.setInt(2, Integer.parseInt(t_age.getText()));
			pstmt.executeUpdate();
			
			// bio���̺� insert
			sql.delete(0, sql.length());// ���������
			sql.append("insert into bio(staff_id, weight, height)");
			sql.append(" values(seq_staff.currval, ?, ?)"); // currVal�� ����Էµ� seq�� �Է����ش�.
			pstmt=con.prepareStatement(sql.toString());
			pstmt.setInt(1, Integer.parseInt(t_weight.getText()));
			pstmt.setInt(2, Integer.parseInt(t_age.getText()));
			pstmt.executeUpdate();
			
			//���� jdbc������ 2��°���� ������ ���� �ڵ����� �ѹ��ϰ� catch������ ����. -->finally���� commit
			//EJB java���� �����ؼ� tranjection�����ϵ��� ������ ������ --> spring
			
			JOptionPane.showMessageDialog(this, "insert�Ϸ�");
			try {
				con.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			try {
				System.out.println("�ѹ��Ѵ�.");
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		} finally {//���⼭ commit�� �Ѵ�!
			//Connection ��ü�� autoCommit �Ӽ��� �ٽ� ��������
			//con�� �ٸ� �޼ҵ忡�� ����� ���� Ʈ������� �������� ���� ��쵵 �����Ƿ�,,
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
