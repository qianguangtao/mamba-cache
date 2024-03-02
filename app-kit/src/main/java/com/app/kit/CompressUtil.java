package com.app.kit;

import cn.hutool.core.io.IoUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩工具类
 * @author qiangt
 * @since 2022-04-18
 */
public class CompressUtil {

	private static final int BUFFER_SIZE = 2 * 1024;

	// public static void doZip(String zipDir, String zipUrl) {
	//     try {
	//         FileUtil.checkDirectory(zipDir);
	//         FileUtil.checkFile(zipUrl);
	//         ZipUtil.toZip(zipDir, new FileOutputStream(zipUrl), true);
	//     } catch (FileNotFoundException e) {
	//         throw new DataAccessException(
	//                 Error.builder()
	//                         .code(Code.DATA_ACCESS_FILE_HANDLE_ERROR)
	//                         .message(String.format("file not found"))
	//                         .arg("zipDir", zipUrl)
	//                         .build()
	//         );
	//     }
	//     FileUtil.chmodFile(zipUrl);
	// }

	/**
	 * 压缩（注意：不保留目录结构可能会出现同名文件, 会压缩失败）
	 * @param srcDir           压缩文件夹路径
	 * @param out              压缩文件输出流
	 * @param KeepDirStructure 是否保留原来的目录结构（true：保留目录结构；false：所有文件跑到压缩包根目录下）
	 * @throws RuntimeException 压缩失败会抛出运行时异常
	 */
	public static void toZip(final String srcDir, final OutputStream out, final boolean KeepDirStructure) throws RuntimeException {
		final long start = System.currentTimeMillis();
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(out);
			final File sourceFile = new File(srcDir);
			compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
			final long end = System.currentTimeMillis();
			System.out.println("压缩完成，耗时：" + (end - start) + " ms");
		} catch (final Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (final IOException e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 压缩
	 * @param zipItemList 需要压缩的文件列表
	 * @param out         压缩文件输出流
	 * @throws RuntimeException 压缩失败会抛出运行时异常
	 */
	public static void createZip(final List<ZipItem> zipItemList, final OutputStream out) throws RuntimeException {
		final long start = System.currentTimeMillis();
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(out);
			for (final ZipItem zipItem : zipItemList) {
				final byte[] buf = new byte[BUFFER_SIZE];
				zos.putNextEntry(new ZipEntry(zipItem.getName()));
				int len;
				final InputStream in = zipItem.getInputStream();
				while ((len = in.read(buf)) != -1) {
					zos.write(buf, 0, len);
				}
				zos.closeEntry();
				in.close();
			}
			final long end = System.currentTimeMillis();
			System.out.println("压缩完成，耗时：" + (end - start) + " ms");
		} catch (final Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (final IOException e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			}
		}
	}

	public static void main(final String[] args) throws Exception {
		// 测试压缩方法1
		// FileOutputStream fos1 = new FileOutputStream(new File("/Users/qiangt/Downloads/ziptest1.zip"));
		// ZipUtil.toZip("/Users/qiangt/Downloads/activiti-template", fos1, true);
		// 测试压缩方法2
		final List<File> fileList = new ArrayList<>();
		fileList.add(new File("/Users/qiangt/Downloads/test1.pdf"));
		fileList.add(new File("/Users/qiangt/Downloads/CommissionPdf.pdf"));
		final FileOutputStream fos2 = new FileOutputStream(new File("/Users/qiangt/Downloads/ziptest2.zip"));
		CompressUtil.toZip(fileList, fos2);
	}

	/**
	 * 压缩
	 * @param srcFiles 需要压缩的文件列表
	 * @param out      压缩文件输出流
	 * @throws RuntimeException 压缩失败会抛出运行时异常
	 */
	public static void toZip(final List<File> srcFiles, final OutputStream out) throws RuntimeException {
		final long start = System.currentTimeMillis();
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(out);
			for (final File srcFile : srcFiles) {
				final byte[] buf = new byte[BUFFER_SIZE];
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int len;
				final FileInputStream in = new FileInputStream(srcFile);
				while ((len = in.read(buf)) != -1) {
					zos.write(buf, 0, len);
				}
				zos.closeEntry();
				in.close();
			}
			final long end = System.currentTimeMillis();
			System.out.println("压缩完成，耗时：" + (end - start) + " ms");
		} catch (final Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			if (zos != null) {
				try {
					zos.close();
				} catch (final IOException e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 递归压缩（注意：不保留目录结构可能会出现同名文件,会压缩失败）
	 * @param sourceFile       源文件
	 * @param zos              zip输出流
	 * @param name             压缩后的名称
	 * @param keepDirStructure 是否保留原来的目录结构（true：保留目录结构；false：所有文件跑到压缩包根目录下）
	 */
	private static void compress(final File sourceFile, final ZipOutputStream zos, final String name, final boolean keepDirStructure) throws IOException {
		final byte[] buffer = new byte[BUFFER_SIZE];
		if (sourceFile.isFile()) {
			// 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
			zos.putNextEntry(new ZipEntry(name));
			// copy文件到zip输出流中
			int len;
			final FileInputStream in = new FileInputStream(sourceFile);
			while ((len = in.read(buffer)) != -1) {
				zos.write(buffer, 0, len);
			}
			IoUtil.copy(new FileInputStream(sourceFile), zos);
			// Complete the entry
			zos.closeEntry();
			in.close();
		} else {
			final File[] listFiles = sourceFile.listFiles();
			if (listFiles == null || listFiles.length == 0) {
				// 需要保留原来的文件结构时,需要对空文件夹进行处理
				if (keepDirStructure) {
					// 空文件夹的处理
					zos.putNextEntry(new ZipEntry(name + "/"));
					// 没有文件，不需要文件的copy
					zos.closeEntry();
				}
			} else {
				for (final File file : listFiles) {
					// 判断是否需要保留原来的文件结构
					if (keepDirStructure) {
						// 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
						// 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
						compress(file, zos, name + "/" + file.getName(), keepDirStructure);
					} else {
						compress(file, zos, file.getName(), keepDirStructure);
					}
				}
			}
		}
	}

	@Setter
	@Getter
	@Builder
	public static class ZipItem {
		private InputStream inputStream;
		private String name;
	}

}
