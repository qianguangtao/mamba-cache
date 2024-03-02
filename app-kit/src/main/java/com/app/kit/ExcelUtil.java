package com.app.kit;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class ExcelUtil {


    public static <T> void doExport(List<T> data, Class<T> clazz, HttpServletResponse response, String fileName) {
        try {
            export(data, clazz, response, fileName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static <T> void export(List<T> data, Class<T> clazz, HttpServletResponse response, String fileName) throws IOException {

        StopWatch watch = new StopWatch();
        watch.start();

        // response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));
        // 发送一个报头，告诉浏览器当前页面不进行缓存，每次访问的时间必须从服务器上读取最新的数据
        response.setHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "max-age=0");

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        // 这里 需要指定写用哪个class去写
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), clazz).build();
        // 这里注意 如果同一个sheet只要创建一次
        WriteSheet writeSheet = EasyExcel.writerSheet("sheet0").build();
        excelWriter.write(data, writeSheet);
        /// 千万别忘记finish 会帮忙关闭流
        excelWriter.finish();

        watch.stop();
        log.info("导出Excel完成，耗时: {}秒", watch.getTotalTimeSeconds());
    }
}
