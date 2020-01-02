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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.edusoho.cloud.core.entity.ResourceType;
import com.edusoho.playerdemo.R;

public class UploadDialog extends DialogFragment {

    private LinearLayout         mBtnUploadVideo;
    private LinearLayout         mBtnUploadAudio;
    private LinearLayout         mBtnUploadDoc;
    private LinearLayout         mBtnUploadAnimPPT;

    private UploadDialogListener mUploadDialogListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_upload_resource, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        mBtnUploadVideo = view.findViewById(R.id.ll_upload_video);
        mBtnUploadAudio = view.findViewById(R.id.ll_upload_audio);
        mBtnUploadDoc = view.findViewById(R.id.ll_upload_doc);
        mBtnUploadAnimPPT = view.findViewById(R.id.ll_upload_anim_ppt);

        if (mUploadDialogListener!= null) {
            mBtnUploadVideo.setOnClickListener(v -> mUploadDialogListener.uploadResourceByType(this, ResourceType.VIDEO));
            mBtnUploadAudio.setOnClickListener(v -> mUploadDialogListener.uploadResourceByType(this, ResourceType.AUDIO));
            mBtnUploadDoc.setOnClickListener(v -> mUploadDialogListener.uploadResourceByType(this, ResourceType.DOCUMENT));
            mBtnUploadAnimPPT.setOnClickListener(v -> mUploadDialogListener.uploadResourceByType(this, ResourceType.PPT));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window                     window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static UploadDialog newInstance(Bundle bundle) {
        UploadDialog fragment = new UploadDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    public UploadDialog setUploadDialogListener(UploadDialogListener listener) {
        mUploadDialogListener = listener;
        return this;
    }

    public interface UploadDialogListener {
        /**
         * 按类型选择上传的文件
         * @param dialog
         * @param resourceType
         */
        void uploadResourceByType(UploadDialog dialog, ResourceType resourceType);
    }
}
