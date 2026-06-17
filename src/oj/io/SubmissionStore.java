package oj.io;

import oj.core.Submission;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionStore {
    private final File storeFile;

    public SubmissionStore(String path) {
        this.storeFile = new File(path);
    }

    public void save(Submission submission) throws IOException {
        boolean exists = storeFile.exists() && storeFile.length() > 0;
        try (FileOutputStream fos = new FileOutputStream(storeFile, true);
             ObjectOutputStream oos = exists ? new AppendOOS(fos) : new ObjectOutputStream(fos)) {
            oos.writeObject(submission);
        }
    }

    public List<Submission> loadAll() throws IOException, ClassNotFoundException {
        List<Submission> list = new ArrayList<>();
        if (!storeFile.exists() || storeFile.length() == 0) return list;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeFile))) {
            while (true) {
                try {
                    list.add((Submission) ois.readObject());
                } catch (EOFException e) {
                    break;
                }
            }
        }
        return list;
    }

    private static class AppendOOS extends ObjectOutputStream {
        AppendOOS(OutputStream out) throws IOException { super(out); }
        @Override
        protected void writeStreamHeader() throws IOException {
            reset();
        }
    }
}
