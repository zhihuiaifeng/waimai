package com.XZYHOrderingFood.back.util;

import java.util.Random;

public final  class RandomUtils {

	private RandomUtils(){}
	
	/**
	 * 取得随机字符串
	 * 
	 * @param len
	 *            长度
	 * @param type
	 *            类型 1:数字+字母混合 2:字母 3:数字
	 * @return
	 */
	public static String getRandomStr(int len, int type) {
		String str = "";
		Random random = new Random();
		for (int i = 0; i < len; i++) {
			if (type == 1) {
				String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
				if (("char").equals(charOrNum)) {
					str += (char) (choice + random.nextInt(26));
				} else if (("num").equals(charOrNum)) {
					str += String.valueOf(random.nextInt(10));
				}
			} else if (type == 2) {
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
				str += (char) (choice + random.nextInt(26));
			} else if (type == 3) {
				str += String.valueOf(random.nextInt(10));
			} else {
				str = "";
			}
		}
		return str;
	}
	
}
