package ap.mobile.notedifywithfirebase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    private PreviewView viewFinder;
    private ImageButton ibGallery, ibCapture;
    private ExecutorService cameraExecutor;
    private Uri currentImageUri;
    private ImageCapture imageCapture;
    Map<String, String> cloudinaryConfig = new HashMap<>();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    currentImageUri = uri;
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        viewFinder = findViewById(R.id.viewFinder);
        ibGallery = findViewById(R.id.image_select_button);
        ibCapture = findViewById(R.id.image_capture_button);

        cloudinaryConfig.put("cloud_name", "dedqotfgx");
        cloudinaryConfig.put("api_key", "195986673787355");
        cloudinaryConfig.put("api_secret", "2miZ8wFvq4qsTk7BsGVMS2EiUAM");

        try {
            MediaManager.init(this, cloudinaryConfig);
        } catch (IllegalStateException e) {
            // Cloudinary already initialized
        }

        ibCapture.setOnClickListener(v -> {
            takePhoto();
        });

        ibGallery.setOnClickListener(v -> {
            pickImage.launch("image/*");
        });

        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();

    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .setJpegQuality(100)
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );

            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Camera initialization failed",
                        Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        File photoFile = new File(
                getOutputDirectory(),
                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                        .format(System.currentTimeMillis()) + ".jpg"
        );

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults output) {
                        currentImageUri = Uri.fromFile(photoFile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Toast.makeText(CameraActivity.this,
                                "Photo capture failed: " + exc.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Toast.makeText(this, "Capture button clicked", Toast.LENGTH_SHORT).show();
        uploadImage();
    }

    private File getOutputDirectory() {
        File mediaDir = getExternalMediaDirs()[0];
        if (mediaDir != null) {
            File appDir = new File(mediaDir, getString(R.string.app_name));
            appDir.mkdirs();
            return appDir;
        }
        return getFilesDir();
    }

    private void uploadImage() {
        if (currentImageUri == null) return;

        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();

        try {
            File imageFile = getFileFromUri(currentImageUri);
            String requestId = MediaManager.get().upload(Uri.fromFile(imageFile))
                    .option("folder", "notedify")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {

                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {

                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String imageUrl = (String) resultData.get("secure_url");
                            Toast.makeText(CameraActivity.this, imageUrl, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CameraActivity.this, AddNotes.class);
                            intent.putExtra("NOTE_ID", getIntent().getStringExtra("NOTE_ID"));
                            intent.putExtra("NOTE_TITLE", getIntent().getStringExtra("NOTE_TITLE"));
                            intent.putExtra("NOTE_CONTENT", getIntent().getStringExtra("NOTE_CONTENT"));
                            intent.putExtra("NOTE_CATEGORY", getIntent().getStringExtra("NOTE_CATEGORY"));
                            intent.putExtra("imageUrl", imageUrl);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            runOnUiThread(() -> {
                                Toast.makeText(CameraActivity.this,
                                        "Upload failed: " + error.getDescription(),
                                        Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    })
                    .dispatch();

        } catch (IOException e) {
            Toast.makeText(this, "Failed to prepare image: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private File getFileFromUri(Uri uri) throws IOException {
        File destinationFile = new File(getCacheDir(), "temp_image.jpg");

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(destinationFile)) {

            if (inputStream == null) {
                throw new IOException("Failed to open input stream");
            }

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return destinationFile;
        }
    }
}