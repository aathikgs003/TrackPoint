package com.example.trackpoint;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class S3StorageManager {

    private static S3StorageManager instance;
    private AmazonS3Client s3Client;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface S3UploadCallback {
        void onSuccess(String objectKey, String presignedUrl);
        void onError(Exception e);
    }

    public interface S3DeleteCallback {
        void onSuccess();
        void onError(Exception e);
    }

    private S3StorageManager(Context context) {
        S3Config.init(context);
        initS3Client();
    }

    public static synchronized S3StorageManager getInstance(Context context) {
        if (instance == null) {
            instance = new S3StorageManager(context.getApplicationContext());
        }
        return instance;
    }

    private void initS3Client() {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(
                    S3Config.ACCESS_KEY_ID,
                    S3Config.SECRET_ACCESS_KEY
            );
            s3Client = new AmazonS3Client(credentials);

            // Path-style access is MANDATORY for Backblaze B2 S3 Endpoints
            S3ClientOptions clientOptions = S3ClientOptions.builder()
                    .setPathStyleAccess(true)
                    .build();
            s3Client.setS3ClientOptions(clientOptions);

            s3Client.setEndpoint(S3Config.getFormattedEndpoint());

            try {
                Regions regions = Regions.fromName(S3Config.REGION);
                s3Client.setRegion(Region.getRegion(regions));
            } catch (Exception ignored) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- 1. UPLOAD FUNCTION (Images & PDFs to Backblaze B2 Bucket) ---

    public void uploadFile(Context context, Uri fileUri, String studentRegNo, String dateKey, S3UploadCallback callback) {
        executor.execute(() -> {
            try {
                if (fileUri == null) {
                    notifyError(callback, new IllegalArgumentException("File URI cannot be null"));
                    return;
                }

                String fileName = getFileNameFromUri(context, fileUri);
                String mimeType = context.getContentResolver().getType(fileUri);
                if (mimeType == null) {
                    mimeType = fileName.endsWith(".pdf") ? "application/pdf" : "image/jpeg";
                }

                // Generate object key in bucket: submissions/<studentRegNo>/<dateKey>_<fileName>
                String objectKey = "submissions/" + studentRegNo + "/" + dateKey + "_" + fileName;

                // Create temp file for upload
                File tempFile = createTempFileFromUri(context, fileUri, fileName);

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(mimeType);
                metadata.setContentLength(tempFile.length());

                PutObjectRequest putRequest = new PutObjectRequest(
                        S3Config.BUCKET_NAME,
                        objectKey,
                        tempFile
                ).withMetadata(metadata);

                // Execute S3 Upload to Backblaze B2
                s3Client.putObject(putRequest);

                // Clean up temp file
                if (tempFile.exists()) {
                    boolean deleted = tempFile.delete();
                }

                // Generate 1-hour presigned URL for viewing
                String presignedUrl = generatePresignedUrl(objectKey, 60);

                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onSuccess(objectKey, presignedUrl);
                    }
                });

            } catch (Exception e) {
                notifyError(callback, e);
            }
        });
    }

    // --- 2. VIEW / DISPLAY FUNCTION (Generate Secure Presigned Temporary URL) ---

    public String generatePresignedUrl(String objectKey, int expirationMinutes) {
        if (objectKey == null || objectKey.isEmpty()) return "";

        try {
            Date expirationDate = new Date();
            long msec = expirationDate.getTime();
            msec += 1000L * 60 * expirationMinutes;
            expirationDate.setTime(msec);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(S3Config.BUCKET_NAME, objectKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expirationDate);

            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return url.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // --- 3. PERMANENT HARD DELETE FUNCTION (Purge file versions & hide markers from Backblaze B2 Bucket) ---

    public void deleteFile(String objectKey, S3DeleteCallback callback) {
        executor.execute(() -> {
            try {
                if (objectKey == null || objectKey.isEmpty()) {
                    notifyDeleteError(callback, new IllegalArgumentException("Object Key cannot be null or empty"));
                    return;
                }

                // 1. Purge all S3 version IDs & hide markers to ensure permanent hard delete
                try {
                    VersionListing versionListing = s3Client.listVersions(S3Config.BUCKET_NAME, objectKey);
                    if (versionListing != null && versionListing.getVersionSummaries() != null) {
                        for (S3VersionSummary version : versionListing.getVersionSummaries()) {
                            if (objectKey.equals(version.getKey())) {
                                s3Client.deleteVersion(S3Config.BUCKET_NAME, objectKey, version.getVersionId());
                            }
                        }
                    }
                } catch (Exception ignored) {
                }

                // 2. Execute standard deleteObject fallback
                try {
                    s3Client.deleteObject(S3Config.BUCKET_NAME, objectKey);
                } catch (Exception ignored) {
                }

                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                });

            } catch (Exception e) {
                notifyDeleteError(callback, e);
            }
        });
    }

    // Helper Methods
    private void notifyError(S3UploadCallback callback, Exception e) {
        mainHandler.post(() -> {
            if (callback != null) {
                callback.onError(e);
            }
        });
    }

    private void notifyDeleteError(S3DeleteCallback callback, Exception e) {
        mainHandler.post(() -> {
            if (callback != null) {
                callback.onError(e);
            }
        });
    }

    private String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result != null ? result.lastIndexOf('/') : -1;
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "upload_file";
    }

    private File createTempFileFromUri(Context context, Uri uri, String fileName) throws Exception {
        File tempFile = new File(context.getCacheDir(), "s3_upload_" + System.currentTimeMillis() + "_" + fileName);
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }
}
