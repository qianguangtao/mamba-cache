package com.app.kit;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public abstract class NativeUtil {

	/**
	 * 获取机器名
	 */
	public static String getHostName() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostName();
	}

	/**
	 * 获取本机IP地址
	 */
	private static String getIpAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

}
