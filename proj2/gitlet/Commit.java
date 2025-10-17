package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     * <p>
     * ArrayList all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    public ArrayList<String> files;
    public ArrayList<String> pervCommit;
    public ArrayList<String> tracked;
    public Date date;
    public String hashMetadata;
    public String timeStamp;
    /**
     * The message of this Commit.
     */
    private String message;
    private ArrayList<String> fileHash;
    /* TODO: fill in the rest of this class. */

    Commit(String message, ArrayList<String> files) {
        this.message = message;
        this.date = new Date();
        fileHash = new ArrayList<>();

        this.timeStamp = String.valueOf(new Date());
        if (files != null) {
            for (String f : files) {
                this.files.add(f);
                this.fileHash.add(sha1(new File(f)));
            }


        }


        this.hashMetadata = sha1(message, timeStamp);


    }

    public Commit getPervCommit() {
        File perv = join(Repository.GITLET_DIR, pervCommit.get(0));
        return readObject(perv, Commit.class);
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
