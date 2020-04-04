package main.java.source;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DataSourceToBeanUtil extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 5725706474912002588L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_4;
	private JTextField textFiled_5;
	private JTextField textFiled_6;
	private JTextField textFiled_7;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					DataSourceToBeanUtil frame = new DataSourceToBeanUtil();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DataSourceToBeanUtil() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 200, 611, 475);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel label = new JLabel("数据源:");
		label.setBounds(156, 81, 54, 15);
		contentPane.add(label);

		textField = new JTextField();
		textField.setBounds(234, 78, 178, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel label_1 = new JLabel("用户名:");
		label_1.setBounds(156, 123, 54, 15);
		contentPane.add(label_1);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(234, 120, 178, 21);
		contentPane.add(textField_1);

		JLabel label_2 = new JLabel("密码:");
		label_2.setBounds(156, 162, 54, 15);
		contentPane.add(label_2);

		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(234, 159, 178, 21);
		contentPane.add(textField_2);

		//获取数据库地址 用户名  密码
		textField.setText("jdbc:mysql://192.168.1.131:3306/ptt_data_v2_fpp_test?useUnicode=true&characterEncoding=utf-8");
		textField_1.setText("root");
		textField_2.setText("pttdata");



		JCheckBox chckbxNewCheckBox = new JCheckBox("不是全库生成");
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setBounds(234, 318, 103, 23);
		contentPane.add(chckbxNewCheckBox);

		JCheckBox chckbxNewCheckBoxBean = new JCheckBox("生成bean文件");
		chckbxNewCheckBoxBean.setSelected(true);
		chckbxNewCheckBoxBean.setBounds(40, 348, 103, 23);
		contentPane.add(chckbxNewCheckBoxBean);

		JCheckBox chckbxNewCheckBoxHandler = new JCheckBox("生成handler文件");
		chckbxNewCheckBoxHandler.setSelected(true);
		chckbxNewCheckBoxHandler.setBounds(160, 348, 103, 23);
		contentPane.add(chckbxNewCheckBoxHandler);

		JCheckBox chckbxNewCheckBoxServiceImpl = new JCheckBox("生成ServiceImpl文件");
		chckbxNewCheckBoxServiceImpl.setSelected(true);
		chckbxNewCheckBoxServiceImpl.setBounds(280, 348, 103, 23);
		contentPane.add(chckbxNewCheckBoxServiceImpl);

		JCheckBox chckbxNewCheckBoxDao = new JCheckBox("生成Dao文件");
		chckbxNewCheckBoxDao.setSelected(true);
		chckbxNewCheckBoxDao.setBounds(404, 348, 103, 23);
		contentPane.add(chckbxNewCheckBoxDao);

		JButton btnNewButton = new JButton("生成");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String url=textField.getText();
				String user=textField_1.getText();
				String password=textField_2.getText();
				String tableName=textField_4.getText();
				String projectUrl=textFiled_5.getText();
				String beforeSrcUrl=textFiled_6.getText();
				String srcUrl=textFiled_7.getText();
				String qudongName=url.indexOf("mysql")>0?"com.mysql.jdbc.Driver":url.indexOf("oracle")>0?"":"";
				//判断是不是全库生成
				boolean flag=chckbxNewCheckBox.isSelected();
				//初始话数据
				TableToJavaTool.quDongName=qudongName;
				TableToJavaTool.user=user;
				TableToJavaTool.dbUrl=url;
				TableToJavaTool.password=password;
				TableToJavaTool.projectUrl=projectUrl;
				TableToJavaTool.beforeSrcUrl=beforeSrcUrl;
				TableToJavaTool.srcUrl=srcUrl;

				TableToJavaTool a=new TableToJavaTool();
				try {
					if(flag) {
						a.process(tableName,chckbxNewCheckBoxBean.isSelected(),chckbxNewCheckBoxHandler.isSelected(),chckbxNewCheckBoxServiceImpl.isSelected(),chckbxNewCheckBoxDao.isSelected());
					}else {
						a.process(chckbxNewCheckBoxBean.isSelected(),chckbxNewCheckBoxHandler.isSelected(),chckbxNewCheckBoxServiceImpl.isSelected(),chckbxNewCheckBoxDao.isSelected());
					}
					JOptionPane.showMessageDialog(contentPane, "成功", "提示",JOptionPane.WARNING_MESSAGE);
				}catch(Exception e1) {
					JOptionPane.showMessageDialog(contentPane, "生成失败!\n\r\n原因:"+e1.getMessage(), "提示",JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		btnNewButton.setBounds(234, 385, 93, 23);
		contentPane.add(btnNewButton);

		JLabel lblNewLabel = new JLabel("目标表名");
		lblNewLabel.setBounds(358, 322, 54, 15);
		contentPane.add(lblNewLabel);

		textField_4 = new JTextField();
		textField_4.setBounds(422, 319, 122, 21);
		contentPane.add(textField_4);
		textField_4.setColumns(10);

		textFiled_5 = new JTextField();
//		textFiled_5.setText("C:\\ecplise workspace\\ChaoqiIsPrivateLibrary-master\\springboot_swagger");
		textFiled_5.setText("D:\\ecplise workspace2\\PTTWeb4.80");
		textFiled_5.setColumns(10);
		textFiled_5.setBounds(234, 204, 178, 21);
		contentPane.add(textFiled_5);

		JLabel lblController = new JLabel("项目路径地址:");
		lblController.setBounds(133, 207, 103, 15);
		contentPane.add(lblController);

		JLabel lblService = new JLabel("源代码前缀地址:");
		lblService.setBounds(133, 242, 103, 15);
		contentPane.add(lblService);

		JLabel lblServiceimpl = new JLabel("源代码地址:");
		lblServiceimpl.setBounds(133, 277, 89, 15);
		contentPane.add(lblServiceimpl);

		textFiled_6 = new JTextField();
		textFiled_6.setText("src");
		textFiled_6.setColumns(10);
		textFiled_6.setBounds(234, 239, 178, 21);
		contentPane.add(textFiled_6);

		textFiled_7 = new JTextField();
		textFiled_7.setText("ptt\\proxyapi");
		textFiled_7.setColumns(10);
		textFiled_7.setBounds(234, 274, 178, 21);
		contentPane.add(textFiled_7);

		JTextPane txtpnJavabeanbeancontrollercontrollerservice = new JTextPane();
		txtpnJavabeanbeancontrollercontrollerservice.setBackground(Color.WHITE);
		txtpnJavabeanbeancontrollercontrollerservice.setForeground(Color.RED);
		txtpnJavabeanbeancontrollercontrollerservice.setText("javabean目录 默认为bean，controller目录默认为controller ，service 目录为默认service.inter   serviceimpl目录为默认 service.impl   dao目录为默认 dao");
		txtpnJavabeanbeancontrollercontrollerservice.setBounds(23, 10, 547, 38);
		contentPane.add(txtpnJavabeanbeancontrollercontrollerservice);

	}
}
///