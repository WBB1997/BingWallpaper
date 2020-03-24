package Util.Qihu;

public class QihuPicBean {
    private String id;
    private String resolution;
    private String utag;
    private String url;
    private String img_2560_1440;
    private String img_1440_900;
    private String img_1024_768;
    private String img_800_600;

    public static void main(String[] args) {
        QihuPicBean  q = new QihuPicBean();
        q.setUrl("http://p18.qhimg.com/bdr/__85/t016d400453090f19cc.jpg");
        System.out.println(q.getImg_2560_1440());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getUtag() {
        return utag;
    }

    public void setUtag(String utag) {
        this.utag = utag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        img_2560_1440 = url.replace("__85", "2560_1440_85");
        img_800_600 = url.replace("__85", "800_600_85");
    }

    public String getImg_2560_1440() {
        return img_2560_1440;
    }

    public String getImg_1440_900() {
        return img_1440_900;
    }

    public void setImg_1440_900(String img_1440_900) {
        this.img_1440_900 = img_1440_900;
    }

    public String getImg_1024_768() {
        return img_1024_768;
    }

    public void setImg_1024_768(String img_1024_768) {
        this.img_1024_768 = img_1024_768;
    }

    public String getImg_800_600() {
        return img_800_600;
    }

    public void setImg_800_600(String img_800_600) {
        this.img_800_600 = img_800_600;
    }

    private String getUrl(String url, String resolution){
        return url.replace("__85", resolution + "_85");
    }
}
