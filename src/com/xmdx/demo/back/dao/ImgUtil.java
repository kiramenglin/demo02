package com.xmdx.demo.back.dao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.xmzy.framework.service.MessageService;


/**
 * 
 * 图片处理
 * 
 * 
 */
public class ImgUtil {

	/**
	 * 将图片转成base64编码
	 * @param imgFilePath 图片路径
	 * @return
	 */
	public static String img2Base64Code(String imgFilePath) {
		byte[] data = null;

		// 读取图片字节数组
		try {
			InputStream in = new FileInputStream(imgFilePath);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			MessageService.errString("图片转成base64编码出错");
		}

		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);// 返回Base64编码过的字节数组字符串
	}
	
	/**
	 * 将base64编码反转为图片
	 * 
	 * @param imgStr base64编码图片
	 * @param imgFilePath 图片路径
	 * @return true:转换成功
	 */
	public static boolean base64Code2Img(String imgStr, String imgFilePath) {
		if (imgStr == null) {
			return false;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] bytes = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < bytes.length; ++i) {
				// 调整异常数据
				if (bytes[i] < 0) {
					bytes[i] += 256;
				}
			}
			// 生成图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(bytes);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}