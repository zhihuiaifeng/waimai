package com.XZYHOrderingFood.back.util;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ParamBean {
	private Integer pageNo;//当前第一页
	private Integer pageSize;//每页条数
}
