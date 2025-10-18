package gitlet;

import edu.princeton.cs.algs4.ST;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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
    public static ArrayList<String> HEAD;
    public static String master;
    public static ArrayList<String> STAGING_AREA;
    public static ArrayList<String> currentMasterTracked;
    static File HEADfile=join(GITLET_DIR,"HEAD");
    static File MASTER=join(GITLET_DIR,"Master");
    static File TRACKING=join(GITLET_DIR,"TRACKING");
    static File STAGING=join(GITLET_DIR,"STAGING");

    /* TODO: fill in the rest of this class. */

    public static void readConfig(){
        if(HEADfile.exists()&&MASTER.exists()&&TRACKING.exists()) {
            HEAD = readObject(HEADfile, ArrayList.class);
            master = readObject(MASTER, String.class);
            currentMasterTracked = readObject(TRACKING, ArrayList.class);
            STAGING_AREA=readObject(STAGING, ArrayList.class);
        }
    }
    public static Commit getMaster(){
        Commit masterThis=readObject(join(GITLET_DIR,master,"data"),Commit.class);
        return masterThis;
    }
    public static void initCommit(String message){
        File newRepo=GITLET_DIR;
        if(newRepo.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }

        newRepo.mkdir();
        Commit initCommit=new Commit("initial commit",null);

        initCommit.date=new Date(0);
        initCommit.timeStamp=String.valueOf(new Time(0));
        initCommit.hashMetadata=sha1(initCommit.getMessage(),initCommit.timeStamp);
        STAGING_AREA=new ArrayList<>();

        HEAD= new ArrayList<>();

        try{
            MASTER.createNewFile();
            TRACKING.createNewFile();
            HEADfile.createNewFile();
            STAGING.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        master=initCommit.getHashMetadata();
        HEAD.add(initCommit.getHashMetadata());
        addCommit(initCommit);
        currentMasterTracked = new ArrayList<>();

        writeObject(HEADfile,HEAD);
        writeObject(MASTER,master);
        writeObject(TRACKING,currentMasterTracked);
        writeObject(STAGING,STAGING_AREA);

    }
    public static void removeFile(String arg){
        STAGING_AREA.remove(arg);

        if(Repository.currentMasterTracked.contains(arg)){
            Repository.currentMasterTracked.remove(arg);
            File CWDfile=join(Repository.CWD,arg);
            CWDfile.delete();
        }
    }
    public static void addFile(String arg){
        File f = new File(arg);
        if (f.exists()) {

            if (STAGING_AREA.contains(arg)) {
                STAGING_AREA.remove(arg);
                currentMasterTracked.remove(arg);
            } else {
                STAGING_AREA.add(arg);
                currentMasterTracked.add(arg);
            }

            File currentMasterFile=join(Repository.GITLET_DIR,Repository.master,arg);
            if(currentMasterFile.exists())
            {
                String argContent=readContentsAsString(f);
                String content=readContentsAsString(currentMasterFile);
                String currentMasterHash = sha1(content);
                if (currentMasterHash.equals(sha1(argContent))) {
                    writeObject(STAGING,STAGING_AREA);
                    writeObject(TRACKING,currentMasterTracked);
                    System.exit(0);
                }
            }


        } else {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        writeObject(STAGING,STAGING_AREA);
        writeObject(TRACKING,currentMasterTracked);
    }
    public static void addCommit(Commit newCommit){
        File f=join(GITLET_DIR, newCommit.getHashMetadata());
        File c=join(GITLET_DIR,newCommit.getHashMetadata(),"data");


        f.mkdir();

        if(newCommit.files!=null)
        {
            for (String fs : newCommit.files) {
                try {if(!currentMasterTracked.contains(fs))
                    {
                        currentMasterTracked.add(fs);
                    }
                    String createFile = readContentsAsString(join(CWD,fs));
                    File newFile = join(f, fs);
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
            HEAD.remove(master);
            HEAD.add(newCommit.getHashMetadata());
        }
        master=newCommit.getHashMetadata();
        writeObject(MASTER,master);
        writeObject(HEADfile,HEAD);
        writeObject(STAGING,STAGING_AREA);
        writeObject(TRACKING,currentMasterTracked);
        try{
            c.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(c,newCommit);


    }
    public static void makeCommit(String message){


        Commit thisCommit=new Commit(message, STAGING_AREA);
        thisCommit.pervCommit.add(master);
        if(!thisCommit.checkChanged()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        addCommit(thisCommit);
    }

}
