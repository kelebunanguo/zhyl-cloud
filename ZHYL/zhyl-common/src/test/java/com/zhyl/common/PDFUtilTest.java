package com.zhyl.common;

import com.zhyl.common.utils.PDFUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PDFUtilTest {
    public static void main(String[] args) throws FileNotFoundException {

    FileInputStream fileInputStream = new FileInputStream("D:\\JAVA智能应用开发（AI）\\黑马博学谷AI智能应用开发（Java）就业课\\配套资料\\第04阶段  企业级智能物联网项目（中州养老）\\09. 智能评估-集成AI大模型\\资料\\体检报告样例\\体检报告-刘爱国-男-69岁.pdf");

    String result = PDFUtil.pdfToString(fileInputStream);
    System.out.println(result);

    }
}
