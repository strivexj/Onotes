package com.example.onotes.view;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onotes.R;

import com.example.onotes.utils.LogUtil;

import com.example.onotes.utils.ScreenShot;
import com.example.onotes.utils.ToastUtil;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;


import org.apache.http.client.methods.HttpPost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileActivity extends AppCompatActivity implements View.OnClickListener {



    private static final int CAMERA_CODE = 1;
    private static final int GALLERY_CODE = 2;
    private static final int CROP_CODE = 3;
    private ImageView mImageView;

    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;


    private Uri imageUri;
    private File outputImage1;

    private final File outputImage2 = new File(Environment.
            getExternalStorageDirectory(), "tempImage2.jpg");

    private ImageView imageview;

    private Button takephote;
    private Button choosephoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        initView();


        if (ContextCompat.checkSelfPermission(FileActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(FileActivity.this,
                    new String[]{Manifest.permission.CAMERA},1);

        }
    }

    private void initView() {

        mImageView = (ImageView) findViewById(R.id.imageview);


        takephote = (Button) findViewById(R.id.takephoto);
        choosephoto = (Button) findViewById(R.id.choosephoto);

        takephote.setOnClickListener(this);
        choosephoto.setOnClickListener(this);

      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   Document doc= Jsoup.connect("http://cn.bing.com/").get();
                    LogUtil.d("bing",doc.toString());
                    Elements element=doc.select("backgroud");

                }catch (IOException e){
                    e.printStackTrace();
                }

            }

        }).start();*/
    }
    public final static String FORMAT_DATE_TIME_SECOND = "yyyy-MM-dd HH:mm:ss";
  //  String timeStamp = new SimpleDateFormat(FORMAT_DATE_TIME_SECOND).format(new Date());



    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takephoto:

                //sharePhoto();

                //拍照选择
              //  chooseFromCamera();
                getAlbumStorageDir("cwj");
                break;
            case R.id.choosephoto:
                String timeStamp = new SimpleDateFormat(FORMAT_DATE_TIME_SECOND).format(new Date());
                ToastUtil.showToast(timeStamp);
                //从相册选取
                chooseFromGallery();
             //   bottonsheet();
                break;
        }
    }

    private void sharePhoto() {
        try{
            File image=createImageFile();
            String path=  image.getAbsolutePath();
         //   ScreenShot.getScrollViewBitmap()
        }catch (IOException e){
            e.printStackTrace();
        }


        Intent intent  = new Intent(Intent.ACTION_SEND);
        File file = new File("sds");
       // intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        Bundle bundle = new Bundle();
        //把Bitmap对象放到bundle中
        bundle.putParcelable("bitmap", ScreenShot.takeScreenShot(this));

        intent.putExtra(Intent.EXTRA_STREAM,bundle );

        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent, "Share screen shot");
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }



    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                //...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }





    private void bottonsheet(){
    BottomSheetDialog dialog = new BottomSheetDialog(this);
    View view =this.getLayoutInflater().inflate(R.layout.edit_setting_sheet, null);
    dialog.setContentView(view);
    dialog.show();
}









    /**
     * 拍照选择图片
     */
    private void chooseFromCamera() {
        //构建隐式Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //调用系统相机
        startActivityForResult(intent, CAMERA_CODE);
    }
    /**
     * 从相册选择图片
     */
    private void chooseFromGallery() {
        //构建一个内容选择的Intent
       // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      //  Intent intent = new Intent(Intent.ACTION_PICK, null);
      //  intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      //          "image/*");
        //设置选择类型为图片类型
       // intent.setType("image/*");
        //打开图片选择

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_CODE);
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            LogUtil.e(this, "Directory not created");
        }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CAMERA_CODE:

                //用户点击了取消
                if(data == null){
                    return;
                }else{
                    Bundle extras = data.getExtras();
                    if (extras != null){
                        //获得拍的照片
                        Bitmap bm = extras.getParcelable("data");
                        //将Bitmap转化为uri
                        Uri uri = saveBitmap(bm, "temp");
                        //启动图像裁剪
                        startImageZoom(uri);
                    }
                }
                break;
            case GALLERY_CODE:
                if (data == null){
                    return;
                }else{
                    //用户从图库选择图片后会返回所选图片的Uri
                    Uri uri;
                    //获取到用户所选图片的Uri
                    //uri = data.getData();
                    //返回的Uri为content类型的Uri,不能进行复制等操作,需要转换为文件Uri
                    uri = convertUri(data.getData());
                    startImageZoom(uri);

                }
                break;
            case CROP_CODE:


                if (data == null){
                    return;
                }else{
                    Bundle extras = data.getExtras();
                    if (extras != null){
                        //获取到裁剪后的图像
                        Bitmap bm = extras.getParcelable("data");
                        mImageView.setImageBitmap(bm);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 将content类型的Uri转化为文件类型的Uri
     * @param uri
     * @return
     */
    private Uri convertUri(Uri uri){
        InputStream is;
        try {
            //Uri ----> InputStream
            is = getContentResolver().openInputStream(uri);
            //InputStream ----> Bitmap
            Bitmap bm = BitmapFactory.decodeStream(is);
            //关闭流
            is.close();
            return saveBitmap(bm, "temp");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * 将Bitmap写入SD卡中的一个文件中,并返回写入文件的Uri
     * @param bm
     * @param dirPath
     * @return
     */



    private Uri saveBitmap(Bitmap bm, String dirPath) {
        //新建文件夹用于存放裁剪后的图片
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/" + dirPath);
        if (!tmpDir.exists()){
            tmpDir.mkdir();
        }

        //新建文件存储裁剪后的图片
        File img = new File(tmpDir.getAbsolutePath() + "/.png");
        try {
            //打开文件输出流
            FileOutputStream fos = new FileOutputStream(img);
            //将bitmap压缩后写入输出流(参数依次为图片格式、图片质量和输出流)
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            //刷新输出流
            fos.flush();
            //关闭输出流
            fos.close();
            //返回File类型的Uri
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    /**
     * 通过Uri传递图像信息以供裁剪
     * @param uri
     */
    private void startImageZoom(Uri uri){
        //构建隐式Intent来启动裁剪程序
        Intent intent = new Intent("com.android.camera.action.CROP");
        //设置数据uri和类型为图片类型
        intent.setDataAndType(uri, "image/*");
        //显示View为可裁剪的
        intent.putExtra("crop", true);
        //裁剪的宽高的比例为1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //输出图片的宽高均为150
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        //裁剪之后的数据是通过Intent返回
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_CODE);
    }
    private void colorpicker() {


       ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(R.color.light_blue_500)

                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                //.wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        //Toast.makeText(FileActivity.this, Integer.toHexString(selectedColor)).show();

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        //changeBackgroundColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

}

   /*  private void choosephote() {
        File outputImage = new File(Environment.
                getExternalStorageDirectory(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }



       imageUri = Uri.fromFile(outputImage);
        // Intent intent2 = new Intent(Intent.ACTION_PICK);
        Intent intent2=new Intent("android.intent.action.GET_CONTENT");
        intent2.setType("image/*");//相片类型
        intent2.putExtra("crop", true);
        intent2.putExtra("scale", true);
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent2, 3);
    }*/

   /* private void takephote() {
        outputImage1 = new File(Environment.
                getExternalStorageDirectory(), "tempImage1.jpg");
        try {
            if (outputImage1.exists()) {
                imageUri = Uri.fromFile(outputImage1);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream
                            (getContentResolver()
                                    .openInputStream(imageUri));
                   // Glide.with(this).load(bitmap).into(imageview);
                  imageview.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                outputImage1.delete();
            }
            outputImage1.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(outputImage1);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序
=======

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takephote:
                outputImage1 = new File(Environment.
                        getExternalStorageDirectory(), "tempImage1.jpg");
                try {
                    if (outputImage1.exists()) {
                        imageUri = Uri.fromFile(outputImage1);
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream
                                    (getContentResolver()
                                            .openInputStream(imageUri));
                           // Glide.with(this).load(bitmap).into(imageview);
                          imageview.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        outputImage1.delete();
                    }
                    outputImage1.createNewFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage1);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序
                break;
            case R.id.choosephoto:
                File outputImage = new File(Environment.
                        getExternalStorageDirectory(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
               // Intent intent2 = new Intent(Intent.ACTION_PICK);
                Intent intent2=new Intent("android.intent.action.GET_CONTENT");
                intent2.setType("image/*");//相片类型
                intent2.putExtra("crop", true);
                intent2.putExtra("scale", true);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent2, 3);
                break;
        }
>>>>>>> dcd88902b7652eb8125fbb4d781cfb9fc6923534
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:

                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream
                                (getContentResolver()
                                        .openInputStream(imageUri));
                       imageview.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        imageview.setImageBitmap(bit);
                       // Glide.with(this).load(bit).into(imageview);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
<<<<<<< HEAD
}*/
// 监听表更新

// 监听表删除
// rtd.subTableDelete("Person");
// 监听行更新
// rtd.subRowUpdate(tableName, objectId);
// 监听行删除
// rtd.subRowDelete(tableName, objectId);




       /* String picPath = "sdcard/Onotes.apk";
        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Toast.makeText(FileActivity.this, "上传文件成功:"+ bmobFile.getFileUrl()).show();
                }else{
                    Toast.makeText(FileActivity.this, "上传文件失败:"+ e.getMessage()).show();
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });*/
//sendMsg("cwj","qunimade");


   /* private void initchat() {
        final BmobRealTimeData rtd = new BmobRealTimeData();
        rtd.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                if(BmobRealTimeData.ACTION_UPDATETABLE.equals(data.optString("action"))){
                    JSONObject contentdata = data.optJSONObject("data");
                   // messages.add(new Chat(data.optString("name"), data.optString("content")));
                   // myAdapter.notifyDataSetChanged();
                    Log.d("bmob",contentdata.optString("name")+" "+contentdata.optString("content"));
                }

            }

            @Override
            public void onConnectCompleted(Exception ex) {
                Log.d("bmob", "连接成功:"+rtd.isConnected());
                if(rtd.isConnected())
                rtd.subTableUpdate("Chat");
            }
        });
    }

    private void sendMsg(String name, String msg){
        Chat chat = new Chat();
        chat.setName(name);
        chat.setContent(msg);
        chat.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(FileActivity.this, "succeed").show();
                }
            }
        });

    }
*/

