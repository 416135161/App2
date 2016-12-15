package com.sjning.app2.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/15.
 */

public class GetNetMessageBean {
    /**
     * result : [{"phone":"15198216330","contents":[{"tag":"12","date":"2016-12-06 11:12:24"},{"tag":"-","date":"2016-12-06 09:39:43"},{"tag":"-","date":"2016-12-06 09:38:32"},{"tag":"-","date":"2016-12-06 09:38:18"},{"tag":"-","date":"2016-12-05 16:24:43"},{"tag":"-","date":"2016-12-05 14:45:30"},{"tag":"-","date":"2016-12-05 14:43:53"},{"tag":"-","date":"2016-12-05 09:52:59"}],"comdate":1481784502000,"recivephone":"15198216330"}]
     * rowCount : 8
     * success : true
     */

    private int rowCount;
    private boolean success;
    /**
     * phone : 15198216330
     * contents : [{"tag":"12","date":"2016-12-06 11:12:24"},{"tag":"-","date":"2016-12-06 09:39:43"},{"tag":"-","date":"2016-12-06 09:38:32"},{"tag":"-","date":"2016-12-06 09:38:18"},{"tag":"-","date":"2016-12-05 16:24:43"},{"tag":"-","date":"2016-12-05 14:45:30"},{"tag":"-","date":"2016-12-05 14:43:53"},{"tag":"-","date":"2016-12-05 09:52:59"}]
     * comdate : 1481784502000
     * recivephone : 15198216330
     */

    private List<ResultBean> result;

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private String phone;
        private long comdate;
        private String recivephone;
        /**
         * tag : 12
         * date : 2016-12-06 11:12:24
         */

        private List<ContentsBean> contents;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public long getComdate() {
            return comdate;
        }

        public void setComdate(long comdate) {
            this.comdate = comdate;
        }

        public String getRecivephone() {
            return recivephone;
        }

        public void setRecivephone(String recivephone) {
            this.recivephone = recivephone;
        }

        public List<ContentsBean> getContents() {
            return contents;
        }

        public void setContents(List<ContentsBean> contents) {
            this.contents = contents;
        }

        public static class ContentsBean {
            private String tag;
            private String date;

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }
        }
    }
}
