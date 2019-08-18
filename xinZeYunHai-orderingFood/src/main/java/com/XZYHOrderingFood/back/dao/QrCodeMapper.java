package com.XZYHOrderingFood.back.dao;

import com.XZYHOrderingFood.back.pojo.QrCode;
import com.XZYHOrderingFood.back.pojo.TUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Mapper
public interface QrCodeMapper {
    int deleteByPrimaryKey(String id);

    int insert(QrCode record);

    int insertSelective(QrCode record);

    QrCode selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(QrCode record);

    int updateByPrimaryKey(QrCode record);

    /**
     * @description: 查询二维码相关信息
     * @author: zpy
     * @date: 2019-07-26
     */
    List<QrCode> selectByShopId(String id);
    /**
     * @description: 更新二维码状态表
     * @author: zpy
     * @date: 2019-07-26
     */
    int updateQrCodeStatusById(@Param("param") Map<String, Object> map);

    /**
     * 通过用户id查询 最大桌号
     * @param id
     * @return
     */
	Integer getMaxTableNumByTUserId(@Param("tUserId") String tUserId);

	/**
	 * 更新二维码状态
	 * @param qrCode
	 * @return
	 */
	int updateTableStatusByUserId(QrCode qrCode);
}