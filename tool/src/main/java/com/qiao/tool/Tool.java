package com.qiao.tool;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Tool {

    private static OkHttpClient mOkHttpClient;

    public static void main(String[] args) {
        mOkHttpClient = new OkHttpClient();
        search("");
    }

    private static void search(String id) {
        Request request = new Request.Builder()
                .url("https://ztcwx.myscrm.cn/index.php?r=choose-room-activity/ajax-room-list&token=pozdis1553145056&activityId=5951&chooseRoomStatus=-1&block=A1%E5%8F%B7%E6%A5%BC&areaFullName=&unit=2&houseTypeId=-1")//请求接口。如果需要传参拼接到接口后面。
                .addHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8")
                .addHeader("Accept-Encoding", "br, gzip, deflate")
                .addHeader("Cookie", "last_env=g2; PHPSESSID=ll03p4gh6olvalvcvs0jsgr9h2; acw_tc=781bad2915581793649154837e08eb394466b64b44d88c1b187369de971bef; aliyungf_tc=AQAAAMsmbXCb8g0AuSb6cjptAiTOJInY")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/604.4.7 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 MicroMessenger/6.5.2.501 NetType/WIFI WindowsWechat Chrome/39.0.2171.95 Safari/537.36 MicroMessenger/6.5.2.501 NetType/WIFI WindowsWechat")
                .addHeader("Referer", "https://ztcwx.myscrm.cn/page/room_list.html?activityId=5951&token=pozdis1553145056")
                .addHeader("Accept-Language", "zh-cn")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build(); //创建Request 对象
        Response response;
        try {
            response = mOkHttpClient.newCall(request).execute();//得到Response 对象
            if (response.isSuccessful()) {
                System.out.println(response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
