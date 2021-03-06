package xyz.imxqd.clickclick.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.imxqd.clickclick.App;
import xyz.imxqd.clickclick.R;
import xyz.imxqd.clickclick.dao.DefinedFunction;
import xyz.imxqd.clickclick.dao.KeyMappingEvent;
import xyz.imxqd.clickclick.log.LogUtils;
import xyz.imxqd.clickclick.model.AppKeyEventType;
import xyz.imxqd.clickclick.ui.adapters.FunctionSpinnerAdapter;
import xyz.imxqd.clickclick.utils.KeyEventUtil;

public class AddKeyEventActivity extends BaseActivity {

    public static final String ARG_KEY_EVENT = "key_event";

    private KeyMappingEvent mKeyEvent = new KeyMappingEvent();

    @BindView(R.id.key_tips)
    TextView mTvTips;
    @BindView(R.id.key_name)
    TextView mTvKeyName;
    @BindView(R.id.key_code)
    TextView mTvKeyCode;
    @BindView(R.id.key_device_name)
    TextView mTvDeviceName;
    @BindView(R.id.key_btn_add)
    View mBtnAdd;
    @BindView(R.id.key_info_layout)
    LinearLayout mInfoLayout;
    @BindView(R.id.key_ignore_device)
    CheckBox mCkIgnoreDevice;
    @BindView(R.id.key_event_type)
    Spinner mSpEventType;
    @BindView(R.id.key_function)
    Spinner mSpFunction;

    List<String> mEventTypeValues;

    FunctionSpinnerAdapter mFuncAdapter;
    int mLastSelectedPosition = 0;

    private static final Set<Integer> mKeycodeBlacklist = new HashSet<>();

    static {
        mKeycodeBlacklist.add(KeyEvent.KEYCODE_BACK);
        mKeycodeBlacklist.add(KeyEvent.KEYCODE_APP_SWITCH);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_event);
        setFinishOnTouchOutside(false);
        ButterKnife.bind(this);
        App.get().isServiceOn = false;
        mBtnAdd.setEnabled(false);
        mBtnAdd.setAlpha(0.5f);
        List<String> spinnerArray =  new ArrayList<>();
        Collections.addAll(spinnerArray, getResources().getStringArray(R.array.event_type));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spinnerArray);
        mSpEventType.setAdapter(adapter);

        mEventTypeValues = new ArrayList<>();
        Collections.addAll(mEventTypeValues, getResources().getStringArray(R.array.event_type_value));

        mFuncAdapter = new FunctionSpinnerAdapter();
        mSpFunction.setAdapter(mFuncAdapter);
        mSpFunction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != -1) {
                    mLastSelectedPosition = position;
                } else {
                    mSpFunction.setSelection(mLastSelectedPosition);
                    Intent intent = new Intent(AddKeyEventActivity.this, FunctionsActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        App.get().isServiceOn = true;
        super.onDestroy();
    }

    @OnClick(R.id.key_btn_close)
    public void onCloseBtnClick() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }

    @OnClick(R.id.key_btn_add)
    public void onAddBtnClick() {
        if (mKeyEvent.deviceId < 0 && mKeycodeBlacklist.contains(mKeyEvent.keyCode)) {
            Toast.makeText(this, R.string.device_id_error, Toast.LENGTH_LONG).show();
            return;
        }
        try {

            DefinedFunction function = (DefinedFunction) mFuncAdapter.getItem(mLastSelectedPosition);
            mKeyEvent.funcName = function.name;
            mKeyEvent.funcId = function.id;
            mKeyEvent.ignoreDevice = mCkIgnoreDevice.isChecked();
            mKeyEvent.eventType = AppKeyEventType.valueOf(mEventTypeValues.get(mSpEventType.getSelectedItemPosition()));
            mKeyEvent.save();

            Intent intent = new Intent();
            intent.putExtra(ARG_KEY_EVENT, mKeyEvent);
            setResult(RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, R.string.add_key_event_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mBtnAdd.setEnabled(true);
        mBtnAdd.setAlpha(1f);
        mTvTips.setVisibility(View.GONE);
        mInfoLayout.setVisibility(View.VISIBLE);
        mKeyEvent.deviceId = event.getDeviceId();
        mKeyEvent.keyCode = event.getKeyCode();
        mKeyEvent.keyName = KeyEventUtil.getKeyName(mKeyEvent.keyCode);
        if (event.getDevice() != null) {
            mKeyEvent.deviceName = event.getDevice().getName();
        } else {
            mKeyEvent.deviceName = "UNKNOW";
        }

        mKeyEvent.ignoreDevice = mCkIgnoreDevice.isChecked();

        mTvKeyCode.setText(getString(R.string.key_code, event.getKeyCode()));
        mTvKeyName.setText(getString(R.string.key_name, KeyEventUtil.getKeyName(event.getKeyCode())));
        mTvDeviceName.setText(getString(R.string.key_device_name, event.getDevice().getName()));
        return true;
    }

    @Override
    public void onEvent(int what, Object data) {
        if (what == App.EVENT_WHAT_REFRESH_UI) {
            LogUtils.d("EVENT_WHAT_REFRESH_UI");
            mFuncAdapter.refreshData();
        }
    }
}
