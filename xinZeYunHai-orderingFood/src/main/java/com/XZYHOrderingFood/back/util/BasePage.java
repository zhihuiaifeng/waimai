package com.XZYHOrderingFood.back.util;

import java.io.Serializable;
import java.util.List;

import com.github.pagehelper.Page;

import lombok.Data;

/** 
 * 分页基类
 * @author dell
 *
 */

@Data
public class BasePage<T> implements Serializable {
	
  private Integer pageNo;//当前第一页
  private Integer pageSize;//每页条数
  private Long total; //总条数
  private Integer pages; //总页数
  private List<T> list; //结果集
  private int size; 
  
  public BasePage(List<T> list) {
      if (list instanceof Page) {
          Page<T> page = (Page<T>) list;
          this.pageNo = page.getPageNum();
          this.pageSize = page.getPageSize();
          this.total = page.getTotal();
          this.pages = page.getPages();
          this.list = page;
          this.size = page.size();
      }
  }
 
}
