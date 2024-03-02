package com.app.kit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@Slf4j
public class CommandUtil {

	public static String exeShellCmd(final String cmd) throws IOException {
		log.debug("start exe shell cmd --> " + cmd);
		final Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
		final List<String> consoles = IOUtils.readLines(process.getInputStream(), StandardCharsets.UTF_8);
		final List<String> errors = IOUtils.readLines(process.getErrorStream());
		collectionToString(consoles);
		final InputStreamReader reader = new InputStreamReader(process.getInputStream());
		final LineNumberReader lineReader = new LineNumberReader(reader);
		final InputStreamReader errorReader = new InputStreamReader(process.getErrorStream());
		final LineNumberReader lineErrorReader = new LineNumberReader(errorReader);
		String lineError = null;
		final StringBuilder sb = new StringBuilder();
		while ((lineError = lineErrorReader.readLine()) != null) {
			sb.append(lineError);
		}
		reader.close();
		lineReader.close();
		process.destroy();
		final String result = sb.toString();
		log.debug("finish exe shell cmd --> " + result);
		return result;
	}

	public static String collectionToString(final Collection<?> collection) {
		final StringBuilder sb = new StringBuilder();
		for (final Object o : collection) {
			sb.append(o.toString()).append("\n");
		}
		return sb.toString();
	}

	public static void main(final String[] args) throws IOException {
		final String filePath = "/Users/qiangt/cmd.sh";
		Runtime.getRuntime().exec(String.format("chmod %d %s", 777, filePath));
		// exeShellCmd(filePath);
		// Runtime.getRuntime().exec("sh /Users/qiangt/WebstormProjects/welfare-web/pkd-prod.sh");
		final String result = executeCommand(filePath);
		System.out.println(result);
	}

	public static String executeCommand(final String command) {
		log.info("执行shell脚本开始");
		try {
			final Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
			try (final InputStream errors = process.getErrorStream()) {
				final String error = collectionToString(IOUtils.readLines(errors, "UTF-8"));
				if (StringUtils.isNotEmpty(error)) {
					log.info("errors: {}", collectionToString(IOUtils.readLines(errors, "UTF-8")));
				}
			}
			try (final InputStream consoles = process.getInputStream()) {
				return collectionToString(IOUtils.readLines(consoles, "UTF-8"));
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} finally {
			log.info("执行shell脚本结束");
		}
	}

	/**
	 * Windows cmd 命令执行工具
	 * 有时候我们可能需要调用系统外部的某个程序，此时就可以用Runtime.getRuntime().exec()来调用，它会生成一个新的进程去运行调用的程序。
	 * 此方法返回一个java.lang.Process对象，该对象可以得到之前开启的进程的运行结果，还可以操作进程的输入输出流。
	 * Process对象有以下几个方法：
	 * 1、destroy()　　　　　　杀死这个子进程
	 * 2、exitValue()　　　 　 得到进程运行结束后的返回状态
	 * 3、waitFor()　　　　 　 得到进程运行结束后的返回状态，如果进程未运行完毕则等待直到执行完毕
	 * 4、getInputStream()　　 得到进程的标准输出信息流
	 * 5、getErrorStream()　　 得到进程的错误输出信息流
	 * 6、getOutputStream()　  得到进程的输入流
	 * 现在来讲讲exitValue()，当线程没有执行完毕时调用此方法会跑出IllegalThreadStateException异常，最直接的解决方法就是用waitFor()方法代替。
	 * 但是waitFor()方法也有很明显的弊端，因为java程序给进程的输出流分配的缓冲区是很小的，有时候当进程输出信息很大的时候会导致缓冲区被填满，
	 * 如果不及时处理程序会阻塞。如果程序没有对进程的输出流处理的会就会导致执行exec()的线程永远阻塞，
	 * 进程也不会执行下去直到输出流被处理或者java程序结束。
	 * 解决的方法就是处理缓冲区中的信息，开两个线程分别去处理标准输出流和错误输出流。
	 */
	public static String exeRuntimeCmd(final String cmd) throws Exception {
		log.debug("start exe windows cmd --> " + cmd);
		BufferedReader bufferedReader = null;
		Process process = null;
		final StringBuilder sb = new StringBuilder();
		try {
			String buffer;
			process = Runtime.getRuntime().exec(cmd);
			// 得到返回结果
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "gbk"));
			while ((buffer = bufferedReader.readLine()) != null) {
				sb.append(buffer + "\n");
			}
			bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "gbk"));
			while ((buffer = bufferedReader.readLine()) != null) {
				sb.append(buffer + "\n");
			}
			// 等待进程执行完毕
			process.waitFor();
			return sb.toString().trim();
		} catch (final Exception e) {
			log.error("exe windows cmd occured error --> " + e.getMessage(), e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
					bufferedReader = null;
				} catch (final Exception e) {
					log.error("exe windows cmd occured error --> " + e.getMessage(), e);
				}
			}
			if (process != null) {
				try {
					process.destroy();
					process = null;
				} catch (final Exception e) {
					log.error("exe windows cmd occured error --> " + e.getMessage(), e);
				}
			}

		}
		final String result = sb.toString();
		log.debug("finish exe windows cmd --> " + result);
		return result;
	}

}
