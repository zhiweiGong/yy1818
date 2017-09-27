package com.android.EgLauncher;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.tools.SysPorp.UtilShareDB;


public class SetpwdDialogActivity extends Activity{
    private Button mEnter;
    private Button mCancel;

    private Button mNum0;
    private Button mNum1;
    private Button mNum2;
    private Button mNum3;
    private Button mNum4;
    private Button mNum5;
    private Button mNum6;
    private Button mNum7;
    private Button mNum8;
    private Button mNum9;
    private Button mNumx;
    private Button mNumj;
    private TextView mShowKey;
    private  String password;
    private WindowManager.LayoutParams mLayoutParams;
    private View mLayoutView;
    private LayoutInflater mInflater;
    //private View confirmView;
    private View fillView;

    private void setPassword(String mPassword){
     //   City.getCity().setCityName(_cityName);

        Context ctx =SetpwdDialogActivity.this;
        SharedPreferences sp =ctx.getSharedPreferences("PASSWORD", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("Password", mPassword);
        editor.commit();
        Log.e("SetpwdDialogActivity", "set password:" + mPassword);
       // return City.getCity().getCityName();
    }
    private String getPassword(){
        //String cityName = City.getCity().getCityName();
       // if(cityName==null ||cityName==""){
            Context ctx =SetpwdDialogActivity.this;
            SharedPreferences sp =ctx.getSharedPreferences("PASSWORD", MODE_PRIVATE);
            //City.getCity().setCityName(sp.getString("PASSWORD", "123456"));
        //}
        String pwd = sp.getString("Password", "123456");
        Log.e("SetpwdDialogActivity", "get pwd = "+ pwd);
        return pwd;
    }
    private void createFloatWindow(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.x = 0;
        mLayoutParams.y = 0;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        initStateBarView(context);
        // mLayoutView = View.inflate(context, R.layout.dateset_dialog, null);
        windowManager.addView(mLayoutView, mLayoutParams);
    }
    public void initStateBarView(Context context) {
        mLayoutView = View.inflate(context, R.layout.dialog_passwd, null);
        mEnter = (Button) mLayoutView.findViewById(R.id.pwd_enter);
        mCancel = (Button) mLayoutView.findViewById(R.id.pwd_cancel);

        mNum0 = (Button) mLayoutView.findViewById(R.id.btn_pass_0);
        mNum1 = (Button) mLayoutView.findViewById(R.id.btn_pass_1);
        mNum2 = (Button) mLayoutView.findViewById(R.id.btn_pass_2);
        mNum3 = (Button) mLayoutView.findViewById(R.id.btn_pass_3);
        mNum4 = (Button) mLayoutView.findViewById(R.id.btn_pass_4);
        mNum5 = (Button) mLayoutView.findViewById(R.id.btn_pass_5);
        mNum6 = (Button) mLayoutView.findViewById(R.id.btn_pass_6);
        mNum7 = (Button) mLayoutView.findViewById(R.id.btn_pass_7);
        mNum8 = (Button) mLayoutView.findViewById(R.id.btn_pass_8);
        mNum9 = (Button) mLayoutView.findViewById(R.id.btn_pass_9);
        mNumx = (Button) mLayoutView.findViewById(R.id.btn_pass_x);
        mNumj = (Button) mLayoutView.findViewById(R.id.btn_pass_j);
        mShowKey = (TextView) mLayoutView.findViewById(R.id.passwd_set);

        String ypassword = getPassword();//UtilShareDB.getInstance().ReadSysKey("password", "123456");
        mShowKey.setText(ypassword);
        password = ypassword;
        mNum0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                if (password.length() < 8 ) {
                    password = password + "0";
                    mShowKey.setText(password);
                }
            }
        });

        mNum1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                if (password.length() < 8 ) {
                    password = password + "1";
                    mShowKey.setText(password);
                }
            }
        });

        mNum2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                if (password.length() < 8 ) {
                    password = password + "2";
                    mShowKey.setText(password);
                }
            }
        });
        mNum3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                if (password.length() < 8 ) {
                    password = password + "3";
                    mShowKey.setText(password);
                }
            }
        });
        mNum4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                if (password.length() < 8 ) {
                    password = password + "4";
                    mShowKey.setText(password);
                }
            }
        });
        mNum5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                if (password.length() < 8 ) {
                    password = password + "5";
                    mShowKey.setText(password);
                }
            }
        });
        mNum6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                if (password.length() < 8 ) {
                    password = password + "6";
                    mShowKey.setText(password);
                }
            }
        });
        mNum7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                if (password.length() < 8 ) {
                    password = password + "7";
                    mShowKey.setText(password);
                }
            }
        });
        mNum8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                if (password.length() < 8 ) {
                    password = password + "8";
                    mShowKey.setText(password);
                }
            }
        });
        mNum9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                if (password.length() < 8 ) {
                    password = password + "9";
                    mShowKey.setText(password);
                }
            }
        });
        mNumx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.length() > 0 ) {
                    password = password.substring(0, password.length() - 1);
                    mShowKey.setText(password);
                }
            }
        });
        mNumj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword(password);
                getPassword();
               // UtilShareDB.getInstance().WriteSysKey("password", password);
            }
        });

        // mCancel.OnClickListener()
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this(Dialog).dismiss();
                SetpwdDialogActivity.this.finish();
            }
        });
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  UtilFileOper.getInstance().DeleteDir(absDir);
                // UtilShareDB.getInstance().WriteSysKey("password", password);
                setPassword(password);
                getPassword();
                SetpwdDialogActivity.this.finish();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("SetdateDialogActivity", "start timeset_dialog");
        //initViews();
        //setContentView(R.layout.dialog_passwd);
        createFloatWindow(this);


    }
}
