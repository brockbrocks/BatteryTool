package app.nehc.batterytool;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import app.nehc.batterytool.adapter.FunctionListAdapter;

public class SetBatteryNoticeDialog extends Dialog {
    private EditText dialogInput;
    private TextView dialogSubmit;
    private SharedPreferences sharedPreferences;
    private FunctionListAdapter.SetBatteryNoticeListener listener;

    public SetBatteryNoticeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = (float) 0.382;
        params.width = (int) (displayMetrics.widthPixels * 0.90);
        window.setAttributes(params);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences("charge_notice_value", Context.MODE_PRIVATE);
        dialogInput = findViewById(R.id.dialog_input);
        dialogInput.setText("" + sharedPreferences.getInt("notice_value", -1));
        dialogSubmit = findViewById(R.id.dialog_submit);
        dialogSubmit.setOnClickListener(v -> {
            sharedPreferences.edit().putInt("notice_value", Integer.parseInt(dialogInput.getText().toString())).commit();
            dismiss();
            listener.refreshList();
        });
    }

    public void setListener(FunctionListAdapter.SetBatteryNoticeListener listener) {
        this.listener = listener;
    }
}
