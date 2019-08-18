package com.XZYHOrderingFood.back.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.XZYHOrderingFood.back.caffeine.CacheConfig;
import com.XZYHOrderingFood.back.dao.QrCodeMapper;
import com.XZYHOrderingFood.back.dao.TSceneMapper;
import com.XZYHOrderingFood.back.dao.TUserMapper;
import com.XZYHOrderingFood.back.exception.CustomException;
import com.XZYHOrderingFood.back.pojo.QrCode;
import com.XZYHOrderingFood.back.pojo.TScene;
import com.XZYHOrderingFood.back.pojo.TUser;
import com.XZYHOrderingFood.back.util.*;
import com.XZYHOrderingFood.back.util.BasePage;
import com.XZYHOrderingFood.back.util.Config;
import com.XZYHOrderingFood.back.util.DateUtil;
import com.XZYHOrderingFood.back.util.PublicDictUtil;
import com.XZYHOrderingFood.back.util.QrCodeUtils;
import com.XZYHOrderingFood.back.util.RandomUtils;
import com.XZYHOrderingFood.back.util.Result;
import com.XZYHOrderingFood.back.util.fileUtil.GetCurrentUser;
import com.github.pagehelper.PageHelper;
import com.github.vioao.wechat.Const;
import com.github.vioao.wechat.bean.response.qrcode.QrCodeResponse;
import com.github.vioao.wechat.utils.Params;
import com.github.vioao.wechat.utils.client.HttpUtil;


