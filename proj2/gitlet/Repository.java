package gitlet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.*;

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
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    static final File HEADfile = join(GITLET_DIR, "HEAD");
    static final File MASTER = join(GITLET_DIR, "Master");
    static final File TRACKING = join(GITLET_DIR, "TRACKING");
    static final File STAGING = join(GITLET_DIR, "STAGING");
    static final File BRANCHES = join(GITLET_DIR, "BRANCHES");
    private static final FilenameFilter PLAIN_DIRS = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return !(new File(dir, name).isFile());
        }//changed for returning directory(added "!")
    };
    public static ArrayList<String> HEAD;
    public static ArrayList<branchHead> branches;
    public static String currentBranchMaster;
    public static ArrayList<stagedPair> STAGING_AREA;
    public static ArrayList<String> currentMasterTracked;
    public static ArrayList<String> removedFiles;

    public static void readConfig() {
        if (HEADfile.exists() && MASTER.exists() && TRACKING.exists() && STAGING.exists()) {
            HEAD = readObject(HEADfile, ArrayList.class);
            currentBranchMaster = readObject(MASTER, String.class);
            currentMasterTracked = readObject(TRACKING, ArrayList.class);
            STAGING_AREA = readObject(STAGING, ArrayList.class);
            branches=readObject(BRANCHES,ArrayList.class);
        }
    }

    public static void saveConfig() {
        if (HEADfile.exists() && MASTER.exists() && TRACKING.exists() && STAGING.exists()) {
            writeObject(HEADfile, HEAD);
            writeObject(MASTER, currentBranchMaster);
            writeObject(TRACKING, currentMasterTracked);
            writeObject(STAGING, STAGING_AREA);
            writeObject(BRANCHES,branches);
        }
    }

    /* TODO: fill in the rest of this class. */

    public static void getGlobalLog() {
        List<String> commitNames = plainFilenamesIn(GITLET_DIR);
        commitNames.sort(Comparator.naturalOrder());
        for (String x : new ArrayList<>(commitNames)) {
            File commitIter = join(GITLET_DIR, x, "data");
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
        List<String> commitNames = plainDirnamesIn(GITLET_DIR);

        for (String x : new ArrayList<>(commitNames)) {
            File commitIter = join(GITLET_DIR, x, "data");
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

    public static Commit getCurrentBranchMaster() {
        return readObject(join(GITLET_DIR, currentBranchMaster, "data"), Commit.class);
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

        currentBranchMaster = initCommit.getHashMetadata();
        HEAD.add(initCommit.getHashMetadata());
        branches = new ArrayList<>();
        branchHead branchMaster = new branchHead();
        branchMaster.branchName = "master";
        branchMaster.hash = initCommit.getHashMetadata();

        currentMasterTracked = new ArrayList<>();
        makeBranch("master");
        addCommit(initCommit);
        writeObject(BRANCHES, branches);
        writeObject(HEADfile, HEAD);
        writeObject(MASTER, currentBranchMaster);
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


            File currentMasterFile = join(Repository.GITLET_DIR, Repository.currentBranchMaster, arg);
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
                stagedPair x=new stagedPair();
                x.name=fs;
                try {
                    if (!currentMasterTracked.contains(fs)) {
                        currentMasterTracked.add(fs);
                    }
                    if(STAGING_AREA.contains(x))
                    {
                        String createFile = readContentsAsString(join(CWD, fs));
                        File newFile = join(f, fs);
                        newFile.createNewFile();
                        writeContents(newFile, createFile);
                    }else {
                        Commit perv=newCommit.getPervCommit();
                        String Content=readContentsAsString(join(GITLET_DIR, perv.getHashMetadata(),fs));
                        File newFile = join(f, fs);
                        newFile.createNewFile();
                        writeContents(newFile,Content);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        newCommit.tracked = currentMasterTracked;
        if (branches != null) {
            for(branchHead x:branches){
                if(x.hash.equals(currentBranchMaster)){
                    x.hash= newCommit.getHashMetadata();
                    break;
                }
            }

        }
        currentBranchMaster = newCommit.getHashMetadata();
        STAGING_AREA = new ArrayList<>();

        try {
            c.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        saveConfig();
        writeObject(c, newCommit);
        writeObject(MASTER, currentBranchMaster);
        writeObject(HEADfile, HEAD);
        writeObject(STAGING, STAGING_AREA);
        writeObject(TRACKING, currentMasterTracked);

    }

    public static void makeCommit(String message) {
        for (stagedPair x : STAGING_AREA) {
            if (x.markedToRemove) {
                currentMasterTracked.remove(x.name);
            }
        }

        Commit thisCommit = new Commit(message, currentMasterTracked);
        thisCommit.pervCommit.add(currentBranchMaster);
        if (!thisCommit.checkChanged()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        addCommit(thisCommit);
    }

    public static void makeBranch(String arg) {
        branchHead newBranch = new branchHead();
        newBranch.hash = getCurrentBranchMaster().getHashMetadata();
        newBranch.branchName = arg;
        currentBranchMaster = newBranch.hash;
        branches.add(newBranch);
        writeObject(BRANCHES, branches);
        saveConfig();
    }

    public static void status() {
        System.out.println("=== Branches ===\n");
        branches.sort(branchHead.BY_BRANCH_NAME);
        STAGING_AREA.sort(stagedPair.BY_NAME);
        for (branchHead x : branches) {
            if (Objects.equals(x.hash, currentBranchMaster)) {
                System.out.println("*");
                System.out.println(x.branchName);
                System.out.println("\n");
            } else {
                System.out.println(x.branchName);
                System.out.println("\n");
            }
        }
        System.out.println("\n");
        System.out.println("=== Staged Files ===\n");
        for (stagedPair x : STAGING_AREA) {
            System.out.println(x.name);
            System.out.println("\n");
        }
        System.out.println("\n");
        System.out.println("=== Removed Files ===\n");
        removedFiles.sort(Comparator.naturalOrder());
        for (String x : removedFiles) {
            System.out.println(x);
            System.out.println("\n");
        }
        System.out.println("\n");
        System.out.println("=== Modifications Not Staged For Commit ===\n");
        ArrayList<String> filesInCurrentCommit = (ArrayList<String>) plainFilenamesIn(join(GITLET_DIR, currentBranchMaster));
        ArrayList<String> filesInCWD = (ArrayList<String>) plainFilenamesIn(CWD);
        if (filesInCurrentCommit != null && filesInCWD != null) {
            for (String x : filesInCurrentCommit) {
                stagedPair InSTAGE = new stagedPair();
                InSTAGE.name = x;
                if (!filesInCWD.contains(x) && STAGING_AREA.contains(InSTAGE)) {
                    for (stagedPair k : STAGING_AREA) {
                        if (k.equals(InSTAGE) && k.markedToRemove == false) {
                            System.out.println(x);
                            System.out.println(" (deleted)\n");
                        }
                    }
                } else if (STAGING_AREA.contains(InSTAGE)) {
                    String fileInCommit = readContentsAsString(join(GITLET_DIR, currentBranchMaster, x));
                    String fileInCWD = readContentsAsString(join(CWD, x));
                    if (!sha1(fileInCWD).equals(sha1(fileInCommit))) {
                        System.out.println(x);
                        System.out.println(" (modified)\n");
                    }
                }
            }
            for (String x : filesInCWD) {
                if (!filesInCurrentCommit.contains(x)) {
                    System.out.println(x);
                    System.out.println(" (new)\n");
                } else {
                    String fileInCommit = readContentsAsString(join(GITLET_DIR, currentBranchMaster, x));
                    String fileInCWD = readContentsAsString(join(CWD, x));
                    if (!sha1(fileInCWD).equals(sha1(fileInCommit))) {
                        System.out.println(x);
                        System.out.println(" (modified)\n");
                    }
                }
            }
        }

        System.out.println("=== Untracked Files ===\n");
        if (filesInCWD != null) {
            for (String x : filesInCWD) {
                if (!currentMasterTracked.contains(x)) {
                    System.out.println(x);
                    System.out.println("\n");
                }
            }
        }
    }

    static List<String> plainDirnamesIn(File dir) {
        String[] files = dir.list(PLAIN_DIRS);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    static List<String> plainDirnamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    public static void checkOutFile(String commit, String arg) {
        if (!join(GITLET_DIR, commit).exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File fileToCheck = join(GITLET_DIR, commit, arg);
        if (!fileToCheck.exists()) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String content = readContentsAsString(fileToCheck);
        File resFile = join(CWD, arg);
        if (resFile.exists()) {
            writeObject(resFile, content);
        } else {
            try {
                resFile.createNewFile();
                writeObject(resFile, content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void checkOutAllFile(String name) {
        String commit = null;
        for (branchHead x : branches) {
            if (x.branchName.equals(name)) {
                commit = x.hash;
            }
        }

        if (commit == null) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (commit.equals(currentBranchMaster)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        File fileToCheck = join(GITLET_DIR, commit);
        Commit thisBranch = readObject(join(fileToCheck, "data"), Commit.class);
        ArrayList<String> filesInCommit = (ArrayList<String>) plainFilenamesIn(fileToCheck);
        for (String x : filesInCommit) {
            if (currentMasterTracked.contains(x)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String x : filesInCommit) {
            File CWDFILE = join(CWD, x);
            if (!CWDFILE.exists()) {
                try {
                    CWDFILE.createNewFile();
                    String content = readContentsAsString(join(fileToCheck, x));
                    writeObject(CWDFILE, content);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String content = readContentsAsString(join(fileToCheck, x));
                writeObject(CWDFILE, content);
            }
        }
        currentMasterTracked = thisBranch.tracked;
        currentBranchMaster = commit;
        STAGING_AREA = new ArrayList<>();
        saveConfig();
    }

    public static void rmBranch(String name) {

        for (branchHead x : branches) {
            if (x.branchName.equals(name)) {
                if (!x.hash.equals(currentBranchMaster)) {
                    branches.remove(x);
                    saveConfig();
                    System.exit(0);
                } else {
                    System.out.println("Cannot remove the current branch.");
                    System.exit(0);
                }
            }
        }
        System.out.println("A branch with that name does not exist.");
        System.exit(0);
    }

    public static void reset(String commitHash) {

        for (branchHead x : branches) {
            if (x.hash.equals(commitHash)) {
                x.hash = commitHash;
                checkOutAllFile(x.branchName);
                currentBranchMaster = commitHash;
                saveConfig();
                System.exit(0);
            }
        }
        System.out.println("No commit with that id exists.");


    }

    static class stagedPair implements Serializable {
        public static final Comparator<stagedPair> BY_NAME = Comparator.comparing(b -> b.name);
        String name;
        Boolean markedToRemove;

        @Override
        public boolean equals(Object o) {
            if (o instanceof stagedPair) {
                stagedPair k = (stagedPair) o;
                return this.name.equals(k.name);
            }
            if (o instanceof String) {
                return o.equals(name);
            }
            return false;
        }

    }

    static class branchHead implements Serializable {
        public static final Comparator<branchHead> BY_BRANCH_NAME = Comparator.comparing(b -> b.branchName);
        String hash;
        String branchName;

        @Override
        public boolean equals(Object o) {
            if (o instanceof branchHead) {
                branchHead k = (branchHead) o;
                return this.hash.equals(k.hash);
            }
            return false;
        }
    }
}
