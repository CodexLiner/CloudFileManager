package com.codingergo.documentsaver.MyDownloadManager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.codingergo.documentsaver.R;

import java.util.NavigableMap;

public class MyDownloadManager extends AppCompatActivity {
    String Url , Name ,Mim;
    Long id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        Url = getIntent().getStringExtra("URL");
        Name = getIntent().getStringExtra("Name");
        Mim= MimeTypeMap.getFileExtensionFromUrl(Url);
try {
    android.app.DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Url));
    request.allowScanningByMediaScanner();
    getApplicationContext().getFilesDir().getPath();
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Name );
    request.addRequestHeader("Downloading..." ,Name);
    request.setDescription("Downloading "+Name);
    request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    DownloadManager dm = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
    dm.enqueue(request);
  }  catch (Exception e){
       Toast.makeText(this, "Something Went Wrong ", Toast.LENGTH_SHORT).show();
  }
   finish();
    }
}