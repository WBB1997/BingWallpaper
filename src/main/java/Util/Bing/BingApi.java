package Util.Bing;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BingApi {
    // imagesize
    private static final List<String> imageSize = new ArrayList<>(Arrays.asList("1920x1080", "1366x768", "1280x768", "1080x1920", "1024x768", "800x600",
            "800x480", "768x1366", "768x1280", "720x1280", "640x480", "480x800", "400x240", "320x240", "240x320"));
    private static String defaultSize = "1920x1080";

    public static void main(String[] args) {
        for (BingPicBean picBean : getAllImages())
            System.out.println(picBean);
    }

    public static StoryBean getStory(String date) {
        String coverstory = "https://cn.bing.com/cnhp/coverstory?d=/";
        String url = coverstory + date;
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
        return JSONObject.parseObject(jsonString, StoryBean.class);
    }

    public static List<BingPicBean> getAllImages() {
        return getImages(1, 16);
    }

    public static List<BingPicBean> getImages(int start, int end) {
        if (start > end)
            throw new IllegalArgumentException("start greater than end");
        if (start > 16 || start < 1)
            throw new IllegalArgumentException("start must more than 1 or less then 11");
        if (end > 16 || end < 1)
            throw new IllegalArgumentException("end must more than 1 or less then 11");

        List<BingPicBean> res = new ArrayList<>();
        String format = "js";
        int[] idx = {-1, 8};
        int n = 8;
        String jsonString;
        for (int i = 0; i < 2; ++i) {
            try {
                // 网络连接
                String head = "http://cn.bing.com/HPImageArchive.aspx?";
                URL urlObject = new URL(head + "format=" + format + "&" + "idx=" + idx[i] + "&" + "n=" + n);
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
            if (jsonString != null) {
                JSONArray array = JSONObject.parseObject(jsonString).getJSONArray("images");
                for (Object o : array) {
                    BingPicBean picBean = JSONObject.parseObject(((JSONObject) o).toJSONString(), BingPicBean.class);
                    String url = picBean.getUrl();
                    url = "http://s.cn.bing.net" + url.substring(0, url.lastIndexOf('_')) + "_" + defaultSize + ".jpg";
                    picBean.setUrl(url);
                    res.add(picBean);
                }
            }
        }
        return res.subList(start - 1, end);
    }

    public static void getAllImages(ImageStreamListener listener) {
        getImages(1, 16, listener);
    }

    public static void getImages(int start, int end, ImageStreamListener listener) {
        if (start > end)
            throw new IllegalArgumentException("start greater than end");
        if (start > 16 || start < 1)
            throw new IllegalArgumentException("start must more than 1 or less then 11");
        if (end > 16 || end < 1)
            throw new IllegalArgumentException("end must more than 1 or less then 11");

        String format = "js";
        Map<Integer, Integer> idx = new HashMap();
        idx.put(-1, 7);
        idx.put(8, 8);
        String jsonString;
        for (Map.Entry<Integer, Integer> m : idx.entrySet()) {
            try {
                // 网络连接
                String head = "http://cn.bing.com/HPImageArchive.aspx?";
                URL urlObject = new URL(head + "format=" + format + "&" + "idx=" + m.getKey() + "&" + "n=" + m.getValue());
                URLConnection uc = urlObject.openConnection();
                // 数据流连接
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), StandardCharsets.UTF_8));
                // 读取数据
                jsonString = in.readLine();
                // 关闭流
                in.close();
                if (jsonString != null) {
                    JSONArray array = JSONObject.parseObject(jsonString).getJSONArray("images");
                    for (Object o : array) {
                        BingPicBean picBean = JSONObject.parseObject(((JSONObject) o).toJSONString(), BingPicBean.class);
                        String url = picBean.getUrl();
                        url = "http://s.cn.bing.net" + url.substring(0, url.lastIndexOf('_')) + "_" + defaultSize + ".jpg";
                        picBean.setUrl(url);
                        listener.produce(picBean);
                    }
                }
            } catch (IOException e) {
                listener.onError(e);
            }
        }
        listener.onComplete();
    }

    public static String getSize() {
        return defaultSize;
    }

    public static void setSize(String size) {
        defaultSize = size;
    }

    public interface ImageStreamListener {
        void produce(Object obj);

        void onComplete();

        void onError(Throwable e);
    }
}
