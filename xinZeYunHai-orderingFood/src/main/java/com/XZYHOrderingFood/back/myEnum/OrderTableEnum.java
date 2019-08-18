package com.XZYHOrderingFood.back.myEnum;

/**
 * @description: 订单枚举类
 * @author: zpy
 * @create: 2019-07-29 11:10
 **/
public enum OrderTableEnum {
    CANCEL_ORDER(0,"订单已取消"),
    PENDING_ORDER(1,"订单待支付"),
    PAYMENTED_ORDER(2,"订单已支付"),
    IS_CASHOUT(1,"已提现"),
    NO_CASHOUT(0,"未提现"),
    IS_DELETE(0,"删除"),
    NO_DELETE(1,"未删除")
    ;

    private Integer code;
    private String msg;

    OrderTableEnum(Integer code,String msg) {
        this.code = code;
        this.msg = msg;
    }

    // 普通方法
    public static String getName(Integer code) {
        for (OrderTableEnum u : OrderTableEnum.values()) {
            if ( u.getCode() == code) {
                return u.msg;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
