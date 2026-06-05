package cn.wildfirechat.pojos.mesh;

import com.google.gson.Gson;

public class MeshRestResult<T> {
    private static Gson gson = new Gson();
    public enum MeshRestCode {
        SUCCESS(0, "success"),
        ERROR_INVALID_MOBILE(1, "无效的电话号码"),
        ERROR_SEND_SMS_OVER_FREQUENCY(3, "请求验证码太频繁"),
        ERROR_SERVER_ERROR(4, "服务器异常"),
        ERROR_CODE_EXPIRED(5, "验证码已过期"),
        ERROR_CODE_INCORRECT(6, "验证码或密码错误"),
        ERROR_SERVER_CONFIG_ERROR(7, "服务器配置错误"),
        ERROR_SESSION_EXPIRED(8, "会话不存在或已过期"),
        ERROR_SESSION_NOT_VERIFIED(9, "会话没有验证"),
        ERROR_SESSION_NOT_SCANED(10, "会话没有被扫码"),
        ERROR_SERVER_NOT_IMPLEMENT(11, "功能没有实现"),
        ERROR_GROUP_ANNOUNCEMENT_NOT_EXIST(12, "群公告不存在"),
        ERROR_NOT_LOGIN(13, "没有登录"),
        ERROR_NO_RIGHT(14, "没有权限"),
        ERROR_INVALID_PARAMETER(15, "无效参数"),
        ERROR_NOT_EXIST(16, "对象不存在"),
        ERROR_USER_NAME_ALREADY_EXIST(17, "用户名已经存在"),
        ERROR_SESSION_CANCELED(18, "会话已经取消"),
        ERROR_PASSWORD_INCORRECT(19, "密码错误"),
        ERROR_FAILURE_TOO_MUCH_TIMES(20, "密码错误次数太多，请等5分钟再试试"),
        ERROR_USER_FORBIDDEN(21, "用户被封禁");
        public int code;
        public String msg;

        MeshRestCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }
    private int remote_im_code;
    private int remote_mesh_code;
    private int local_mesh_code;
    private String error_message;
    private T result;

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public static MeshRestResult ok() {
        return new MeshRestResult();
    }

    public static MeshRestResult ok(Object object) {
        return new MeshRestResult(object);
    }

    public static MeshRestResult remoteIMError(int code, String message) {
        MeshRestResult r = new MeshRestResult();
        r.remote_im_code = code;
        r.error_message = message;
        return r;
    }

    public static MeshRestResult remoteMeshError(int code, String message) {
        MeshRestResult r = new MeshRestResult();
        r.remote_mesh_code = code;
        r.error_message = message;
        return r;
    }

    public MeshRestResult addRemoteMeshError(int code, String message) {
        MeshRestResult r = new MeshRestResult();
        remote_mesh_code = code;
        error_message = message;
        return this;
    }

    public static MeshRestResult localMeshError(int code, String message) {
        MeshRestResult r = new MeshRestResult();
        r.local_mesh_code = code;
        r.error_message = message;
        return r;
    }

    public MeshRestResult addLocalMeshError(int code, String message) {
        local_mesh_code = code;
        error_message = message;
        return this;
    }


    public MeshRestResult() {

    }

    public MeshRestResult(T object) {
        result = object;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public int getRemote_im_code() {
        return remote_im_code;
    }

    public void setRemote_im_code(int remote_im_code) {
        this.remote_im_code = remote_im_code;
    }

    public int getRemote_mesh_code() {
        return remote_mesh_code;
    }

    public void setRemote_mesh_code(int remote_mesh_code) {
        this.remote_mesh_code = remote_mesh_code;
    }

    public int getLocal_mesh_code() {
        return local_mesh_code;
    }

    public void setLocal_mesh_code(int local_mesh_code) {
        this.local_mesh_code = local_mesh_code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}
