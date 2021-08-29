package com.sindrax.ProfessionDiscovery_client.homeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.unity3d.player.UnityPlayer;

import java.io.File;

public class MainActivity extends Activity {
    private Activity _unityActivity;          //Activity parameters
    private static final int REQUEST_CODE_INSTALL = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void InstallApk(Context context, String apkPath) {

        UnityPlayer.UnitySendMessage("SindraxProfessionalExperience.AndroidUpdate.AndroidUpdate", "AndroidCallBack", "Android下载地址：" + apkPath);
        try {
            File apkFile = new File(apkPath);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(apkUri);
            } else {
                intent.setData(Uri.fromFile(apkFile));
            }
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            context.startActivity(intent);
        }
        catch (Exception e){
            UnityPlayer.UnitySendMessage("SindraxProfessionalExperience.AndroidUpdate.AndroidUpdate", "AndroidCallBack", "自动安装应用报错：" + e.getMessage());
        }

    }
    //==========================================================================================
    //get content by reflect

     Activity getActivity() {
        if (null == _unityActivity) {
            try {
                Class<?> classType = Class.forName("com.unity3d.player.UnityPlayer");
                _unityActivity = (Activity) classType.getDeclaredField("currentActivity").get(classType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return _unityActivity;
    }

    public boolean InstallApk(String apkName) {
        if (apkName == null) {
            return false;
        }
        File apkFile = new File(apkName);
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);

        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT < 24) {
            intent.setData(Uri.fromFile(apkFile));
        } else { // Android N，下面的 provider 要和 mainfest 一致

            // 应用 ID 过去直接关联到代码的软件包名称；
            // 所以，有些 Android API 会在其方法名称和参数名称中使用“package name”一词，
            // 但这实际上是您的应用 ID。例如，Context.getPackageName() 方法会返回您的应用 ID。
            // 无论何时都不需要在应用代码以外分享代码的真实软件包名称。
                Uri apkUri = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                        getActivity().getPackageName().concat(".fileprovider"), apkFile);
                // grant READ permission for this content Uri
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(apkUri);
        }
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);

        try {
            getActivity().startActivityForResult(intent, REQUEST_CODE_INSTALL);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
