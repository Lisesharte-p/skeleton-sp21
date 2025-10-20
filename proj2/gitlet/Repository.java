package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     * <p>
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    static class stagedPair implements Serializable {
        String name;
        Boolean markedToRemove;

        @Override
        public boolean equals(Object o) {
            stagedPair k = (stagedPair) o;
            return this.name.equals(k.name);
        }

    }

    static class branchHead {
        String hash;
        String branchName;

    }

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static ArrayList<String> HEAD;
    public static ArrayList<branchHead> branches;
    public static String master;
    public static ArrayList<stagedPair> STAGING_AREA;
    public static ArrayList<String> currentMasterTracked;
    static final File HEADfile = join(GITLET_DIR, "HEAD");
    static final File MASTER = join(GITLET_DIR, "Master");
    static final File TRACKING = join(GITLET_DIR, "TRACKING");
    static final File STAGING = join(GITLET_DIR, "STAGING");
    static final File BRANCHES = join(GITLET_DIR, "BRANCHES");
    public static ArrayList<String> removedFiles;

    /* TODO: fill in the rest of this class. */

    public static void readConfig() {
        if (HEADfile.exists() && MASTER.exists() && TRACKING.exists()) {
            HEAD = readObject(HEADfile, ArrayList.class);
            master = readObject(MASTER, String.class);
            currentMasterTracked = readObject(TRACKING, ArrayList.class);
            STAGING_AREA = readObject(STAGING, ArrayList.class);
        }
    }

    public static void getGlobalLog() {
        List<String> commitNames = plainFilenamesIn(GITLET_DIR);

        for (String x : new ArrayList<>(commitNames)) {
            File commitIter = join(GITLET_DIR, (String) x, "data");
            Commit currCommit = readObject(commitIter, Commit.class);

            System.out.println("===");
            String Hash = String.format("Commit %s", currCommit.getHashMetadata());
            System.out.println(Hash);
            String date = String.format("Date: %s", currCommit.date);
            System.out.println(date);
            System.out.println(currCommit.getMessage());
            System.out.println();


        }

    }

    public static void findCommit(String message) {
        List<String> commitNames = plainFilenamesIn(GITLET_DIR);

        for (String x : new ArrayList<>(commitNames)) {
            File commitIter = join(GITLET_DIR, (String) x, "data");
            Commit currCommit = readObject(commitIter, Commit.class);
            if (currCommit.getMessage().equals(message)) {
                System.out.println("===");
                String Hash = String.format("Commit %s", currCommit.getHashMetadata());
                System.out.println(Hash);
                String date = String.format("Date: %s", currCommit.date);
                System.out.println(date);
                System.out.println(currCommit.getMessage());
                System.out.println();
            }


        }
    }

    public static Commit getMaster() {
        return readObject(join(GITLET_DIR, master, "data"), Commit.class);
    }

    public static void initCommit(String message) {
        File newRepo = GITLET_DIR;
        if (newRepo.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }

        newRepo.mkdir();
        Commit initCommit = new Commit("initial commit", null);

        initCommit.date = new Date(0);
        initCommit.timeStamp = String.valueOf(new Time(0));
        initCommit.hashMetadata = sha1(initCommit.getMessage(), initCommit.timeStamp);
        STAGING_AREA = new ArrayList<>();

        HEAD = new ArrayList<>();

        try {
            MASTER.createNewFile();
            TRACKING.createNewFile();
            HEADfile.createNewFile();
            STAGING.createNewFile();
            BRANCHES.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        master = initCommit.getHashMetadata();
        HEAD.add(initCommit.getHashMetadata());
        branches = new ArrayList<>();
        branchHead branchMaster = new branchHead();
        branchMaster.branchName = "master";
        branchMaster.hash = initCommit.getHashMetadata();
        addCommit(initCommit);
        currentMasterTracked = new ArrayList<>();
        branches.add(branchMaster);
        writeObject(BRANCHES, branches);
        writeObject(HEADfile, HEAD);
        writeObject(MASTER, master);
        writeObject(TRACKING, currentMasterTracked);
        writeObject(STAGING, STAGING_AREA);

    }

    public static void removeFile(String arg) {
        stagedPair rmFile = new stagedPair();
        rmFile.name = arg;
        rmFile.markedToRemove = true;
        if (!(STAGING_AREA.contains(rmFile) && currentMasterTracked.contains(arg))) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        for (stagedPair x : STAGING_AREA) {
            if (x.equals(rmFile)) {
                if (!x.markedToRemove) {
                    x.markedToRemove = true;
                    break;
                }
            }
        }

        if (Repository.currentMasterTracked.contains(arg)) {
            Repository.currentMasterTracked.remove(arg);
            File CWDfile = join(Repository.CWD, arg);
            removedFiles.add(arg);
            STAGING_AREA.add(rmFile);
            CWDfile.delete();
        }

        writeObject(STAGING, STAGING_AREA);
        writeObject(TRACKING, currentMasterTracked);
    }

    public static void addFile(String arg) {
        File f = new File(arg);
        stagedPair newFile = new stagedPair();
        newFile.name = arg;
        newFile.markedToRemove = false;
        if (f.exists()) {
            if (!currentMasterTracked.contains(arg)) {
                currentMasterTracked.add(arg);
            }


            File currentMasterFile = join(Repository.GITLET_DIR, Repository.master, arg);
            if (currentMasterFile.exists()) {
                String argContent = readContentsAsString(f);
                String content = readContentsAsString(currentMasterFile);
                String currentMasterHash = sha1(content);
                if (currentMasterHash.equals(sha1(argContent))) {
                    STAGING_AREA.remove(newFile);
                    writeObject(STAGING, STAGING_AREA);

                    System.exit(0);
                }
            }
            for (stagedPair x : STAGING_AREA) {
                if (x.equals(newFile)) {
                    if (x.markedToRemove) {
                        x.markedToRemove = false;
                        break;
                    } else {
                        STAGING_AREA.remove(x);
                        break;
                    }
                }
            }
            if (!STAGING_AREA.contains(newFile)) {
                STAGING_AREA.add(newFile);
            }

        } else {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        writeObject(STAGING, STAGING_AREA);
        writeObject(TRACKING, currentMasterTracked);
    }

    public static void addCommit(Commit newCommit) {
        File f = join(GITLET_DIR, newCommit.getHashMetadata());
        File c = join(GITLET_DIR, newCommit.getHashMetadata(), "data");


        f.mkdir();

        if (newCommit.files != null) {
            for (String fs : newCommit.files) {
                try {
                    if (!currentMasterTracked.contains(fs)) {
                        currentMasterTracked.add(fs);
                    }
                    String createFile = readContentsAsString(join(CWD, fs));
                    File newFile = join(f, fs);
                    newFile.createNewFile();
                    writeContents(newFile, createFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        newCommit.tracked = currentMasterTracked;
        if (HEAD != null) {
            HEAD.remove(master);
            HEAD.add(newCommit.getHashMetadata());
        }
        master = newCommit.getHashMetadata();
        STAGING_AREA = new ArrayList<>();
        writeObject(MASTER, master);
        writeObject(HEADfile, HEAD);
        writeObject(STAGING, STAGING_AREA);
        writeObject(TRACKING, currentMasterTracked);
        try {
            c.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(c, newCommit);


    }

    public static void makeCommit(String message) {
        for (stagedPair x : STAGING_AREA) {
            if (x.markedToRemove) {
                currentMasterTracked.remove(x.name);
            }
        }

        Commit thisCommit = new Commit(message, currentMasterTracked);
        thisCommit.pervCommit.add(master);
        if (!thisCommit.checkChanged()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        addCommit(thisCommit);
    }

    public static void makeBranch(String arg) {
        branchHead newBranch = new branchHead();
        newBranch.hash = getMaster().getHashMetadata();
        newBranch.branchName = arg;
        branches.add(newBranch);
        writeObject(BRANCHES,branches);
    }

}
