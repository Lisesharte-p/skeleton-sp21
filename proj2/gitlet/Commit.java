package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import static gitlet.Utils.*;


public class Commit implements Serializable {

    private final String message;
    private final ArrayList<String> fileHash;

    boolean isMerge;
    ArrayList<String> files;
    ArrayList<String> pervCommit;
    ArrayList<String> tracked;
    Date date;
    String hashMetadata;
    String timeStamp;

    Commit(String message, ArrayList<String> files) {
        this.message = message;
        this.date = new Date();
        fileHash = new ArrayList<>();
        this.tracked = new ArrayList<>();
        this.files = new ArrayList<>();
        this.pervCommit = new ArrayList<>();
        this.timeStamp = String.valueOf(new Date());
        if (files != null) {
            for (String f : files) {
                this.files.add(f);
                String newfile = readContentsAsString(new File(f));
                this.fileHash.add(sha1(newfile));
            }


        }


        this.hashMetadata = sha1(message, timeStamp);


    }

    public Commit getPervCommit() {
        if (pervCommit.isEmpty()) {
            return null;
        }
        File perv = join(Repository.GITLET_DIR, pervCommit.get(0), "data");
        if (!perv.exists()) {
            return null;
        }
        return readObject(perv, Commit.class);
    }

    public boolean checkChanged() {
        Commit thePervCommit = getPervCommit();
        return !thePervCommit.fileHash.equals(fileHash);
    }

    public String getHashMetadata() {
        return this.hashMetadata;
    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }


}
