package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static Commit[] HEAD;
    public static Commit master;
    public static final File STAGING_AREA=join(GITLET_DIR,"stagingArea");
    /* TODO: fill in the rest of this class. */


    public static void init(String message){
        File newRepo=GITLET_DIR;
        if(newRepo.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }

        newRepo.mkdir();
        Commit initCommit=new Commit("initial commit",null);
        STAGING_AREA.mkdir();
        addCommit(initCommit);
    }
    public static void addCommit(Commit newCommit){
        File f=join(GITLET_DIR, newCommit.getHashMetadata());

        f.mkdir();
        for(File fs:newCommit.files){
            try {
                String createFile=readContentsAsString(fs);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        File[] fs=STAGING_AREA.listFiles();
        for(File files:fs){
            files.delete();
        }

    }
    public static void makeCommit(String message){
        if(STAGING_AREA.listFiles().length==0){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit thisCommit=new Commit(message,STAGING_AREA.listFiles());

    }

}
