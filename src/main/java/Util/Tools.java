package Util;

import com.alibaba.simpleimage.ImageFormat;
import com.alibaba.simpleimage.ImageWrapper;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.analyze.search.cluster.impl.Point;
import com.alibaba.simpleimage.render.WatermarkParameter;
import com.alibaba.simpleimage.render.WriteParameter;
import com.alibaba.simpleimage.util.ImageDrawHelper;
import com.alibaba.simpleimage.util.ImageReadHelper;
import com.alibaba.simpleimage.util.ImageWriteHelper;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.win32.StdCallLibrary;
import org.apache.commons.io.IOUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    public static void changeBackground(String srcUrl, String waterMark) {
        try {
            Pattern pattern = Pattern.compile("[\\s\\\\/:*?\"<>|]");
            String[] tmp = srcUrl.trim().split("/");
            Matcher matcher = pattern.matcher(tmp[Math.max(tmp.length - 1, 0)]);
            String filename = "wallpaper/" + matcher.replaceAll("");
            File file = new File(filename);
            File fileParent = file.getParentFile();
            boolean flag = true;
            if (!fileParent.exists())
                flag = fileParent.mkdir();
            if (flag) {
                save(srcUrl, file, waterMark.replace("©","Copyright"));
                change(file.getAbsolutePath());
            } else
                System.err.println("文件夹创建失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String url = "http://s.cn.bing.net/th?id=OHR.MetamorphicRocks_ZH-CN9753251368_1920x1080.jpg&rf=LaDigue_1920x1080.jpg";
        File file = new File("C:\\Users\\Wu\\Desktop\\1.jpg");
        String note = "这是测试水印这是测试水印这是测试水印这是测试水印";
        save(url, file, note);
    }

    // 保存图片
    private static void save(String srcUrl, File saveFile, String note) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            //获取输入流
            URL url = new URL(srcUrl);
            HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            inputStream = httpUrl.getInputStream();
            // 加上水印
            BufferedImage bufferedImage = addWaterMark(httpUrl.getInputStream(), note);
            // 输出
            // 获取文件输出流
            outputStream = new FileOutputStream(saveFile);
            // 获取图片格式
            String prefix = saveFile.getName().substring(saveFile.getName().lastIndexOf(".") + 1);
            // 输出图片
            ImageWriteHelper.write(new ImageWrapper(bufferedImage), outputStream, ImageFormat.getImageFormat(prefix), new WriteParameter());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream); // 图片文件输入输出流必须记得关闭
            IOUtils.closeQuietly(outputStream);
        }
    }

    // 添加水印 第二个参数为水印的内容，为字符串
    public static BufferedImage addWaterMark(InputStream in, String note) throws SimpleImageException {
        // 获取照片类
        ImageWrapper imageWrapper = ImageReadHelper.read(in);
        // 获取水印照片包装类
        ImageWrapper waterWrapper = new ImageWrapper(getWaterMask(note));
        // 计算水印放在哪个位置
        Point p = calculate(imageWrapper.getWidth(), imageWrapper.getHeight(), waterWrapper.getWidth(), waterWrapper.getHeight());
        // 水印属性类
        WatermarkParameter param = new WatermarkParameter(waterWrapper, 1f, (int) p.getX(), (int) p.getY());
        // // 将水印写到图片上并返回图片Buff
        return ImageDrawHelper.drawWatermark(imageWrapper.getAsBufferedImage(), param);
    }

    // 将文字生成为图片
    private static BufferedImage getWaterMask(String text) {
        Color fontColor = new Color(203, 203, 203);
        Color bgColor = new Color(0, 0, 0, 178);
        Font font = new Font("楷体", Font.PLAIN, 20);
        int[] num = getStringWidthAndHeight(text, font);
        int width = num[1]; //字符串宽度
        int height = num[0]; //字符串高度
        float alpha = 0.2f;

        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //得到画笔对象
        Graphics2D g2d = buffImg.createGraphics();
        // 添加背景色
//        g2d.setColor(bgColor);
        g2d.setColor(bgColor);
        g2d.fillRoundRect(0, 0, width, height, 7,7);
        // ----------  增加下面的代码使得背景透明  -----------------
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.dispose();
        // ----------  背景透明代码结束  -----------------
        // 添加文字
        g2d = buffImg.createGraphics();
        // 设置对线段的锯齿状边缘处理
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 设置颜色
        g2d.setColor(fontColor);
        // 设置 Font
        g2d.setFont(font);
        //设置透明度:1.0f为透明度 ，值从0-1.0，依次变得不透明
//        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.drawString(text, 0, height - 5);//居中显示
        //释放资源
        g2d.dispose();
//        System.out.println("添加水印文字完成!");
        return buffImg;
    }

    // 获取文字的高和宽
    private static int[] getStringWidthAndHeight(String str, Font font) {
        JLabel label = new JLabel();
        label.setFont(font);
        FontMetrics metrics;
        int textH;
        int textW;
        label.setText(str);
        metrics = label.getFontMetrics(font);
        textH = metrics.getHeight();//字符串的高,   只和字体有关
        textW = metrics.stringWidth(label.getText());//字符串的宽
        int[] num = new int[2];
        num[0] = textH;
        num[1] = textW;
        return num;
    }

    // 计算水印放的位置
    private static Point calculate(int parentWidth, int parentHeight, int width, int height) {
        int x = (parentWidth - (int)(parentWidth * 0.05)) - width ;
        int y = (parentHeight - (int)(parentHeight * 0.1));
        return new Point(x, y);
    }

    // 设置为桌面
    private interface MyUser32 extends StdCallLibrary {

        MyUser32 INSTANCE = Native.loadLibrary("user32", MyUser32.class);

        void SystemParametersInfoA(int uiAction, int uiParam, String fnm, int fWinIni);
    }

    // 设置为桌面
    private static void change(String img) {

        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "Wallpaper", img);
        //WallpaperStyle = 10 (Fill), 6 (Fit), 2 (Stretch), 0 (Tile), 0 (Center)
        //For windows XP, change to 0
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "WallpaperStyle", "10"); //fill
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "TileWallpaper", "0");   // no tiling

        // refresh the desktop using User32.SystemParametersInfo(), so avoiding an OS reboot
        int SPI_SETDESKWALLPAPER = 0x14;
        int SPIF_UPDATEINIFILE = 0x01;
        int SPIF_SENDWININICHANGE = 0x02;

        // User32.System
        MyUser32.INSTANCE.SystemParametersInfoA(SPI_SETDESKWALLPAPER, 0,
                img, SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE);
    }
}
