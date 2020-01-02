package com.edusoho.playerdemo.ui.resource.upload.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.edusoho.playerdemo.R;
import com.edusoho.playerdemo.util.Utils;

public class EditResourceDialog extends DialogFragment {

    private TextView     mTvTitle;
    private TextView     mTvTips;
    private LinearLayout mLlTips;
    private EditText     mTvEdit;
    private TextView     mTvCancel;
    private TextView     mTvConfirm;

    private String mTitle;
    private String mTips;

    private UploadDialogListener mUploadDialogListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_recource, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvTips = view.findViewById(R.id.tips);
        mLlTips = view.findViewById(R.id.ll_tips);
        mTvEdit = view.findViewById(R.id.tv_edit);
        mTvCancel = view.findViewById(R.id.tv_cancel);
        mTvConfirm = view.findViewById(R.id.tv_confirm);

        if (mTips != null) {
            this.mTvTips.setText(mTips);
            this.mTvTips.setVisibility(View.VISIBLE);
            this.mTvEdit.setVisibility(View.GONE);
            this.mLlTips.setBackground(getResources().getDrawable(R.color.textDivider));
        }

        if (mTitle != null) {
            this.mTvTitle.setText(mTitle);
        }

        if (mUploadDialogListener != null) {
            mTvCancel.setOnClickListener(v -> mUploadDialogListener.onCancel(this));
            mTvConfirm.setOnClickListener(v -> {
                if (mTips == null && mTvEdit.getText().toString().isEmpty()) {
                    Utils.showToast(getContext(), getContext().getString(R.string.not_empty_tips));
                    return;
                }
                mUploadDialogListener.onConfirm(this, mTvEdit.getText().toString());
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window                     window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static EditResourceDialog newInstance(Bundle bundle) {
        EditResourceDialog fragment = new EditResourceDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    public EditResourceDialog setUploadDialogListener(UploadDialogListener listener) {
        mUploadDialogListener = listener;
        return this;
    }

    public EditResourceDialog setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public EditResourceDialog setTips(String tips) {
        this.mTips = tips;
        return this;
    }

    public interface UploadDialogListener {
        void onCancel(EditResourceDialog dialog);

        void onConfirm(EditResourceDialog dialog, String text);
    }
}
