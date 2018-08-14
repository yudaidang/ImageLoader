package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import java.io.File;

public class Entry {
    public File file;
    public long sizeBytes;
    public int key;

    public Entry(File file, long sizeBytes, int key) {
        this.file = file;
        this.sizeBytes = sizeBytes;
        this.key = key;
    }
}