import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class QrCodeService {

	@Autowired
	private QrCodeMapper qrCodeMapper;

	@Autowired
	private CacheConfig cacheConfig;

	@Autowired
	private TUserMapper userMapper;

	@Autowired
	private TSceneMapper tSceneMapper;

	@Autowired
	private Config config;

	public static final int TABLE_STATUS_N = 0;// 未启用

	public static final int TABLE_STATUS_Y = 1;// 启用

	private static final String QR_CODE_CREATE = Const.Uri.API_URI + "/cgi-bin/qrcode/create";

	/**
	 * @description: 查询二维码信息
	 * @author: zpy
	 * @date: 2019-07-26
	 * @params:
	 * @return:
	 */
	public Result getQrCodeService(QrCode qrCode, HttpServletRequest request) {
		if (qrCode.getPageNo() != null && qrCode.getPageSize() != null) {
			PageHelper.startPage(qrCode.getPageNo(), qrCode.getPageSize());
		} else {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "分页未获取到数据", null);
		}
		// 从session缓存中获取用户
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
		}
		List<QrCode> qrCodes = null;
		try {
			// 查询数据
			qrCodes = qrCodeMapper.selectByShopId(currentUser.getId());
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询二维码信息失败", null);
		}
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询二维码信息成功", new BasePage<QrCode>(qrCodes));
	}

	/**
	 * @description: 更新二维码表是否启用的状态
	 * @author: zpy
	 * @date: 2019-07-26
	 * @params:
	 * @return:
	 */
	public Result updateQrCodeStatusById(QrCode qrCode, HttpServletRequest request) {
		// 从session缓存中获取用户
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
		}
		// 初始化mybatis查询的条件
		Map map = new HashMap<String, Object>();
		// 主键和状态 用户的主键
		map.put("qrCodeId", qrCode.getId());
		map.put("tableStatus", qrCode.getTableStatus());
		map.put("shopId", currentUser.getId());
		map.put("isShops", currentUser.getIsShops());
		// 更新后的结果
		Integer result = null;
		try {
			result = qrCodeMapper.updateQrCodeStatusById(map);
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "更新二维码表失败", null);
		}
		if (result == 1) {
			return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "更新二维码表成功", null);
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "更新二维码表失败", null);
	}

	public Result add(HttpServletRequest request) {
		// 获取当前用户信息
		TUser tUser = GetCurrentUser.getCurrentUser(request);
		if (tUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "请登录后再添加桌数", null);
		}
		/*
		 * String acctoken = (String)
		 * cacheConfig.cache().getIfPresent(PublicDictUtil.ACCESS_TOKEN); if
		 * (StringUtils.isBlank(acctoken)) { return
		 * Result.resultData(PublicDictUtil.ERROR_VALUE, "获取acctoken失败", null); }
		 */

		/*
		 * //公司获取当前使用到scene值 String restSceneId = qrCodeMapper.getRestSceneId(); int
		 * tempSc = Integer.valueOf(restSceneId); tempSc+=1;
		 */
		List<TScene> sceneList = tSceneMapper.list();
		TScene tScene = sceneList.get(0);
		Integer sceneIdCount = tScene.getSceneIdCount();
		if ((sceneIdCount - 1) < 0) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "抱歉系统的总餐桌数已经用完", null);
		}
		// 查询当前用户的最大桌号
		Integer tableNum = qrCodeMapper.getMaxTableNumByTUserId(tUser.getId());
		if (tableNum == null) {
			tableNum = 1;
		} else {

			// 判断桌号是否超出预设桌号
			if ((tUser.getTableMaxNumber() - 1) < 0) {
				return Result.resultData(PublicDictUtil.ERROR_VALUE, "您已经超出最大预设桌数", null);
			}


			tableNum += 1;

		}

		// 用户id + 桌号 = scene_str
		String sceneStr = tUser.getId() + "scene_str" + tableNum;
		//String sceneStr =  "https://w.url.cn/s/AIrfL1V";
		/*
		 * QrCodeResponse qrBean = createFinal(acctoken, sceneStr); if
		 * (StringUtils.isBlank(qrBean.getTicket())) { return
		 * Result.resultData(PublicDictUtil.ERROR_VALUE, "获取ticket失败", null); }
		 */

		// 保存数据库
		QrCode qrCode = new QrCode();
		qrCode.setCreateTime(new Date());
		qrCode.setTuserId(tUser.getId());

		qrCode.setTableNumber(tableNum);
		qrCode.setTableStatus(TABLE_STATUS_Y);
		qrCode.setIsDelete(1);
		//String contentUrl = config.getAppPageUrl()+"?tUserId="+tUser.getId()+"&tableNum="+tableNum;
		String content = config.getAppPageUrl() + "#/?tUserId="+tUser.getId()+"&tableNum=" +tableNum;
		
		String newFileName = System.currentTimeMillis()+RandomUtils.getRandomStr(4, 1);
		String fileName = createQr(content,null, newFileName);
		if(StringUtils.isBlank(fileName)) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "二维码生产失败", null);
		}
		qrCode.setQrImgName(fileName);

		/*
		 * qrCode.setTicket(qrBean.getTicket()); qrCode.setQrImgName(qrBean.getUrl());
		 */
		qrCode.setId(Sid.nextShort());
		// 生产短链接
		/*
		 * String longUrl = config.getAppPageUrl() + "?" + "tUserId=" + tUser.getId() +
		 * "&" + "tableNum=" + tableNum; ShortUrlResponse sr =
		 * ShortUrlApi.toShortUrl(acctoken, longUrl);
		 */
		/*
		 * if (!(sr.getErrcode() == 0 && "ok".equals(sr.getErrmsg()))) { return
		 * Result.resultData(PublicDictUtil.ERROR_VALUE, "短连接生成失败", null); }
		 */
		//qrCode.setShortUrl(qrBean.getUrl());
		int count = qrCodeMapper.insertSelective(qrCode);
		if (count > 0) {
			// 将当前用户的总左数做减一操作
			TUser tempUser = new TUser();
			tempUser.setId(tUser.getId());
			int tableMaxNum = tUser.getTableMaxNumber();
			tableMaxNum = tableMaxNum - 1;
			if (tableMaxNum < 0) {
				throw new CustomException(PublicDictUtil.ERROR_VALUE, "桌数已经达到最大值,不能再添加");
			}
			tempUser.setTableMaxNumber(tableMaxNum);
			count = userMapper.updateByPrimaryKeySelective(tempUser);
			if (count > 0) {
				// 公司最大桌数做减一操作 scene_id -1
				// 公司当前使用到scene值做加一操作 rest_scene + 1
				TScene tempScene = new TScene();
				tempScene.setId(tScene.getId());
				tempScene.setSceneIdCount(tScene.getSceneIdCount() - 1);
				tempScene.setRestSceneCount(tScene.getRestSceneCount() + 1);
				count = tSceneMapper.updateByPrimaryKeySelective(tempScene);
				if (count > 0) {
					return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "操作成功", null);
				}

			}
		}
		return Result.resultData(PublicDictUtil.ERROR_VALUE, "操作失败", null);
	}






