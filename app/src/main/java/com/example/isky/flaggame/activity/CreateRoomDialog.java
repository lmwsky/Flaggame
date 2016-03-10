package com.example.isky.flaggame.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.isky.flaggame.R;

/**
 * Created by x1832 on 2016/3/7.
 * 自定义的弹出的创建房间的对话框
 */
public class CreateRoomDialog extends Dialog {
    private EditText editText;
    private Button positiveButton, negativeButton;
    private RadioGroup radioGroup;

    public CreateRoomDialog(Context context) {
        super(context);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_createroom, null);
        radioGroup = (RadioGroup) mView.findViewById(R.id.rg_room_number);
        editText = (EditText) mView.findViewById(R.id.et_room_name);
        positiveButton = (Button) mView.findViewById(R.id.bt_confirm);
        negativeButton = (Button) mView.findViewById(R.id.bt_cancel);
        super.setContentView(mView);
    }

    public String getRoomname() {
        if (editText != null)
            return editText.getText().toString();
        else return null;
    }

    public int getRoomSize() {
        int roomnum = 2;

        if (radioGroup != null) {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            switch (radioButtonId) {
                case R.id.rb_room_two:
                    roomnum = 2;
                    break;
                case R.id.rb_room_six:
                    roomnum = 6;
                    break;
                case R.id.rb_room_ten:
                    roomnum = 10;
                    break;
                default:
                    break;
            }
            return roomnum;
        } else
            return 2;
    }

    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }

    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }
}
