package com.example.onotes.view;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.onotes.R;
import com.example.onotes.datebase.NotesDbHelper;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.KeyboardUtil;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.ScreenShot;
import com.example.onotes.utils.TimeUtil;
import com.example.onotes.utils.ToastUtil;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditTextActivity extends AppCompatActivity implements View.OnClickListener{

    private SeekBar linespacing;
    private SeekBar textsize;
    private EditText edittext;

    public static final String REFRESH = "com.example.onotes.refresh";

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

        LogUtil.d("time", System.currentTimeMillis() + "");

        LogUtil.d("time", getcurrenttime());

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


        initView();
        //edittext.setTextSize(25);

    }

    private String getcurrenttime() {
        Calendar calendar = Calendar.getInstance();
        String second = calendar.get(Calendar.SECOND) + "";

        String hour = calendar.get(Calendar.HOUR_OF_DAY) + "";
        String minute = calendar.get(Calendar.MINUTE) + "";

        if (second.length() == 1) {
            second = "0" + second;
        }

        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        if (minute.length() == 1) {
            minute = "0" + minute;
        }

        return calendar.get(Calendar.YEAR) + "." +
                calendar.get(Calendar.MONTH) + "."
                + calendar.get(Calendar.DAY_OF_MONTH) + "  " +
                hour + ":" + minute + ":" + second;

    }

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

        edittext.setText(load());
        edittext.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(edittext.getText().toString())) {
                    edittext.setSelection(edittext.getText().toString().length());
                    LogUtil.d("cwj", "set");
                }
            }
        });

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

        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Spannable inputStr = (Spannable) s;
                if (s.equals("草")) {

                    inputStr.setSpan(new ForegroundColorSpan(Color.BLUE), start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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

        edit_bg = (ImageView) findViewById(R.id.edit_bg);

        if (bg_color != -1) {
            edit_bg.setBackgroundColor(bg_color);
        }
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bold:
                edittext.getPaint().setFakeBoldText(true);
                break;
            case R.id.italic:
               /* Intent intent  = new Intent(Intent.ACTION_SEND);
                Bundle bundle = new Bundle();
                //把Bitmap对象放到bundle中
                bundle.putParcelable("bitmap", ScreenShot.captureScreen(this));
                intent.putExtra(Intent.EXTRA_STREAM,bundle );
                intent.setType("image/*");
                Intent chooser = Intent.createChooser(intent, "Share screen shot");
                startActivity(chooser);*/



                break;
            case R.id.textcolor:
                Toast.makeText(this, "aaaa", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting_more:
                final PopupWindow popupWindow = new PopupWindow(settingview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(settingview, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                //   Toast.makeText(this, "aaaa", Toast.LENGTH_SHORT).show();
               /* final BottomSheetDialog dialog = new BottomSheetDialog(this);
                dialog.setContentView(settingview);
                dialog.show();*/
                break;
        }
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
           // values.put("time", getcurrenttime());
            values.put("time", TimeUtil.getYeraMonthHourMinuteSecond());
            values.put("bgcolor", bg_color);
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

            KeyboardUtil.hideSoftInput(this,edittext);
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
                    ScrollView scrollView=(ScrollView)findViewById(R.id.edit_scrollview);

                    ScreenShot.sharePhoto(EditTextActivity.this,scrollView);
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

            CircleImageView color = (CircleImageView) view.findViewById(R.id.color);
            if (bg_color != -1) {
                color.setBackgroundColor(bg_color);
            }

            view.findViewById(R.id.bg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    colorpicker();
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
           // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            ToastUtil.showToast(R.string.writing_nothing,Toast.LENGTH_SHORT);
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

    private void colorpicker() {

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
                        // Toast.makeText(EditTextActivity.this, Integer.to(selectedColor), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(EditTextActivity.this, Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();

                    }
                })

                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        //changeBackgroundColor(selectedColor);
                        edit_bg.setBackgroundColor(selectedColor);
                        //   Toast.makeText(EditTextActivity.this, ""+selectedColor, Toast.LENGTH_SHORT).show();

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
            //Toast.makeText(this, "You haven't written anything!", Toast.LENGTH_SHORT).show();
            ToastUtil.showToast(R.string.writing_nothing,Toast.LENGTH_SHORT);
            return;
        }
        ClipboardManager manager = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", text);

        manager.setPrimaryClip(clipData);
        showTextCopied(v);
    }



   /* public void addToOrDeleteFromBookmarks() {
        String tmpTable = "";
        String tmpId = "";
        switch (type) {
            case TYPE_ZHIHU:
                tmpTable = "Zhihu";
                tmpId = "zhihu_id";
                break;
            case TYPE_GUOKR:
                tmpTable = "Guokr";
                tmpId = "guokr_id";
                break;
            case TYPE_DOUBAN:
                tmpTable = "Douban";
                tmpId = "douban_id";
                break;
            default:
                break;
        }

        if (queryIfIsBookmarked()) {
            // delete
            // update Zhihu set bookmark = 0 where zhihu_id = id
            ContentValues values = new ContentValues();
            values.put("bookmark", 0);
            dbHelper.getWritableDatabase().update(tmpTable, values, tmpId + " = ?", new String[]{String.valueOf(id)});
            values.clear();

            view.showDeletedFromBookmarks();
        } else {
            // add
            // update Zhihu set bookmark = 1 where zhihu_id = id

            ContentValues values = new ContentValues();
            values.put("bookmark", 1);
            dbHelper.getWritableDatabase().update(tmpTable, values, tmpId + " = ?", new String[]{String.valueOf(id)});
            values.clear();

            view.showAddedToBookmarks();
        }
    }

    public boolean queryIfIsBookmarked() {
        if (id == 0 || type == null) {
            view.showLoadingError();
            return false;
        }

        String tempTable = "";
        String tempId = "";

        switch (type) {
            case TYPE_ZHIHU:
                tempTable = "Zhihu";
                tempId = "zhihu_id";
                break;
            case TYPE_GUOKR:
                tempTable = "Guokr";
                tempId = "guokr_id";
                break;
            case TYPE_DOUBAN:
                tempTable = "Douban";
                tempId = "douban_id";
                break;
            default:
                break;
        }

        String sql = "select * from " + tempTable + " where " + tempId + " = ?";
        Cursor cursor = dbHelper.getReadableDatabase()
                .rawQuery(sql, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            do {
                int isBookmarked = cursor.getInt(cursor.getColumnIndex("bookmark"));
                if (isBookmarked == 1) {
                    return true;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return false;
    }*/

}
