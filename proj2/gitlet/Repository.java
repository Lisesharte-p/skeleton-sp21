package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

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
    public static LinkedListDeque<Commit> HEAD;
    public static Commit master;
    public static final File STAGING_AREA=join(GITLET_DIR,"stagingArea");
    public static LinkedListDeque<String> currentMasterTracked;
    static File HEADfile=join(GITLET_DIR,"HEAD");
    static File MASTER=join(GITLET_DIR,"Master");
    static File TRACKING=join(GITLET_DIR,"TRACKING");

    /* TODO: fill in the rest of this class. */


    public static void init(String message){
        File newRepo=GITLET_DIR;
        if(newRepo.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }

        newRepo.mkdir();
        Commit initCommit=new Commit("initial commit",null);
        initCommit.date=new Date(0,0,0);
        STAGING_AREA.mkdir();

        HEAD=new LinkedListDeque<>();

        try{
            MASTER.createNewFile();
            TRACKING.createNewFile();
            HEADfile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        master=initCommit;
        HEAD.addLast(initCommit);
        addCommit(initCommit);
        currentMasterTracked =new LinkedListDeque<String>();

        writeContents(HEADfile,HEAD);
        writeContents(MASTER,master);
        writeContents(TRACKING,currentMasterTracked);

    }
    public static void addCommit(Commit newCommit){
        File f=join(GITLET_DIR, newCommit.getHashMetadata());
        File c=join(GITLET_DIR,newCommit.getHashMetadata(),"data");


        f.mkdir();

        if(newCommit.files!=null)
        {
            for (File fs : newCommit.files) {
                try {
                    currentMasterTracked.addLast(fs.getName());
                    String createFile = readContentsAsString(fs);
                    File newFile = join(f, fs.getName());
                    newFile.createNewFile();
                    writeContents(newFile, createFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        newCommit.tracked= currentMasterTracked;
        if(HEAD!=null)
        {
            HEAD.remove(HEAD.getIndex(master));
            HEAD.addLast(newCommit);
        }
        master=newCommit;
        try{
            c.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(c,newCommit);
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
        thisCommit.pervCommit.addLast(master);
        addCommit(thisCommit);
    }

}