public String uploadFile( )  {

	 String path = "";
		try {

		//图片文件
		path = config.getUploadPath() + config.getQrImg();

		File file2 = new File(path); // 创建本地文件流对象
		if (!file2.exists()) {
			file2.mkdirs();
		}
		log.info("创建文件夹"+path);
			/*
			 * File targetFile = new File(path, newFileName);
			 *
			 * file.transferTo(targetFile);
			 */

	} catch (Exception e) {
		e.printStackTrace();

	}

	return path;
}



	/**
	 * 生成二维码
	 * content 内容链接地址
	 * fileName 文件名称
	 */
	public String createQr(String urlParam, String logoPath, String fileName) {
		String imgName = "";
		try {
			imgName = QrCodeUtils.encode(urlParam,null, uploadFile(), fileName, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			imgName = "";
			e.printStackTrace();

		}
		return imgName;
	}



	/**
	 * 创建持久二维码.
	 *
	 * @param accessToken 公众号的全局唯一接口调用凭据
	 * @param sceneId     场景值ID 1-100000
	 */
	public QrCodeResponse createFinal(String accessToken, String sceneStr) {
		return create(accessToken, null, "QR_LIMIT_STR_SCENE", sceneStr);
	}

	/**
	 * 创建二维码. 每次创建二维码ticket需要提供一个开发者自行设定的参数（scene_id） 分别介绍临时二维码和永久二维码的创建二维码ticket过程.
	 *
	 * @param accessToken   公众号的全局唯一接口调用凭据
	 * @param expireSeconds 该二维码有效时间，以秒为单位。最大不超过2592000（即30天）。
	 * @param actionName    二维码类型，QR_SCENE为临时的整型参数值，QR_LIMIT_SCENE为永久的整型参数值，
	 * @param sceneId       场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
	 */
	private QrCodeResponse create(String accessToken, Integer expireSeconds, String actionName, String sceneStr) {
		String data = String.format(
				"{" + (expireSeconds == null ? "%1$s" : "\"expire_seconds\": %1$s, ")
						+ "\"action_name\": \"%2$s\", \"action_info\": {\"scene\": {\"scene_str\": %3$s}}}",
				expireSeconds == null ? "" : expireSeconds, actionName, sceneStr);
		Map<String, String> params = Params.create("access_token", accessToken).get();
		return HttpUtil.postJsonBean(QR_CODE_CREATE, params, data, QrCodeResponse.class);
	}

		public void uploadQR(String id, HttpServletResponse response) throws Exception {
			if (StringUtils.isBlank(id)) {
				throw new CustomException(PublicDictUtil.ERROR_VALUE, "二维码id不能为空");
			}
			QrCode qrCode = qrCodeMapper.selectByPrimaryKey(id);
			String subName = qrCode.getQrImgName().substring(qrCode.getQrImgName().lastIndexOf("."));
			String fileName =  DateUtil.getStrYm(new Date()) + " "+qrCode.getTableNumber()+"号桌";
			response.setHeader("content-type", "application/octet-stream"); // 二进制流
			response.setContentType("application/octet-stream"); // 设置文件名称
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("GBK"), StandardCharsets.ISO_8859_1)+subName);
			response.setContentType("application/x-msdownload");
			String path = config.getServerUrl()+config.getQrImg()+qrCode.getQrImgName();
	        // 构造URL "http://xinzeyunhai.com/vueFoodPC/123.png"
	        URL url = new URL(path);
	        // 打开连接
	       URLConnection con = url.openConnection();
	        //设置请求超时为5s
	        con.setConnectTimeout(10*1000);
	        // 输入流
	        @Cleanup InputStream is = con.getInputStream();
			  //@Cleanup FileInputStream in=new FileInputStream("E:\\donglong\\tomcat\\apache-tomcat-8.5.9\\webapps\\xzyhImg\\images\\qrImg\\"+qrCode.getQrImgName());

	        @Cleanup DataInputStream dataInputStream = new DataInputStream(is);

	        @Cleanup OutputStream outputStream = response.getOutputStream();
	        @Cleanup ByteArrayOutputStream output = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int length;

			while ((length = dataInputStream.read(buffer)) > 0) {
				// output.write(buffer, 0, length);
				outputStream.write(buffer, 0, length);
				outputStream.flush();
			}

	    }

	public void uploadWXQR(String id, HttpServletResponse response) throws IOException {
		if (StringUtils.isBlank(id)) {
			throw new CustomException(PublicDictUtil.ERROR_VALUE, "二维码id不能为空");
		}
		QrCode qrCode = qrCodeMapper.selectByPrimaryKey(id);
		// 下载微信二维码
		// MediaFile mediaFile = QrCodeApi.download(qrCode.getTicket());
		/*
		 * if (StringUtils.isNotBlank(mediaFile.getError())) { throw new
		 * CustomException(PublicDictUtil.ERROR_VALUE, "下载失败"); }
		 */
		String fileName = qrCode.getTableNumber() + "号桌" + "_" + DateUtil.getStrYm(new Date()) + ".jpg";
		response.setHeader("content-type", "application/octet-stream"); // 二进制流
		response.setContentType("application/octet-stream"); // 设置文件名称
		response.setHeader("Content-Disposition",
				"attachment;filename=" + new String(fileName.getBytes("GBK"), StandardCharsets.ISO_8859_1));
		response.setContentType("application/x-msdownload");
		URL url = null;
		OutputStream outputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			// 请求的路径
			String qrUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + qrCode.getTicket();
			url = new URL(qrUrl);
			DataInputStream dataInputStream = new DataInputStream(url.openStream());

			// FileOutputStream fileOutputStream = new FileOutputStream(new
			// File("d:\\test.jpg"));// 下载的位置及文件名

			outputStream = response.getOutputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int length;

			while ((length = dataInputStream.read(buffer)) > 0) {
				// output.write(buffer, 0, length);
				outputStream.write(buffer, 0, length);
				outputStream.flush();
			}
			// fileOutputStream.write(output.toByteArray());
			outputStream.close();
			dataInputStream.close();
			// fileOutputStream.close();
		} catch (MalformedURLException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}

		// BufferedInputStream bis = mediaFile.getFileStream();

		/*
		 * byte[] buff = new byte[1024]; BufferedInputStream bufferedInputStream = null;
		 * OutputStream outputStream = null; try { outputStream =
		 * response.getOutputStream();
		 * 
		 * int num = mediaFile.getFileStream().read(buff); while (num != -1) {
		 * outputStream.write(buff, 0, num); outputStream.flush(); num =
		 * bufferedInputStream.read(buff); }
		 * 
		 * } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
		 * finally { if (bufferedInputStream != null) { bufferedInputStream.close(); } }
		 */

	}

	/**
	 * @description: 查询一共的桌号
	 * @author: zpy
	 * @date: 2019-08-03
	 * @params:
	 * @return:
	 */
	public Result selectQrCodeNumById(HttpServletRequest request) {
		TUser currentUser = GetCurrentUser.getCurrentUser(request);
		if (currentUser == null) {
			return Result.resultData(PublicDictUtil.TOKEN_INVAALID, "缓存获取用户失败", null);
		}
		// 从缓存的用户中获取用户id， 通过id查询所有桌号
		// 初始化返回字段
		List<QrCode> qrCodes = null;
		try {
			qrCodes = qrCodeMapper.selectByShopId(currentUser.getId());
		} catch (Exception e) {
			return Result.resultData(PublicDictUtil.ERROR_VALUE, "查询该商家桌号失败", null);
		}
		return Result.resultData(PublicDictUtil.SUCCESS_VALUE, "查询该商家桌号成功", qrCodes);
	}

	public int updateTableStatusByUserId(QrCode qrCode) {
		return qrCodeMapper.updateTableStatusByUserId(qrCode);
	}



	/*
	 * public static InputStream encode(String content, String logoPath, boolean
	 * needCompress) throws Exception { BufferedImage image =
	 * QrCodeUtil.createImage(content, logoPath, needCompress);
	 * ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	 * ImageIO.write(image, FORMAT, outputStream); //String streamEncoded =
	 * Base64Util.encode(outputStream.toByteArray()); InputStream inputStream = new
	 * ByteArrayInputStream(outputStream.toByteArray()); return inputStream; }
	 */


}
