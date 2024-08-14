package org.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.Data;

import java.util.function.Supplier;

/**
 * 响应实体类封装
 * @param <T> 响应数据类型
 */
@Data
public class RestBean<T> {
    private int code;
    private T data;
    private String message;

    /**
     * 构造方法私有，通过静态方法获取对象实例
     * @param code 状态码
     * @param data 响应数据
     * @param message 响应信息
     */
    private RestBean(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 成功响应，并携带数据
     * @param data 响应数据
     * @return RestBean对象
     * @param <T> 响应数据类型
     */
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, data, "response success");
    }

    /**
     * 成功响应，不携带数据
     * @return RestBean对象
     */
    public static <T> RestBean<T> success() {
        return success(null);
    }

    /**
     * 失败响应，并携带错误信息
     * @param code 状态码
     * @param message 错误信息
     * @return RestBean对象
     */
    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, null, message);
    }

    /**
     * 参数不合法响应，并携带错误信息
     * @param message 错误信息
     * @return RestBean对象
     * @param <T> 响应数据类型
     */
    public static <T> RestBean<T> argumentNotValid(String message) {
        return failure(400, message);
    }

    /**
     * 未授权访问响应，并携带错误信息
     * @param message 错误信息
     * @return RestBean对象
     * @param <T> 响应数据类型
     */
    public static <T> RestBean<T> unauthorized(String message) {
        return failure(401, message);
    }

    /**
     * 禁止访问响应，并携带错误信息
     * @param message 错误信息
     * @return RestBean对象
     * @param <T> 响应数据类型
     */
    public static <T> RestBean<T> forbidden(String message) {
        return failure(403, message);
    }

    /**
     * 资源未找到响应，并携带错误信息
     * @param message 错误信息
     * @return RestBean对象
     * @param <T> 响应数据类型
     */
    public static <T> RestBean<T> notFound(String message) {
        return failure(404, message);
    }

    /**
     * 请求方法不允许响应，并携带错误信息
     * @param message 错误信息
     * @return RestBean对象
     * @param <T> 响应数据类型
     */
    public static <T> RestBean<T> methodNotAllowed(String message) {
        return failure(405, message);
    }

    /**
     * 服务器错误，并携带错误信息
     * @param message 错误信息
     * @return RestBean对象
     * @param <T>
     */
    public static <T> RestBean<T> internalServerError(String message) {
        return failure(500, message);
    }

    /**
     * 将响应实例转换为JSON字符串
     * @return JSON字符串
     */
    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls); // JSONWriter.Feature.WriteNulls指将null值也写上，不省略
    }

    /**
     * 对于业务返回string信息进行封装成响应实体
     * @param action 执行业务操作
     * @return 响应实体
     */
    public static RestBean<Void> messageHandler(Supplier<String> action) {
        String message = action.get();
        return message == null ? success() : argumentNotValid(message);
    }
}
