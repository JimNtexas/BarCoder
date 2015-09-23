package com.grayraven.barcoder;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class CameraActivity extends FragmentActivity {

    public static final String SCANNED_IMAGE_FILENAME = "ScannedImage.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
           CameraActivityFragement fragment = CameraActivityFragement.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.camera_container,(android.support.v4.app.Fragment) fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
    }

    public void setScanResult(boolean result) {
        if(result) {
            Toast.makeText(this, "Image retrieved.", Toast.LENGTH_SHORT).show();
        }
    }

}
