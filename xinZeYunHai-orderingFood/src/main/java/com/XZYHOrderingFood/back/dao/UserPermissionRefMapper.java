package com.XZYHOrderingFood.back.dao;

import org.apache.ibatis.annotations.Mapper;

import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.pojo.UserPermissionRef;
@Mapper
public interface UserPermissionRefMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserPermissionRef record);

    int insertSelective(UserPermissionRef record);

    UserPermissionRef selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserPermissionRef record);

    int updateByPrimaryKey(UserPermissionRef record);

    /**
     * 批量插入数据
     * @param user
     * @return
     */
	int batchInsert(TUser user);

	/**
	 * 通过用户id 删除对应数据
	 * @param id
	 * @return
	 */
	int delByUserId(String id);
}