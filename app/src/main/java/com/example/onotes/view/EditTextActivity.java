package com.example.onotes.view;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.onotes.R;
import com.example.onotes.datebase.NotesDbHelper;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.KeyboardUtil;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.ScreenShot;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.utils.TimeUtil;
import com.example.onotes.utils.ToastUtil;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditTextActivity extends PickPictureActivity implements View.OnClickListener {

    public static final String REFRESH = "com.example.onotes.refresh";
    private static final int TAKE_PHOTO_REQUEST_CODE = 3; // 拍照返回的 requestCode
    private static final int CHOICE_FROM_ALBUM_REQUEST_CODE = 4; // 相册选取返回的 requestCode
    private static final int CROP_PHOTO_REQUEST_CODE = 5; // 裁剪图片返回的 requestCode
    private static final String onotesPictureStoreDirectory = "Onotes";
    private static final String backGroundName = "bg.jpg";

    private static final int PICK_PICTURE_FOR_BG = 7;
    private static final int PICK_PICTURE_FOR_NOTES = 8;

    private SeekBar linespacing;
    private SeekBar textsize;
    private EditText edittext;
    private String notesid;
    private float linespace = 0;
    private float textsizef = 25;
    private ImageView edit_bg;
    private int bg_color = -1;
    private String time;
    private ImageView bold;
    private ImageView italic;
    private ImageView textcolor;
    private ImageView setting_more;
    private View settingview;
    private Toolbar toolbar;

    private ScrollView scrollView;
    private ImageView note_picture;
    private FrameLayout background_color;

    private StringBuilder notePhotoPath=new StringBuilder();

    private int insertPitureNumber=0;

    public void showTextCopied(View v) {
        //Snackbar.make(imageView, R.string.copied_to_clipboard, Snackbar.LENGTH_SHORT).show();
        Snackbar.make(toolbar, R.string.copied_to_clipboard, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        LogUtil.d("cwj", "oncerate");
        ActivityCollector.addActivity(this);

        initView();

        LogUtil.d("time", System.currentTimeMillis() + "");

        // LogUtil.d("time", getcurrenttime());

        // this work
        // this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Check if no view has focus:

       /*View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }*/

        //InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // keyboard.hideSoftInputFromWindow(getWindow().getAttributes().token, 0);


        //edittext.setTextSize(25);

    }

   /* @Override
    public void startCamera() {
        super.startCamera();
    }*/


    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d("cwj", "edonpause");
    }

    private void initView() {



        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        settingview = LayoutInflater.from(EditTextActivity.this).inflate(R.layout.edit_setting_sheet, null);

        // copy the text content to clipboard
        linespacing = (SeekBar) settingview.findViewById(R.id.linespacing);
        textsize = (SeekBar) settingview.findViewById(R.id.textsize);

        edittext = (EditText) findViewById(R.id.edittext);

        linespacing.setMax(1000);
        textsize.setMax(100);

        linespacing.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                edittext.setLineSpacing((float) progress, 1);

                linespace = (float) progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        textsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                edittext.setTextSize(progress);
                textsizef = (float) progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        String content=load();
        edittext.setText(content);
        edittext.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(edittext.getText().toString())) {
                    edittext.setSelection(edittext.getText().toString().length());
                    LogUtil.d("cwj", "set");
                }
            }
        });

        if(!notePhotoPath.toString().equals("null")){

            //取出图片名字和记号  奇数为名字，偶数为记号（#0...）
            String[] picture=notePhotoPath.toString().split(",");
            String path;
            int i;
            for(i=0;i<picture.length;i=i+2){
                LogUtil.d("fffff",picture.length+" "+notePhotoPath.toString());

                path=getAlbumStorageDir()+"/"+picture[i].replace("null","");

               // path=getAlbumStorageDir().getPath()+"/"+picture[i];

                LogUtil.d("pathcwj",path);
                LogUtil.d("pathcwj",picture[i]);

                LogUtil.d("aaa",notePhotoPath.toString()+"   "+path+"  "+content.indexOf(picture[i+1]));
                if(content.indexOf(picture[i+1])!=-1){
                    insertPhoto(content.indexOf(picture[i+1]),path,picture[i+1].length());
                }
            }
            //0 1 2 3 4 5
            insertPitureNumber=picture[i-1].charAt(1)-'0'+1;


        }


        //BulletSpan span = new BulletSpan(50,Color.RED);
        /*StrikethroughSpan span = new StrikethroughSpan();
        SpannableString spannableString = new SpannableString("This is a span demo~!");
        spannableString.setSpan(span,0,spannableString .length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        edittext.setText(spannableString);*/
       /* ImageSpan span = new ImageSpan(this,R.drawable.back);

        SpannableString spannableString = new SpannableString("his is a span demo~!");
        spannableString.setSpan(span,0,1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        edittext.setText(spannableString);
*/

        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        LogUtil.d("textlistener", "up");
                        ToastUtil.showToast(edittext.getText().toString().substring(edittext.getSelectionStart(), edittext.getSelectionEnd()));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        LogUtil.d("textlistener", "MOVE");
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        LogUtil.d("textlistener", "HOVER_MOVE");
                        break;
                    case MotionEvent.ACTION_SCROLL:
                        LogUtil.d("textlistener", "SCROLL");
                        break;
                }
                return EditTextActivity.super.onTouchEvent(event);
            }
        });

        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogUtil.d("textlistener", "before");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtil.d("textlistener", "onTextChanged");
                /*Spannable inputStr = (Spannable) s;
                if (s.equals("草")) {

                    inputStr.setSpan(new ForegroundColorSpan(Color.BLUE), start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.d("textlistener", "afterTextChanged");
                Spannable inputStr = (Spannable) s;
                //if(s.equals("草")){

               /* int a=edittext.getText().toString().length();
                int b=s.length();
                if(a>0)
                    inputStr.setSpan(new ForegroundColorSpan(Color.BLUE),a-b,a, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            */
            }
        });
        LogUtil.d("cwj", "length:" + edittext.getText().toString().length());
        textsize.setProgress((int) textsizef);
        linespacing.setProgress((int) linespace);




        //设置标题的时间
        if (time != null) {
            actionBar.setTitle("");
            actionBar.setSubtitle(time);
        }


        bold = (ImageView) findViewById(R.id.bold);
        bold.setOnClickListener(this);
        italic = (ImageView) findViewById(R.id.italic);
        italic.setOnClickListener(this);
        textcolor = (ImageView) findViewById(R.id.textcolor);
        textcolor.setOnClickListener(this);
        setting_more = (ImageView) findViewById(R.id.setting_more);
        setting_more.setOnClickListener(this);

        scrollView = (ScrollView) findViewById(R.id.edit_scrollview);

        note_picture = (ImageView) findViewById(R.id.note_picture);
        note_picture.setOnClickListener(this);

        background_color = (FrameLayout) findViewById(R.id.background_color);
        edit_bg = (ImageView) findViewById(R.id.edit_bg);

        loadBackGround();

    }

    private void loadBackGround() {

        //确定是设置背景图片还是背景颜色
        if (SharedPreferenesUtil.isNeedBackGroud()) {
            edit_bg.setVisibility(View.VISIBLE);


                LogUtil.d("type","1");
                Glide.with(this)
                        .load(new File(getAlbumStorageDir(onotesPictureStoreDirectory), backGroundName))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(edit_bg);

        } else if (bg_color != -1) {
            // edit_bg.setBackgroundColor(bg_color);
            background_color.setBackgroundColor(bg_color);
            edit_bg.setVisibility(View.GONE);
        }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.note_picture:

                setType(PICK_PICTURE_FOR_NOTES);
                KeyboardUtil.hideSoftInput(this);
                showListDialog();
                break;
            case R.id.bold:
                //edittext.getPaint().setFakeBoldText(true);
                int position_bold = edittext.getSelectionEnd();
                SpannableString spannableString_B = new SpannableString(edittext.getText());

                StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);

                //StyleSpan styleSpan_B  = new StyleSpan(Typeface.NORMAL);
                spannableString_B.setSpan(styleSpan_B, edittext.getSelectionStart(), edittext.getSelectionEnd(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                edittext.setText(spannableString_B);
                //edittext.setSelection(edittext.getText().toString().length());
                edittext.setSelection(position_bold);

                break;
            case R.id.italic:


                //LogUtil.d("store", Html.toHtml(edittext.getText().getSpans();

                int position_italic = edittext.getSelectionEnd();

                SpannableString spannableString_I = new SpannableString(edittext.getText());
                StyleSpan styleSpan_I = new StyleSpan(Typeface.ITALIC);



                spannableString_I.setSpan(styleSpan_I, edittext.getSelectionStart(), edittext.getSelectionEnd(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                edittext.setText(spannableString_I);
                edittext.setSelection(edittext.getText().toString().length());
                edittext.setSelection(position_italic);



               /* Intent intent  = new Intent(Intent.ACTION_SEND);
                Bundle bundle = new Bundle();
                //把Bitmap对象放到bundle中
                bundle.putParcelable("bitmap", ScreenShot.captureScreen(this));
                intent.putExtra(Intent.EXTRA_STREAM,bundle );
                intent.setType("image/*");
                Intent chooser = Intent.createChooser(intent, "Share screen shot");
                startActivity(chooser);*/
                //startCamera();
                break;
            case R.id.textcolor:


                LogUtil.d("store", "click");

                StyleSpan[] mSpans = edittext.getText().getSpans(0, edittext.length(), StyleSpan.class);
                ImageSpan[] image = edittext.getText().getSpans(0, edittext.length(), ImageSpan.class);

                for (int i = 0; i < mSpans.length; i++) {
                    if (mSpans[i] instanceof StyleSpan) {
                        int start = edittext.getText().getSpanStart(mSpans[i]);
                        int end = edittext.getText().getSpanEnd(mSpans[i]);
                        int flag = edittext.getText().getSpanFlags(mSpans[i]);
                        int id = mSpans[i].getSpanTypeId();
                        LogUtil.d("store", "Found StyleSpan at:\n" +
                                "Start: " + start +
                                "\n End: " + end +
                                "\n Flag(s): " + flag + "ID: " + id);

                    }
                }

                for (int i = 0; i < image.length; i++) {
                    if (image[i] instanceof ImageSpan) {
                        int start = edittext.getText().getSpanStart(image[i]);
                        int end = edittext.getText().getSpanEnd(image[i]);
                        int flag = edittext.getText().getSpanFlags(image[i]);

                        // int flag = edittext.getText().getSpan(image[i]);
                        LogUtil.d("store", "Found ImageSpan at:\n" +
                                "Start: " + start +
                                "\n End: " + end +
                                "\n Flag(s): " + flag + "ID: ");
                    }
                }
                ToastUtil.showToast(edittext.getSelectionEnd()+"");
                // Toast.makeText(this, "aaaa").show();
                break;
            case R.id.setting_more:

              /*  int position_normal=edittext.getSelectionEnd();
               // SpannableString spannableString_N = new SpannableString(edittext.getText());
                SpannableStringBuilder spannableString_N = new SpannableStringBuilder(edittext.getText());

                StyleSpan span=new StyleSpan(Typeface.BOLD);
                //StyleSpan styleSpan_B  = new StyleSpan(Typeface.NORMAL);
               // spannableString_N.setSpan(span, edittext.getSelectionStart(), edittext.getSelectionEnd(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                spannableString_N.removeSpan(span);
                edittext.setText(spannableString_N );*/

               /* StyleSpan styleSpan_N   = new StyleSpan(Typeface.NORMAL);
                //StyleSpan styleSpan_B  = new StyleSpan(Typeface.NORMAL);

                String temp=edittext.getText().subSequence(edittext.getSelectionStart(), edittext.getSelectionEnd()).toString();

                spannableString_N.replace(edittext.getSelectionStart(), edittext.getSelectionEnd(),temp);
                //spannableString_N.clear();
                spannableString_N.setSpan(styleSpan_N , edittext.getSelectionStart(), edittext.getSelectionEnd(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                edittext.setText(spannableString_N );
                //edittext.setSelection(edittext.getText().toString().length());
                edittext.setSelection(position_normal);
               // LogUtil.d("store",temp);*/

                final PopupWindow popupWindow = new PopupWindow(settingview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(settingview, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                KeyboardUtil.hideSoftInput(this);

                settingview.findViewById(R.id.bottom_pick_picture).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setType(PICK_PICTURE_FOR_BG);
                        showListDialog();
                    }
                });
                //   Toast.makeText(this, "aaaa").show();
               /* final BottomSheetDialog dialog = new BottomSheetDialog(this);
                dialog.setContentView(settingview);
                dialog.show();*/
                break;
        }
    }

    /**
     *
     * @param position
     * @param path
     * @param length  0表示手动插入图片 其他值为标记符的长度
     */

    private void insertPhoto(int position,String path,int length) {

        if(length==0){
        edittext.requestFocus();
        String pictureNotation="#"+insertPitureNumber++;
       // LogUtil.d("fff","position "+position+" start "+start.toString()+" end "+end +" length "+edittext.getText().length());
      /*  0  1  2 3
        啊 啊 啊 啊*/
        //ToastUtil.showToast("position :"+position+" start :"+start.toString()+" end :"+end +" length :"+edittext.getText().length());
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        LogUtil.d("path","insert"+path);
        LogUtil.d("bitmap", " height " + bitmap.getHeight() + " width " + bitmap.getWidth());
        Drawable drawable = new BitmapDrawable(this.getResources(), bitmap);
        drawable.setBounds(10, 10, 1420, 1420 * bitmap.getHeight() / bitmap.getWidth());
        SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder(edittext.getText().insert(position,pictureNotation));
        ImageSpan imageSpan = new ImageSpan(drawable);
        spannableStringBuilder.setSpan(imageSpan, position, position + pictureNotation.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int selectend=edittext.getSelectionEnd();
        spannableStringBuilder.append("\n");
        edittext.setText(spannableStringBuilder);
        edittext.setSelection(selectend);
        String []name=path.split("/");
        notePhotoPath.append(name[name.length-1]).append(","+pictureNotation+",");
        }else{
            edittext.requestFocus();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Drawable drawable = new BitmapDrawable(this.getResources(), bitmap);
            drawable.setBounds(10, 10, 1420, 1420 * bitmap.getHeight() / bitmap.getWidth());
            SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder(edittext.getText());
            ImageSpan imageSpan = new ImageSpan(drawable);
            spannableStringBuilder.setSpan(imageSpan, position, position +length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int selectend=edittext.getSelectionEnd();
           // spannableStringBuilder.append("\n");
            edittext.setText(spannableStringBuilder);
            edittext.setSelection(selectend);

        }
       /* SpannableStringBuilder spannableString_I = new SpannableStringBuilder(edittext.getText().subSequence(0,edittext.getSelectionEnd()));

        Bitmap bitmap = BitmapFactory.decodeFile(getPhotoOutputUri().getPath());
        LogUtil.d("path","insert"+getPhotoOutputUri().getPath());
        //Drawable drawable = getResources().getDrawable(R.drawable.back);

        //drawable.setBounds(0, 0, 1420, 1420);

        //ImageSpan imageSpan = new ImageSpan(this,getPhotoOutputUri());
        LogUtil.d("bitmap", " height " + bitmap.getHeight() + " width " + bitmap.getWidth());

        Drawable drawable = new BitmapDrawable(this.getResources(), bitmap);
        drawable.setBounds(10, 10, 1420, 1420 * bitmap.getHeight() / bitmap.getWidth());

        //ImageSpan imageSpan = new ImageSpan(this,bitmap);

        ImageSpan imageSpan = new ImageSpan(drawable);
        ToastUtil.showToast(getPhotoOutputUri().getPath());


        if (edittext.getSelectionStart() == 0) {
            spannableString_I.setSpan(imageSpan, edittext.getSelectionStart(), edittext.getSelectionStart() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString_I.setSpan(imageSpan, edittext.getSelectionEnd() - 1, edittext.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        int selectend=edittext.getSelectionEnd();

        spannableString_I.append("\n");
        edittext.setText(spannableString_I);
        edittext.setSelection(selectend);*/

    }


    public String load() {

        Intent intent = getIntent();
        notesid = intent.getIntExtra("id", -1) + "";
        textsizef = intent.getFloatExtra("textsize", 25);
        linespace = intent.getFloatExtra("linespace", 0);
        edittext.setTextSize(textsizef);
        edittext.setLineSpacing(linespace, 1);
        bg_color = intent.getIntExtra("bgcolor", -1);
        time = intent.getStringExtra("time");

        LogUtil.d("bgcolorload", "" + bg_color);
        LogUtil.d("load", textsizef + "  " + linespace);

        notePhotoPath.append(intent.getStringExtra("insertpicture"));





        return intent.getStringExtra("content");
    }

    public void save(String data) {
        if (!TextUtils.isEmpty(data)) {
            NotesDbHelper notesDbHelper = new NotesDbHelper(this);
            SQLiteDatabase db = notesDbHelper.getWritableDatabase();
            db.delete("Notes", "id=?", new String[]{notesid});

            LogUtil.d("delete", notesid + "");

            ContentValues values = new ContentValues();
            values.put("textsize", textsizef);
            values.put("linespace", linespace);
            values.put("content", data);
            values.put("time", TimeUtil.getYeraMonthHourMinuteSecond());
            values.put("bgcolor", bg_color);
            values.put("insertpicture",notePhotoPath.toString());
            db.insert("Notes", null, values);
            db.close();
            LogUtil.d("bgcolorsave", "" + bg_color);
            LogUtil.d("textsavesize", edittext.getTextSize() + "");
            LogUtil.d("textsavespace", "" + linespace);
        }

        Intent intent = new Intent(REFRESH);
        sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 通过返回码判断是哪个应用返回的数据
            switch (requestCode) {
                // 拍照
                case TAKE_PHOTO_REQUEST_CODE:

                    cropPhoto(getPhotoUri());

                    break;
                // 相册选择
                case CHOICE_FROM_ALBUM_REQUEST_CODE:

                    cropPhoto(data.getData());
                    LogUtil.d("path", data.getData().toString());

                   /*  ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    cropPhoto(originalUri);

                   try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                        savePhotoToSDCard("avatar.jpg",photo);

                        setuserpicture.setImageBitmap(photo);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    break;

                // 裁剪图片
                case CROP_PHOTO_REQUEST_CODE:

                    if(getFile()!=null&&getFile().exists()){
                        getFile().delete();
                    }

                    String path = getPhotoOutputUri().getPath();
                    File file = new File(path);
                    if (file.exists()) {

                        if(getType()==PICK_PICTURE_FOR_BG){
                            SharedPreferenesUtil.setIsNeedBackGroud(true);
                            loadBackGround();

                        }else if(getType()==PICK_PICTURE_FOR_NOTES){
                            insertPhoto(edittext.getSelectionEnd(),path,0);
                        }

                        LogUtil.d("path output", path+"  name"+notePhotoPath.toString());

                        // Bitmap bitmap = BitmapFactory.decodeFile(path);
                        //.setImageBitmap(bitmap);

                    } else {
                        ToastUtil.showToast(getString(R.string.withoutpicture));
                    }
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d("cwj", "edonstart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d("cwj", "edonstop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // save(edittext.getText().toString());
        LogUtil.d("cwj", "edonresume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("cwj", "edondestory");
        save(edittext.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        View v = EditTextActivity.this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }


        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_more) {
/*
            public static void hideSoftInput(Context context, View view) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm == null) return;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }*/

            KeyboardUtil.hideSoftInput(this, edittext);
            //save(edittext.getText().toString());

            // final BottomSheetDialog dialog = new BottomSheetDialog(this);
            // View view =this.getLayoutInflater().inflate(R.layout.edit_actions_sheet, null);

        /*    if (queryIfIsBookmarked()) {
                ((TextView) view.findViewById(R.id.textView)).setText(R.string.action_delete_from_bookmarks);
                ((ImageView) view.findViewById(R.id.imageView))
                        .setColorFilter(this.getResources().getColor(R.color.colorPrimary));
            }

            // add to bookmarks or delete from bookmarks
            view.findViewById(R.id.layout_bookmark).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    addToOrDeleteFromBookmarks();
                }
            });*/

            View view = LayoutInflater.from(EditTextActivity.this).inflate(R.layout.edit_actions_sheet, null);
            // copy the text content to clipboard


            final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            view.findViewById(R.id.layout_copy_text).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //dialog.dismiss();
                    copyText(v);
                    popupWindow.dismiss();
                }
            });

            view.findViewById(R.id.layout_share_pic).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ScreenShot.sharePhoto(EditTextActivity.this, scrollView);
                    popupWindow.dismiss();
                }
            });


            // shareAsText the content as text
            view.findViewById(R.id.layout_share_text).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // dialog.dismiss();
                    shareAsText();
                    popupWindow.dismiss();

                }
            });

            view.findViewById(R.id.color0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background_color.setBackgroundColor( getResources().getColor(R.color.color0));
                    edit_bg.setVisibility(View.GONE);
                    SharedPreferenesUtil.setIsNeedBackGroud(false);
                    bg_color=getResources().getColor(R.color.color0);
                }
            });
            view.findViewById(R.id.color1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background_color.setBackgroundColor( getResources().getColor(R.color.color1));
                    edit_bg.setVisibility(View.GONE);
                    SharedPreferenesUtil.setIsNeedBackGroud(false);
                    bg_color=getResources().getColor(R.color.color1);
                }
            });
            view.findViewById(R.id.color2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background_color.setBackgroundColor( getResources().getColor(R.color.color2));
                    edit_bg.setVisibility(View.GONE);
                    SharedPreferenesUtil.setIsNeedBackGroud(false);
                    bg_color=getResources().getColor(R.color.color2);
                }
            });
            view.findViewById(R.id.color3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background_color.setBackgroundColor( getResources().getColor(R.color.color3));
                    edit_bg.setVisibility(View.GONE);
                    SharedPreferenesUtil.setIsNeedBackGroud(false);
                    bg_color=getResources().getColor(R.color.color3);
                }
            });
            view.findViewById(R.id.color4).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background_color.setBackgroundColor( getResources().getColor(R.color.color4));
                    edit_bg.setVisibility(View.GONE);
                    SharedPreferenesUtil.setIsNeedBackGroud(false);
                    bg_color=getResources().getColor(R.color.color4);
                }
            });



            /*CircleImageView color = (CircleImageView) view.findViewById(R.id.color0);
            color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/


            /*if (bg_color != -1&&SharedPreferenesUtil.isNeedBackGroud()) {
                //color.setBackgroundColor(bg_color);
                // color.setImageResource(bg_color);
                background_color.setBackgroundColor(bg_color);
                edit_bg.setVisibility(View.GONE);
            }*/

            view.findViewById(R.id.more_color).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showColorPicker();
                    popupWindow.dismiss();
                }
            });


            ColorDrawable cd = new ColorDrawable(0x000000);
            popupWindow.setBackgroundDrawable(cd);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.8f;
            getWindow().setAttributes(lp);

            popupWindow.showAtLocation(view, Gravity.TOP, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
           /* dialog.setContentView(view);
            dialog.show();*/
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {


                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1f;
                    getWindow().setAttributes(lp);

                    KeyboardUtil.showSoftInput(edittext);


                }
            });
        }
        return true;
    }


    public void shareAsText() {
        String text = edittext.getText().toString();
        if (TextUtils.isEmpty(text)) {
            // Toast.makeText(this, "").show();
            ToastUtil.showToast(getString(R.string.writing_nothing));
            return;
        }

        try {
            Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
            String shareText = text;

            //shareText = shareText ;

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            this.startActivity(Intent.createChooser(shareIntent, this.getString(R.string.share_to)));

        } catch (ActivityNotFoundException ex) {
            //  showLoadingError();
        }

    }

    private void showColorPicker() {

        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(bg_color)

                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                //.wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .showAlphaSlider(true)
                .showLightnessSlider(true)
                .showColorPreview(true)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        // Toast.makeText(EditTextActivity.this, Integer.to(selectedColor)).show();
                        //Toast.makeText(EditTextActivity.this, Integer.toHexString(selectedColor)).show();

                    }
                })

                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        //changeBackgroundColor(selectedColor);
                       // edit_bg.setBackgroundColor(selectedColor);

                        background_color.setBackgroundColor(selectedColor);
                        edit_bg.setVisibility(View.GONE);
                        SharedPreferenesUtil.setIsNeedBackGroud(false);

                        //   Toast.makeText(EditTextActivity.this, ""+selectedColor).show();

                        bg_color = selectedColor;

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

    public void copyText(View v) {
        String text = edittext.getText().toString();
        if (TextUtils.isEmpty(text)) {
            //Toast.makeText(this, "You haven't written anything!").show();
            ToastUtil.showToast(getString(R.string.writing_nothing));
            return;
        }

        ClipboardManager manager = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", text);

        manager.setPrimaryClip(clipData);
        showTextCopied(v);
    }


}
