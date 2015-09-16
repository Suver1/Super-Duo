package it.jaschke.alexandria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/*
 * Class that lets users scan barcodes.
 * Source and instructions: https://github.com/dm77/barcodescanner
 *
 * NOTE: There is a memory leak issue with this zbar library?
 * http://stackoverflow.com/questions/26375920/android-performing-stop-of-activity-that-is-not-resumed#answer-26379590
 */

public class ScannerActivity extends ActionBarActivity implements ZBarScannerView.ResultHandler {
    private static final String LOG_TAG = ScannerActivity.class.getSimpleName();
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
        // Set scan format
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.EAN13);
        mScannerView.setFormats(formats);

        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        if (rawResult.getContents() != null && rawResult.getContents().startsWith("978")) {
            Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
            setResult(0, new Intent().putExtra("EAN13", rawResult.getContents()));
            finish();
        } else {
            mScannerView.startCamera();
        }
    }
}