package com.XZYHOrderingFood.back.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * 时间比较对象
 * @author dell
 *
 */
@Data
@Builder
public class DayCompare {
	  private int year;
	  private int month;
	  private int day;
}
