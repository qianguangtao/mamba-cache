package com.app.core.mvc.result;


/**
 * 全局响应代码，异常响应代码: 限制区间在 A00000 - A00999
 * 定义 com.support.mvc.entity.base.Result#setCode(Code) 返回编码
 *
 * @author qiangt
 */
public enum Code implements RespCode {
    /** 成功 */
    A00000("成功"),
    A00001("失败"),
    A00002("自定义异常，将会以 exception 内容替换 message 内容，一般用于抛出带动态参数消息，直接在前端弹窗"),
    A00003("会话超时"),
    A00004("参数校验失败"),
    A00005("接口版本号不匹配"),
    A00006("请求地址不存在"),
    A00007("请求缺少必要的参数"),
    A00008("请求参数类型不匹配或JSON格式不符合规范"),
    A00009("请求方式不支持"),
    A00010("请求不存在"),
    A00011("无操作权限"),
    A00012("排序字段不在可选范围"),
    A00013("分页查询，每页显示数据量超过最大值"),
    A00014("上传文件列表为空"),
    A00015("上传文件格式不符合要求"),
    A00016("水平越权"),
    A00017("请勿重复提交"),
    A00018("获取锁超时"),
    A00019("账户已禁用"),
    A00020("用户名或密码错误"),
    A00021("账户已过期"),
    A00022("短信发送失败"),
    A00023("微信小程序接口openid和token必填"),
    A00024("微信小程序token校验失败"),
    A00025("用户不存在"),
    A00026("{{Parameter}}不能为空"),
    A00027("无法解析路径参数，解密后无法解析为Long数据类型，请检查解密过程"),
    A00028("无接口权限"),
    A00029("获取自增序列号失败"),
    A00030("获取锁线程被中断"),
    A00031("获取token失败"),

    ;

    public final String comment;

    Code(final String comment) {
        this.comment = comment;
    }

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public String message() {
        return this.comment;
    }


}
