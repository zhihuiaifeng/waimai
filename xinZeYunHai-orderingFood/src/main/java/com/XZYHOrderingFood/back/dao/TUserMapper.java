package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.TUser;

import java.util.List;

import com.github.bingoohuang.utils.lang.Str;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.XZYHOrderingFood.back.pojo.TUser;

@Mapper
public interface TUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(TUser record);

    int insertSelective(TUser record);

    TUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(TUser record);

    int updateByPrimaryKey(TUser record);

    List<TUser> findAll();
    /**
     * 通过名称查询用户
     * @param name
     * @return
     */
	TUser findUserByName(String name);
	/**
	 * 通过用户信息查询当前用户
	 * @param user
	 * @return
	 */
	//TUser findByUserCheckAccount(TUser tuser);


    /**
     * 查询用户是否存在
     * @param user
     * @return
     */
	TUser findUserIsExis(TUser user);

	/**
	 * 通过参数查询用户
	 * @param user
	 * @return
	 */
	TUser findUserByParam(TUser user);

    int countShopNumber();

    /**
     * @description: 统计即将到期商户
     * @author: zpy
     * @date: 2019-07-22 11:17
     * @params: No such property: _1 for class: Script1
     * @return:
     */
    int countExpireShop();

    /**
     * @description: 查询新增加商户数量 用于echarts显示
     * @author: zpy
     * @date: 2019-07-23
     * @params:
     * @return:
     */
    List<TUser> getNewShopCharts(@Param("param")HashMap map);


    /**
     * @description: 查询所有用户信息，不添加任何判断的查询
     * @author: zpy
     * @date: 2019-07-23
     * @params:
     * @return:
     */
    List<TUser> getShopInfo(@Param("param") Map map);

    /**
     * @description: 查询后台账户
     * @author: zpy
     * @date: 2019-08-02
     * @params:
     * @return:
     */
    List<TUser> selectBackEndUser();

    /**
     * @description: 通过id集合查询多个商户
     * @author: zpy
     * @date: 2019-08-03
     */
    List<TUser> selectByIdList(List<String> list);
    /**
     * 查询出全部  >=起始时间 和 <= 截止时间 的商户 status = 1启用    is_active != 0
     * @return
     */
	List<TUser> findNoexpireTimeUser(Date date);

	/**
	 * 更新用户到期状态
	 * @param tempUser
	 * @return
	 */
	int updateUserExpireTime(TUser tempUser);

    /**
     * 通过手机号查询是否已经存在
     * @param
     * @return
     */
    Integer findUserPhoneIsExis(String phone);

    /**
     * 修改密码
     * @param user
     * @return
     */
    int updateByUsername(TUser user);

    /**
     * 查询用户名是否重复
     * @param
     * @return
     */
    Integer selectUsernameIsExist(String username);

    /**
     * 查询用户是否存在
     * @param user
     * @return
     */
    TUser findUsernameIsExist(TUser user);
}