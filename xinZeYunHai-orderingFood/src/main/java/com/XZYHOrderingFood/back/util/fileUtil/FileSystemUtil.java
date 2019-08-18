package com.XZYHOrderingFood.back.util.fileUtil;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.comparator.CompositeFileComparator;
import org.apache.commons.io.comparator.DirectoryFileComparator;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.comparator.PathFileComparator;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;


public class FileSystemUtil {
  public String basePath =  System.getProperty("user.dir") + "\\file\\"; //用户根目录定义绝对路径
/*
 * 
 * 获取磁盘剩余空间
 */
	  public void testFreeSpace() throws IOException {  
	        // 以字节为单位  
	        System.out.println(FileSystemUtils.freeSpace("c:\\") / 1024 / 1024 / 1024);  
	        System.out.println(FileSystemUtils.freeSpace("d:\\") / 1024 / 1024 / 1024);  
	        // 以k为单位  
	        System.out.println(FileSystemUtils.freeSpaceKb("e:\\") / 1024 / 1024);  
	        System.out.println(FileSystemUtils.freeSpaceKb("f:\\") / 1024 / 1024);  
	          
	    } 
	  
	  /** 
	     * 测试行迭代器 
	     * @throws IOException 
	     */  
	     
	    public void testIterator() throws IOException{  
	    	
	        File file = new File(basePath + "a.txt");  
	        LineIterator li = org.apache.commons.io.FileUtils.lineIterator(file);  
	        while(li.hasNext()){  
	            System.out.println(li.nextLine());  
	        }  
	        LineIterator.closeQuietly(li);  
	    }  
	    
	    
	    /** 
	     * 空内容文件过滤器 
	     * @throws IOException 
	     */  
	   
	    public void testEmptyFileFilter() throws IOException{  
	        File dir = new File(basePath);  
	        String[] files = dir.list(EmptyFileFilter.NOT_EMPTY);  
	        for (String file : files) {  
	            System.out.println(file);  
	        }  
	    }  
	      
	    /** 
	     * 文件名称后缀过滤器 
	     * @throws IOException 
	     */  
	     
	    public void testSuffixFileFilter() throws IOException{  
	        File dir = new File(basePath);  
	        String[] files = dir.list(new SuffixFileFilter("a.txt"));  
	        for (String file : files) {  
	            System.out.println(file);  
	        }  
	    }  
	    
	    /** 
	     * 文件名称比较器 
	     * @throws IOException 
	     */  
	  
	    public void testNameFileComparator() throws IOException {  
	        File f1 = new File(basePath + "a.txt");  
	        File f2 = new File(basePath + "c.txt");  
	        int result = NameFileComparator.NAME_COMPARATOR.compare(f1, f2);  
	        System.out.println(result);  
	    }  
	  
	    /** 
	     * 文件路径比较器 
	     * @throws IOException 
	     */  
	  
	    public void testPathFileComparator() throws IOException {  
	        File f1 = new File(basePath + "a.txt");  
	        File f2 = new File(basePath + "c.txt");  
	        int result = PathFileComparator.PATH_COMPARATOR.compare(f1, f2);  
	        System.out.println(result);  
	    }  
	  
	    /** 
	     * 组合比较器 
	     * @throws IOException 
	     */  
	    @SuppressWarnings("unchecked")  
	  
	    public void testCompositeFileComparator() throws IOException {  
	        File dir = new File(basePath);  
	        File [] files = dir.listFiles();  
	        for (File file : files) {  
	            System.out.println(file.getName());  
	        }  
	        CompositeFileComparator cfc = new CompositeFileComparator(  
	                DirectoryFileComparator.DIRECTORY_COMPARATOR,  
	                NameFileComparator.NAME_COMPARATOR);  
	        cfc.sort(files);  
	        System.out.println("*****after sort*****");  
	        for (File file : files) {  
	            System.out.println(file.getName());  
	        }  
	    }
}
