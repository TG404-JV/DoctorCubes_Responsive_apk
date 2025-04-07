package com.tvm.doctorcube;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfViewerActivity extends AppCompatActivity {

    private PDFView pdfView;
    private ProgressBar progressBar;
    private LinearLayout loadingLayout;
    private TextView loadingTextView;
    private TextView titleTextView;
    private ImageButton backButton;

    private String pdfTitle;
    private String pdfUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        // Disable screenshots and screen recording
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        // Initialize views
        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progressBar);
        loadingLayout = findViewById(R.id.loadingLayout);
        loadingTextView = findViewById(R.id.loadingTextView);
        titleTextView = findViewById(R.id.titleTextView);
        backButton = findViewById(R.id.backButton);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            pdfTitle = intent.getStringExtra("pdfTitle");
            pdfUrl = intent.getStringExtra("pdfFilePath");
        }

        // Set title
        if (pdfTitle != null) {
            titleTextView.setText(pdfTitle);
        } else {
            titleTextView.setText("PDF Viewer");
        }

        // Set back button click listener
        backButton.setOnClickListener(v -> finish());

        // Load PDF
        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            loadPdfFromUrl(pdfUrl);
        } else {
            CustomToast.showToast(this, "Invalid PDF URL");
            finish();
        }
    }

    private void loadPdfFromUrl(String url) {
        showLoading(true);

        // Create a file in cache directory to store the downloaded PDF
        File outputFile = new File(getCacheDir(), "temp_pdf_" + System.currentTimeMillis() + ".pdf");

        // Use AsyncTask to download the file in background
        new DownloadPdfTask(outputFile).execute(url);
    }

    private void showLoading(boolean isLoading) {
        loadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void displayPdf(File pdfFile) {
        pdfView.fromFile(pdfFile)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAnnotationRendering(false)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10)
                .onLoad(nbPages -> showLoading(false))
                .onError(t -> {
                    showLoading(false);
                    CustomToast.showToast(this, "Error displaying PDF: " + t.getMessage());
                    finish();
                })
                .load();
    }

    private class DownloadPdfTask extends AsyncTask<String, Integer, Boolean> {
        private File outputFile;
        private Exception exception;

        public DownloadPdfTask(File outputFile) {
            this.outputFile = outputFile;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingTextView.setText("Downloading PDF...");
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if (urls.length == 0) return false;

            String url = urls[0];
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            try {
                URL fileUrl = new URL(url);
                connection = (HttpURLConnection) fileUrl.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    exception = new IOException("Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                    return false;
                }

                int fileLength = connection.getContentLength();

                input = new BufferedInputStream(connection.getInputStream());
                output = new FileOutputStream(outputFile);

                byte[] data = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    output.write(data, 0, count);
                }

                return true;
            } catch (Exception e) {
                exception = e;
                return false;
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (IOException e) {
                    // Ignore
                }
                if (connection != null) connection.disconnect();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values.length > 0) {
                int progress = values[0];
                loadingTextView.setText("Downloading PDF... " + progress + "%");
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                loadingTextView.setText("Opening PDF...");
                displayPdf(outputFile);
            } else {
                showLoading(false);
                String errorMessage = exception != null ? exception.getMessage() : "Unknown error";
                CustomToast.showToast(PdfViewerActivity.this, "Error downloading PDF: " + errorMessage);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File cacheDir = getCacheDir();
        File[] files = cacheDir.listFiles((dir, name) -> name.startsWith("temp_pdf_"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}
