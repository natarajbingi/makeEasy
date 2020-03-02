package com.makein.app.Models;

import java.util.List;

public class GetAllSubsRes {

    public boolean error;
    public List<Data> data;
    public String message;

    public class Data {

        public String id;
        public String name;
        public String description;
        public String prod_id;
        public List<String> img_urls;
        public String pur_cost;
        public String sell_cost;
        public String createdby;
        public String created_datetime;
        public String updated_datetime;

    }
}
