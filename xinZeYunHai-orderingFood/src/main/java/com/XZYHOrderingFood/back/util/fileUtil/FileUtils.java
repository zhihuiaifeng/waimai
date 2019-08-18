package com.XZYHOrderingFood.back.util.fileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.n3r.idworker.IdWorker;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;

 

public class FileUtils {
	 private String basePath = null;  
	  
	     
	    public void setUp() {  
	        basePath = System.getProperty("user.dir") + "\\file\\";  
	        
	    }  
	  
	    
	    public void tearDown() throws Exception {  
	    }  
	  
	    /** 
	     * 拷贝文件 
	     * @throws IOException 
	     */  
	   
	    public void testCopy() throws IOException {  
	        File srcFile = new File(basePath + "a.txt");  
	        File destFile = new File(basePath + "b.txt");  
	        org.apache.commons.io.FileUtils.copyDirectory(srcFile, destFile);  
	    }  
	      
	    /** 
	     * 删除文件 
	     * @throws IOException 
	     */  
 
	    public void testDelete() throws IOException{  
	        File delFile = new File(basePath + "b.txt");  
	        org.apache.commons.io.FileUtils.forceDelete(delFile);  
	        //FileUtils.forceMkdir(delFile);  
	    }  
	      
	    /** 
	     * 比较文件内容 
	     * @throws IOException 
	     */  
	    
	    public void testCompareFile() throws IOException{  
	        File srcFile = new File(basePath + "a.txt");  
	        File destFile = new File(basePath + "b.txt");  
	        boolean result = org.apache.commons.io.FileUtils.contentEquals(srcFile, destFile);  
	        System.out.println(result);  
	    }  
	      
	    /** 
	     * 移动文件 
	     * @throws IOException 
	     */  
	     
	    public void testMoveFile() throws IOException{  
	        File srcFile = new File(basePath + "b.txt");  
	        File destDir = new File(basePath + "move");  
	        org.apache.commons.io.FileUtils.moveToDirectory(srcFile, destDir, true);  
	    }  
	      
	    /** 
	     * 读取文件内容 
	     * @throws IOException 
	     */  
	    
	    public void testRead() throws IOException{  
	        File srcFile = new File(basePath + "a.txt");  
	        String content = org.apache.commons.io.FileUtils.readFileToString(srcFile);  
	        List<String> contents = org.apache.commons.io.FileUtils.readLines(srcFile);  
	        System.out.println(content);  
	        System.out.println("******************");  
	        for (String string : contents) {  
	            System.out.println(string);  
	        }  
	    }  
	      
	    /** 
	     * 写入文件内容 
	     * @throws IOException 
	     */  
	     
	    public void testWrite() throws IOException{  
	        File srcFile = new File(basePath + "a.txt");  
	        org.apache.commons.io.FileUtils.writeStringToFile(srcFile, "\nyes文件", true);  
	    } 
	    
	     
	    
	   
}

