package com.edusoho.playerdemo.ui.resource.upload.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.edusoho.cloud.core.entity.ResourceError;
import com.edusoho.cloud.manager.Uploader;
import com.edusoho.playerdemo.R;

import java.io.File;

public class UploadProcessDialog extends DialogFragment {
    private static final String UPLOAD_START  = "http://es3.cloud-test.edusoho.cn/api/upload/test/start";
    private static final String UPLOAD_FINISH = "http://es3.cloud-test.edusoho.cn/api/upload/test/finish";

    private ProgressBar mProgressBar;
    private TextView    mTitle;

    private UploadDialogListener mUploadDialogListener;
    private int                  mProgress;
    boolean isCancelled = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_uploading_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        mTitle = view.findViewById(R.id.tv_progress_title);
        mProgressBar = view.findViewById(R.id.tv_progress);
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

    public static UploadProcessDialog newInstance(Bundle bundle) {
        UploadProcessDialog fragment = new UploadProcessDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    public UploadProcessDialog setUploadDialogListener(UploadDialogListener listener) {
        mUploadDialogListener = listener;
        return this;
    }

    public void startUploadFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Uploader.upload(UPLOAD_START, UPLOAD_FINISH, "1232132121213", file, new Uploader.OnUploadListener() {
                @Override
                public void onError(ResourceError error) {
                    uploadFail();
                }

                @Override
                public void onSuccess(String result) {
                    uploadFinish();
                }

                @Override
                public void progress(double percent) {
                    setProgress((int) (percent * 100));
                }

                @Override
                public boolean isCancelled() {
                    return isCancelled;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                uploadCancel();
                return true;
            }
            return false;
        });
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        mProgressBar.setProgress(progress);
    }

    public void uploadFinish() {
        if (mUploadDialogListener != null) {
            mUploadDialogListener.uploadFinish(this);
        }
    }

    public void uploadCancel() {
        if (mUploadDialogListener != null) {
            isCancelled = true;
            mUploadDialogListener.uploadCancel(this);
        }
    }

    private void uploadFail() {
        if (mUploadDialogListener != null) {
            mUploadDialogListener.uploadFail(this);
        }
    }

    public interface UploadDialogListener {
        void uploadFinish(UploadProcessDialog dialog);

        default void uploadCancel(UploadProcessDialog dialog) {
        }

        default void uploadFail(UploadProcessDialog dialog) {
        }
    }
}
