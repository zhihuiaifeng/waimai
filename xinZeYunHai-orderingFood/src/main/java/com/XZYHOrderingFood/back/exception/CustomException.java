package com.XZYHOrderingFood.back.exception;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * 自定义异常类
 * @author pc
 *
 *
 */
 
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomException extends RuntimeException  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9088449085676251900L;

	private String msg;
	
	private String code;
	 
}
