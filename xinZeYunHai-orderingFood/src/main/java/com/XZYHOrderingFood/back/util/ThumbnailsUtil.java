package com.XZYHOrderingFood.back.util;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * 图片压缩工具类
 * @author pc
 *
 */
public class ThumbnailsUtil {
	
	/** 
	 * 指定大小进行缩放   
	 * @param width
	 * @param height
	 * @param img
	 * @param outFilepath
	 * @param keepAspectRatio    
	 */
	public static String shrinkToSize(int width,int height,String img,String outFilepath,String newFileName){
	String tempName	= newFileName.substring(0, newFileName.lastIndexOf("."));
		try {
			Thumbnails.of(img)
			.size(width, height)
			.keepAspectRatio(false)
			.toFile(outFilepath + tempName+ "th.jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempName+ "th.jpg";
	}
	
	/**
	 * 按照比例进行缩放 
	 * @param scale
	 * @param img
	 * @param outFilepath
	 */
	public static boolean shrinkToScale(Double scale,String img,String outFilepath){
		try {
			Thumbnails.of(img)
			.scale(0.5f)
			.toFile(outFilepath);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 水印 
	 * @param width
	 * @param height
	 * @param img
	 * @param watermarkIMG
	 * @param outFilepath
	 */
	public static void addWatermark(int width,int height,String img,String watermarkIMG,String outFilepath){
		//watermark(位置，水印图，透明度)  
		try {
			Thumbnails.of(img)   
			.size(width, height)  
			.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(watermarkIMG)), 0.5f)   
			.outputQuality(0.8f)   
			.toFile(outFilepath);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 水印 
	 * @param img
	 * @param watermarkIMG
	 * @param outFilepath
	 */
	public static void addWatermark(String img,String watermarkIMG,String outFilepath){
		//watermark(位置，水印图，透明度)  
		try {
			Thumbnails.of(img)
			.scale(1f)
			.watermark(Positions.TOP_RIGHT, ImageIO.read(new File(watermarkIMG)), 0.5f)   
			.outputQuality(0.8f)   
			.toFile(outFilepath);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * 旋转
	 * //rotate(角度),正数：顺时针 负数：逆时针  
	 * @param scale
	 * @param img
	 * @param outFilepath
	 */
	public static void retateImg(double scale,double angle,String img,String outFilepath){
		try {
			Thumbnails.of(img)
			.scale(scale)
			.rotate(angle)
			.toFile(outFilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	 
}
