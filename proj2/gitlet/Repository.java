package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
    public static final File STAGING_AREA=join(GITLET_DIR,"stagingArea");
    public static ArrayList<String> currentMasterTracked;
    static File HEADfile=join(GITLET_DIR,"HEAD");
    static File MASTER=join(GITLET_DIR,"Master");
    static File TRACKING=join(GITLET_DIR,"TRACKING");

    /* TODO: fill in the rest of this class. */

    public static void readConfig(){
        if(HEADfile.exists()&&MASTER.exists()&&TRACKING.exists()) {
            HEAD = readObject(HEADfile, ArrayList.class);
            master = readObject(MASTER, String.class);
            currentMasterTracked = readObject(TRACKING, ArrayList.class);
        }
    }
    public static Commit getMaster(){
        Commit masterThis=readObject(join(GITLET_DIR,master),Commit.class);
        return masterThis;
    }
    public static void initCommit(String message){
        File newRepo=GITLET_DIR;
        if(newRepo.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }

        newRepo.mkdir();
        Commit initCommit=new Commit("initial commit",null);

        initCommit.date=new Date(0,0,0);
        initCommit.timeStamp=String.valueOf(new Date(0,0,0));
        initCommit.hashMetadata=sha1(initCommit.getMessage(),initCommit.timeStamp);
        STAGING_AREA.mkdir();

        HEAD= new ArrayList<>();

        try{
            MASTER.createNewFile();
            TRACKING.createNewFile();
            HEADfile.createNewFile();
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

    }
    public static void removeFile(String arg){
        File fileToRemove = join(Repository.STAGING_AREA, arg);
        if(fileToRemove.exists()){
            fileToRemove.delete();
        }

        if(Repository.currentMasterTracked.contains(arg)){
            Repository.currentMasterTracked.remove(arg);
            File CWDfile=join(Repository.CWD,arg);
            CWDfile.delete();
        }
    }
    public static void addFile(String arg){
        File f = new File(arg);
        if (f.exists()) {
            if(!Repository.currentMasterTracked.contains(arg)){
                Repository.currentMasterTracked.add(arg);
            }
            String buffer=readContentsAsString(f);
            File addFile = join(Repository.STAGING_AREA, arg);
            File currentMasterFile=join(Repository.GITLET_DIR,Repository.master,arg);
            if(currentMasterFile.exists())
            {
                String currentMasterHash = sha1(currentMasterFile);
                if (currentMasterHash.equals(sha1(f))) {
                    System.exit(0);
                }
            }

            if (addFile.exists()) {
                addFile.delete();
            } else {
                try {
                    addFile.createNewFile();
                    writeContents(addFile,buffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }
    public static void addCommit(Commit newCommit){
        File f=join(GITLET_DIR, newCommit.getHashMetadata());
        File c=join(GITLET_DIR,newCommit.getHashMetadata(),"data");


        f.mkdir();

        if(newCommit.files!=null)
        {
            for (String fs : newCommit.files) {
                try {
                    currentMasterTracked.add(fs);
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

        Commit thisCommit=new Commit(message, (ArrayList<String>) plainFilenamesIn(STAGING_AREA));
        thisCommit.pervCommit.add(master);
        addCommit(thisCommit);
    }

}
