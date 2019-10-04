package com.anprmobilesdk.example;

import android.app.Activity;
import android.content.Context;

import com.anprsystemsltd.sdk.mobile.ANPR;
import com.anprsystemsltd.sdk.mobile.CameraInput;
import com.anprsystemsltd.sdk.mobile.Event;
import com.anprsystemsltd.sdk.mobile.Result;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Context context = this;
    private Activity activity = this;
    private ANPR sdkAnpr;
    private CameraInput cameraInput;
    private boolean inited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sdkAnpr = new ANPR(context, new ANPR.EventListener() {

            @Override
            public void onEvent(Event event) {
                if (event.type == ANPR.EventListener.EVENT_TYPE_INIT) {
                    if (event.success) {
                        inited = true;
                        setTitle("ANPR_SDK ver:" + sdkAnpr.getVersion() + " - " + sdkAnpr.getUsedLibraryName() + " ID:" + sdkAnpr.getDeviceId());
                        startCamera();
                    }
                }
            }
        });


        ANPR.Parameters paramsSdk = new ANPR.Parameters();
        paramsSdk.licenseMode = ANPR.Parameters.LICENSE_MODE_ONLINE;
        paramsSdk.requestNationality = "HUN";

        sdkAnpr.init(paramsSdk);
    }

    private void startCamera() {
        if (cameraInput != null) {
            return;
        }

        cameraInput = new CameraInput(context, new CameraInput.EventListener()
        {

            @Override
            public void onEvent(Event event) {
                if (event.type == CameraInput.EventListener.EVENT_TYPE_CAMERA_PLATE_FOUND) {
                    final CameraInput.Found found = (CameraInput.Found)event.object;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, found.plate, Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }, CameraInput.MODE_WITH_PREVIEW);
        CameraInput.Parameters paramsCamera = CameraInput.Parameters.CreateDefault(context);
        if (paramsCamera.result.code == Result.OK) {
            setContentView(cameraInput.getCameraPreview());
            Result res = cameraInput.init(paramsCamera);
            if (res.code == Result.OK) {
                cameraInput.start();
            }

        }

    }

    private void stopCamera() {
        if (cameraInput != null) {
            cameraInput.close();
            cameraInput = null;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (inited) {
            startCamera();
        }
    }

    protected void onDestroy()
    {
        super.onDestroy();
        stopCamera();
        if (sdkAnpr != null) {
            sdkAnpr.close();
            sdkAnpr = null;
        }

    }





}
