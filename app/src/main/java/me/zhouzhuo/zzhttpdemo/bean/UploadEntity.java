package me.zhouzhuo.zzhttpdemo.bean;

/**
 * Created by ZZ on 2016/8/30.
 */
public class UploadEntity {

    /**
     * code : successfully
     * data : {"msg":"上传成功！"}
     */

    private String code;
    /**
     * msg : 上传成功！
     */

    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "msg='" + msg + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "code='" + code + '\'' +
                ", data=" + data +
                '}';
    }
}
