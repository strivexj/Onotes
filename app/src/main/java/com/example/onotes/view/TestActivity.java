package com.example.onotes.view;

import android.annotation.TargetApi;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;

import com.example.onotes.R;
import com.example.onotes.utils.LogUtil;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        upload();
       /* Explode explode=new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);*/
    }

    public File getAlbumStorageDir() {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "cwj");
        if (!file.mkdirs()) {
            LogUtil.e(this, "Directory not created");
        }
        return file;
    }
    public void upload(){
           String path= "/storage/emulated/0/Pictures/cwj/avatar.jpg";
       // final BmobFile bmobFile = new BmobFile(new File(getAlbumStorageDir(),"avatar.jpg"));
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                   LogUtil.d("upload succeed " ,bmobFile.getFileUrl());
                }else{
                    LogUtil.d("upload failed " , e.getMessage());
                }

            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }

}
