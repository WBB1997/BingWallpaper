package Util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.win32.StdCallLibrary;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tools {
    public static void changeBackground(String destUrl) {
        String[] tmp = destUrl.trim().split("/");
        File file = new File("wallpaper/" + tmp[tmp.length - 1]);
        File fileParent = file.getParentFile();
        boolean flag = true;
        if (!fileParent.exists())
            flag = fileParent.mkdir();
        if (flag) {
            saveToFile(destUrl, file);
            change(file.getAbsolutePath());
        } else
            System.err.println("文件夹创建失败");
    }

    private static void saveToFile(String destUrl, File file) {
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url;
        int BUFFER_SIZE = 1024;
        byte[] buf = new byte[BUFFER_SIZE];
        int size;
        try {
            url = new URL(destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            fos = new FileOutputStream(file);
            while ((size = bis.read(buf)) != -1) {
                fos.write(buf, 0, size);
            }
            fos.flush();
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (bis != null)
                    bis.close();
                if (httpUrl != null)
                    httpUrl.disconnect();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private interface MyUser32 extends StdCallLibrary {

        MyUser32 INSTANCE = Native.loadLibrary("user32", MyUser32.class);
        void SystemParametersInfoA(int uiAction, int uiParam, String fnm, int fWinIni);
    }

    private static void change(String img){

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
                img, SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE );
    }
}
