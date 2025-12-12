package com.zhyl;

import com.zhyl.generator.util.VelocityInitializer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.FileWriter;
import java.io.IOException;

public class VelocityDemoTest {
    public static void main(String[] args) throws IOException {
        // 1.初始化Velocity模板引擎
        VelocityInitializer.initVelocity();

        // 2.创建Velocity上下文对象
        VelocityContext context = new VelocityContext();
        context.put("message", "加油朋友~~~");

        // 3.获取模板
        Template template = Velocity.getTemplate("vms/index.html.vm", "utf-8");

        // 4.准备一个输出流，用来将结果文件写出去
        FileWriter fileWriter = new FileWriter("zhyl-generator\\src\\main\\resources\\index.html");

        // 5.合并模板和数据模型
        template.merge(context, fileWriter);

        // 6.关闭输出流
        fileWriter.close();
    }
}
