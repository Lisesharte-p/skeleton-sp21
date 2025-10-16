package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import static gitlet.Utils.sha1;

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
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private String message;
    public LinkedListDeque<File> files;
    private String hashMetadata;
    private LinkedListDeque<String> fileHash;
    private String timeStamp;
    public Commit pervCommit;
    public LinkedListDeque<String> tracked;
    /* TODO: fill in the rest of this class. */

    Commit(String message, File[] files) {
        this.message = message;
        this.files=new LinkedListDeque<File>();
        fileHash=new LinkedListDeque<>();
        if(files!=null)
        {
            for (File file : files) {
                this.files.addLast(file);
            }
        }
        this.timeStamp = String.valueOf(new Date());
        this.hashMetadata = sha1(message, timeStamp);
        updateHash();
    }
    public void addFile(File f){
        this.files.addLast(f);
        this.fileHash.addLast(sha1(f));
    }
    public void updateHash(){
        if (this.files != null) {
            for (File file : this.files) {
                this.fileHash.addLast(sha1(file));
            }
        }
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
