package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.FanControl;
import com.XZYHOrderingFood.back.util.Result;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface FanControlMapper {
    int deleteByPrimaryKey(String id);

    int insert(FanControl record);

    int insertSelective(FanControl record);

    FanControl selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FanControl record);

    int updateByPrimaryKey(FanControl record);

    List<FanControl> getFanControlByDate(@Param("param")HashMap<String, Date> map);

    List<FanControl> selectFanFuzzyQuery(FanControl fanControl);

    /**
     * 通过openId删除用户
     * @param fromUserName
     * @return
     */
	int delByOpenId(String openId);

	/**
	 * 通过openId 取消关注
	 * @param fanControl
	 * @return
	 */
	int updateByOpenId(FanControl fanControl);

	/**
	 * 查询粉丝内容
	 * @param fanControl
	 * @return
	 */
	FanControl findFanControlInfo(FanControl fanControl);
}