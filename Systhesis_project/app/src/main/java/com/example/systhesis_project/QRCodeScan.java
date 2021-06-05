package com.example.systhesis_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.Calendar;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScan extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA=1;
    private ZXingScannerView ScannerView;
    private static int cam = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScannerView = new ZXingScannerView(this);
        setContentView(ScannerView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion >= Build.VERSION_CODES.M){
            if(checkPermission()){
                Toast.makeText(getApplicationContext(),"Permission Granted", Toast.LENGTH_LONG).show();
            }
            else{
                requestPermission();
            }
        }
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(QRCodeScan.this,new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionResult(int requestCode, String permissions[],int[] grantResult){
        switch (requestCode){
            case REQUEST_CAMERA:
                if(grantResult.length>0){
                    boolean cameraAccept = grantResult[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccept){
                        Toast.makeText(getApplicationContext(),"Permission Granted by User",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Permission Not Granted by User",Toast.LENGTH_LONG).show();
                        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                            showMessageOKCancel("You need to grant the permission",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion >= Build.VERSION_CODES.M){
            if(checkPermission()){
                if(ScannerView==null){
                    ScannerView = new ZXingScannerView(this);
                    setContentView(ScannerView);
                }
                ScannerView.setResultHandler(this);
                ScannerView.startCamera();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ScannerView.stopCamera();
        ScannerView=null;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener oklistener){
        new AlertDialog.Builder(QRCodeScan.this)
                .setMessage(message)
                .setPositiveButton("OK",oklistener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        String userName = getIntent().getStringExtra("uname");
        String password = getIntent().getStringExtra("pword");
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                ScannerView.resumeCameraPreview(QRCodeScan.this);
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                onDestroy();
//            }
//        });
//
//        builder.setMessage(result.getText());
//        AlertDialog dialog = builder.create();
//        dialog.show();
        String qrCode = result.getText();
        Intent intent = new Intent(QRCodeScan.this,CheckAttendance.class);
        intent.putExtra("usname",userName);
        intent.putExtra("psword",password);
        intent.putExtra("qrcode",qrCode);
        startActivity(intent);
        finish();
    }
}