package com.zhy.fitandroid7;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 小结：
 * 1.疑问：如果这个应用有不同的拍照场景，但是拍照存储的路径不同，是否可以指定多个URI路径呢？
 *   解答：需要在file_paths.xml配置同等目录多条，如下所示：
 *   <external-path
         name="external"
         path="2018_image1" />

      <external-path
         name="external"
         path="2018_image2" />
 *
 * 2.在7.0系统上使用路径时，需要手动创建出来并不能自己自动创建出来，否则报错！
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_TAKE_PHOTO = 0x110;
    private static final int REQ_PERMISSION_CODE_SDCARD = 0X111;
    private static final int REQ_PERMISSION_CODE_TAKE_PHOTO = 0X112;

    private String mCurrentPhotoPath;
    private ImageView mIvPhoto;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIvPhoto = (ImageView) findViewById(R.id.id_iv);

    }

    public void installApk(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQ_PERMISSION_CODE_SDCARD);

        } else {
            installApk();
        }

    }


    private void installApk() {
        // 需要自己修改安装包路径
        File file = new File(Environment.getExternalStorageDirectory(),
                "app-debug.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(this,
                intent, "application/vnd.android.package-archive", file, true);

        startActivity(intent);
    }


    public void takePhotoNoCompress(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQ_PERMISSION_CODE_TAKE_PHOTO);

        } else {
            takePhotoNoCompress();
        }
    }


    private void takePhotoNoCompress() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
                    .format(new Date()) + ".png";

            File parentFile = new File(Environment.getExternalStorageDirectory() + "/2018_image1/");
            if (!parentFile.exists())
                parentFile.mkdirs();

            File file = new File(parentFile, filename);
            mCurrentPhotoPath = file.getAbsolutePath();

            Uri fileUri = FileProvider7.getUriForFile(this, file);
            System.out.println("Uri=" + fileUri);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION_CODE_SDCARD) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                installApk();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (requestCode == REQ_PERMISSION_CODE_TAKE_PHOTO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoNoCompress();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            mIvPhoto.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
        }
        // else tip?

    }

    public void takePhotoSecondPathClick(View view) {
        startActivity(new Intent(this,SecondTakePhotoActivity.class));
    }
}
