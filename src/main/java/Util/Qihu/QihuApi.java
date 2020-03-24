package Util.Qihu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QihuApi {

    public static void main(String[] args) {
//        System.out.println(new QihuApi().getImages(6,1,10).toString());
    }

    public static void getImages(int category , int start, int count , ObservableList<QihuPicBean> res) {
        if (start < 0)
            throw new IllegalArgumentException("start must more than -1");
        if(res == null)
            res = FXCollections.observableArrayList();
        String jsonString = getJson("http://wallpaper.apc.360.cn/index.php?c=WallPaper&a=getAppsByCategory&cid= " + category + "&start= " + start + "&count=" + count + "&from=360chrome");
        if (jsonString != null) {
            JSONArray array = JSONObject.parseObject(jsonString).getJSONArray("data");
            for (Object o : array) {
                QihuPicBean qihuPicBean = JSONObject.parseObject(((JSONObject) o).toJSONString(), QihuPicBean.class);
                res.add(qihuPicBean);
            }
        }
    }

    private static String getJson(String url){
        String jsonString;
        try {
            // 网络连接
            URL urlObject = new URL(url);
            URLConnection uc = urlObject.openConnection();
            // 数据流连接
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), StandardCharsets.UTF_8));
            // 读取数据
            jsonString = in.readLine();
            // 关闭流
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public static List<Category> getCategories() {
        String jsonString =  getJson("http://cdn.apc.360.cn/index.php?c=WallPaper&a=getAllCategoriesV2&from=360chrome");
        if(jsonString != null) {
            JSONArray array = JSONObject.parseObject(jsonString).getJSONArray("data");
            List<Category> res = new ArrayList<>(array.size());
            for (Object o : array)
                res.add(JSONObject.parseObject(o.toString(), Category.class));
            return res;
        }
        return null;
    }
}
