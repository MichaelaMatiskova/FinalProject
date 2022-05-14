package sk.spse.matiskova.finalproject;

import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesystemManager {

    private final String databaseName = "question_bank.sqlite";
    private final Path databasePath;
    private final String databasePathStr;
    private File externalStorageDbFile;

    public FilesystemManager() {
        databasePath = Paths.get(
                Environment.getExternalStorageDirectory().getPath()
                , "Millionaire"
                , databaseName).toAbsolutePath();
        databasePathStr = databasePath.toString();
    }

    public String GetExternalDatabasePath() {
        return databasePathStr;
    }

    public boolean IsDbInExternalStorage() {
        externalStorageDbFile = new File(databasePathStr);
        return externalStorageDbFile.exists();
    }

    public void CopyDbToExternalStorage(AssetManager am) throws IOException {
        CreateDbParentFolderIfNecessary();
        InputStream assetDatabaseStream = am.open(databaseName);
        FileOutputStream outputStream = new FileOutputStream(externalStorageDbFile);
        CopyFile(assetDatabaseStream, outputStream);
        assetDatabaseStream.close();
        outputStream.flush();
        outputStream.close();
    }

    private void CreateDbParentFolderIfNecessary() throws IOException {
        Files.createDirectories(databasePath.getParent());
    }

    private void CopyFile(InputStream src, FileOutputStream dst) throws IOException {
        int read;
        byte[] buffer = new byte[1024];
        if (src != null) {
            do {
                read = src.read(buffer);
                if (read > 0) {
                    dst.write(buffer, 0, read);
                }
            } while (read != -1);
        }
    }
}
