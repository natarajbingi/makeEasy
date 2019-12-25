package com.makein.app.Models;

import java.io.Serializable;
import java.util.List;

public class MyReqsResponse implements Serializable {
    public boolean error;
    public String message;
    public List<Data> data;

    public class Data implements Serializable {
        public String id;
        public String invoice_no;
        public String prodid;
        public String prodName;
        public String quantity;
        public String SubProdCatId;
        public String SubProdCatName;
        public List<String> img_urls;
        public String userId;
        public String usrName;
        public String gender;
        public String mobile_no;
        public String sell_cost;
        public String delivery_address;
        public String status;
        public String deli_status;
        public String comment;
        public String userContactNo;
        public String userQuery;
        public String userCompanyName;
        public String userCompanyAddress;
        public String userCompnyEmailAddress;
        public String created_datetime;
    }

}
