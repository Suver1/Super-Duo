package it.jaschke.alexandria;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/*
 * Class that lets users scan barcodes.
 * Source and instructions: https://github.com/dm77/barcodescanner
 */

public class ScannerActivity extends ActionBarActivity implements ZBarScannerView.ResultHandler {
    private static final String LOG_TAG = ScannerActivity.class.getSimpleName();
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
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
        Toast.makeText(this, "Contents = " + rawResult.getContents() +
                ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
        mScannerView.startCamera();
        // TODO send result back to MainActivity's AddBook fragment
    }
}