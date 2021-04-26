package com.example.beautysalon.ui.widget;

/**
 * @author Lee
 * @date 2021.4.11  17:56
 * @description
 */
//public class MyDialog extends AlertDialog implements View.OnClickListener {
//    EditText mEtPasswd;
//    Button mBtnCancel, mBtnConnect;
//    Context mContext;
//
//    protected MyDialog(@NonNull Context context) {
//        super(context);
//        mContext = context;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_my_dialog);
//        mEtPasswd = (EditText) findViewById(R.id.et_passwd);
//        //保证EditText能弹出键盘
//        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        this.setCancelable(false);
//        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
//        mBtnCancel.setOnClickListener(this);
//        mBtnConnect = (Button) findViewById(R.id.btn_connect);
//        mBtnConnect.setOnClickListener(this);
//    }
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn_cancel:
//                this.dismiss();
//                break;
//            case R.id.btn_connect:
//                if (TextUtils.isEmpty(mEtPasswd.getText())) {
//                    Toast.makeText(mContext, "密码不能为空", Toast.LENGTH_SHORT).show();
//                } else {
//                    this.dismiss();
//                    Toast.makeText(mContext, mEtPasswd.getText().toString(), Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//                break;
//        }
//    }
//}
