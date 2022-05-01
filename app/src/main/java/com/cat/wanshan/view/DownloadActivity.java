package com.cat.wanshan.view;


import android.content.ContentValues;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.cat.wanshan.R;
import com.cc.baselibrary.base.BaseActivity;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.OnDownloadListener;

import java.io.File;
import java.util.List;

public class DownloadActivity extends BaseActivity implements OnDownloadListener {
    public static final String TAG = "Download_Activity";
    private final String DOWNLOAD_URL = "http://10.0.2.2:8080/download";

    private ProgressBar mProgressBar;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    protected void initView() {
        mProgressBar = findViewById(R.id.progress_bar_view);
        mProgressBar.setMax(100);
        TextView btnClick = findViewById(R.id.download_btn);
        btnClick.setOnClickListener(view -> checkPermission());
    }


    /**
     * 检查是否有读写权限，有则调用下载
     */
    public void checkPermission() {
        if (!PermissionUtils.isGranted(PermissionConstants.STORAGE)) {
            PermissionUtils.permissionGroup(PermissionConstants.STORAGE).callback(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(@NonNull List<String> granted) {
                    Toast.makeText(mContext, "已经允许", Toast.LENGTH_SHORT).show();
                    downloadFile();
                }

                @Override
                public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                    Toast.makeText(mContext, "拒绝了", Toast.LENGTH_SHORT).show();
                }
            }).request();
        } else {
            downloadFile();
        }
    }

    /**
     * 请求下载接口
     */
    private void downloadFile() {
        //下载得文件名
        File downloadFile = new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "wanshan"), "456.jpg");
        HttpRequest httpRequest = HttpRequest.build(mContext, DOWNLOAD_URL);
        httpRequest.addParameter("filename", "123.jpg");
        httpRequest.doDownload(downloadFile, this);

//停止下载：
        //ttpRequest.stop();
    }


    @Override
    public void onDownloadSuccess(File file) {
        Log.d(TAG, "文件已下载完成:" + file.getAbsolutePath());
        //更新xangce
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    @Override
    public void onDownloading(int progress) {
        mProgressBar.setProgress(progress);
        Log.d(TAG, "progress:" + progress);
    }

    @Override
    public void onDownloadFailed(Exception e) {
        Log.d(TAG, "download fail:" + e);
    }
}
