package com.example.notice;

public  class Constants {
    private static final String ROOT_URL = "http://192.168.1.3/NoticeApi/v1/Api.php?apicall=";
    public static final String create_post = ROOT_URL + "createpost";
    public static final String get_post = ROOT_URL + "getpost";
    public static final String update_post = ROOT_URL + "updatepost";
    public static final String delete_post = ROOT_URL + "deletepost";
    public static final String login = ROOT_URL + "login";
    public static final String get_post_by_id = ROOT_URL + "getpostbyid";
    public static String Firebase_Api_Url = "https://fcm.googleapis.com/fcm/send";
    public static final String server_key = "key=AAAAcGNVO3c:APA91bHSUTIZJESI4eoi_lPyt_bWCH-VEVroiWB6aMMfPQKIa39YO5ERzQ44fH2bbpyD9QU-GzW6rg1E927pRSPadlIIkXBfTKbc8LF6_aaET86h_5c_sAXdqwX8iLRPv_BOcTxIzyZ8";


    public static  String getFirebase_Api_Url() {
        return Firebase_Api_Url;
    }
}
