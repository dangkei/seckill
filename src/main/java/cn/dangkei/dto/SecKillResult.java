package cn.dangkei.dto;

/**
 * 所有ajax封装返回类型
 * 封装JSON结果
 * @param <T>
 */
public class SecKillResult<T> {
    private boolean success;
    private T data;
    private String error;

    public SecKillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public SecKillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
