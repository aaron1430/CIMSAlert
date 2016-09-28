package com.alert.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author 包阿儒汉
 * @date 2016年9月23日
 */
public class Main {
	private static ApplicationContext applicationContext;
	private static final String springXmlFile = "META-INF/applicationContext.xml";

	// private static SpringContainer container=new SpringContainer();

	static {
		applicationContext = new ClassPathXmlApplicationContext(springXmlFile);
	}

	public static Object getBean(String beanName) {

		return applicationContext.getBean(beanName);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RunAlert runalert = (RunAlert) getBean("runAlert");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
		String now = "";
		 runalert.run();// 用于测试，发布时去掉，用下面的代码

//		while (true) {
//			now = df.format(new Date());
//			// 每天凌晨0点更新报警信息
//			if (now.equals("00:00"))
//				runalert.run();
//			else {
//				try {
//					System.out.println("没到时间");
//					Thread.sleep(20000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					System.out.println("报错");
//					e.printStackTrace();
//				}
//			}
//		}
	}

}
