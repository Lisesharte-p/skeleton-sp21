package gitlet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gitlet.Utils.*;


public class Repository {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    static final File HEADFILE = join(GITLET_DIR, "HEAD");
    static final File MASTER = join(GITLET_DIR, "Master");
    static final File TRACKING = join(GITLET_DIR, "TRACKING");
    static final File STAGING = join(GITLET_DIR, "STAGING");
    static final File BRANCHES = join(GITLET_DIR, "BRANCHES");
    static final File RMFILE = join(GITLET_DIR, "RMFILE");
    static final File STAGINGFOLDER = join(GITLET_DIR, "STAGEAREA");
    private static final FilenameFilter PLAIN_DIRS = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return !(new File(dir, name).isFile()) && !name.equals("STAGINGAREA");
        }
    };
    static ArrayList<String> HEAD;
    static ArrayList<String> currentMasterTracked;
    static ArrayList<String> removedFiles;
    static ArrayList<branchHead> branches;
    static branchHead currentBranchMaster;
    static ArrayList<stagedPair> STAGING_AREA;

    public static String checkShortUid(String shortUid) {
        List<String> temp = plainDirnamesIn(GITLET_DIR);
        if (shortUid.length() < 10) {
            if (temp != null) {
                for (String x : new ArrayList<>(temp)) {
                    if (x.contains(shortUid)) {
                        return x;
                    }
                }
            }
            return "-1";
        }
        return shortUid;
    }

    public static void readConfig() {
        if (HEADFILE.exists() && MASTER.exists() && TRACKING.exists() && STAGING.exists()) {
            HEAD = readObject(HEADFILE, ArrayList.class);
            currentBranchMaster = readObject(MASTER, branchHead.class);
            currentMasterTracked = readObject(TRACKING, ArrayList.class);
            STAGING_AREA = readObject(STAGING, ArrayList.class);
            branches = readObject(BRANCHES, ArrayList.class);
            removedFiles = readObject(RMFILE, ArrayList.class);

        }
    }

    public static void saveConfig() {
        if (HEADFILE.exists()
                && MASTER.exists()
                && TRACKING.exists()
                && STAGING.exists()
                && RMFILE.exists()) {
            writeObject(HEADFILE, HEAD);
            writeObject(MASTER, currentBranchMaster);
            writeObject(TRACKING, currentMasterTracked);
            writeObject(STAGING, STAGING_AREA);
            writeObject(BRANCHES, branches);
            writeObject(RMFILE, removedFiles);
        } else {
            System.out.print("Not in an initialized Gitlet directory.\n");
            System.exit(0);
        }
    }

    public static void getGlobalLog() {
        List<String> commitNames = plainDirnamesIn(GITLET_DIR);
        if (commitNames != null) {
            commitNames.sort(Comparator.naturalOrder());
        }

        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        ZoneId targetZone = ZoneId.of("Asia/Shanghai");

        if (commitNames != null) {
            for (String x : new ArrayList<>(commitNames)) {
                if (x.equals("STAGEAREA")) {
                    continue;
                }
                Commit currentCommit = readObject(
                        join(GITLET_DIR, x, "data"), Commit.class);
                System.out.println("===");
                String hash = String.format("commit %s", currentCommit.getHashMetadata());
                System.out.println(hash);
                if (currentCommit.isMerge) {
                    System.out.printf("Merge: %s %s%n",
                            currentCommit.pervCommit.get(0).substring(0, 7),
                            currentCommit.pervCommit.get(1).substring(0, 7));
                }
                Instant instant = currentCommit.date.toInstant();
                ZonedDateTime zonedDate = instant.atZone(targetZone);
                String formattedDate = dateFormatter.format(zonedDate);
                System.out.print("Date: ");
                System.out.println(formattedDate);
                System.out.println(currentCommit.getMessage());
                System.out.println();


            }
        }

    }

    public static void findCommit(String message) {
        List<String> commitNames = plainDirnamesIn(GITLET_DIR);
        int cnt = 0;
        if (commitNames != null) {

            for (String x : new ArrayList<>(commitNames)) {
                if (x.equals("STAGEAREA")) {
                    continue;
                }
                File commitIter = join(GITLET_DIR, x, "data");
                Commit currCommit = readObject(commitIter, Commit.class);
                if (currCommit.getMessage().equals(message)) {
                    cnt++;

                    String hash = String.format("%s", currCommit.getHashMetadata());
                    System.out.println(hash);


                }


            }
        }
        if (cnt == 0) {
            System.out.print("Found no commit with that message.");
        }
        System.exit(0);
    }

    public static Commit getCurrentBranchMaster() {
        return readObject(join(GITLET_DIR,
                currentBranchMaster.hash, "data"), Commit.class);
    }

    public static void initCommit(String message) {
        File newRepo = GITLET_DIR;
        if (newRepo.exists()) {
            System.out.println("A Gitlet version-control system already"
                    + " exists in the current directory.");
            return;
        }

        newRepo.mkdir();
        Commit initCommit = new Commit("initial commit", null);

        initCommit.date = new Date(0);
        initCommit.timeStamp = String.valueOf(new Time(0));
        initCommit.hashMetadata = sha1(initCommit.getMessage(), initCommit.timeStamp);
        currentMasterTracked = new ArrayList<>();
        currentBranchMaster = new branchHead();
        STAGING_AREA = new ArrayList<>();
        removedFiles = new ArrayList<>();
        HEAD = new ArrayList<>();

        try {
            MASTER.createNewFile();
            TRACKING.createNewFile();
            HEADFILE.createNewFile();
            STAGING.createNewFile();
            BRANCHES.createNewFile();
            RMFILE.createNewFile();
            STAGINGFOLDER.mkdir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        addCommit(initCommit);
        HEAD.add(initCommit.getHashMetadata());
        branches = new ArrayList<>();

        currentBranchMaster.hash = initCommit.getHashMetadata();
        currentBranchMaster.branchName = "master";
        currentMasterTracked = new ArrayList<>();

        makeBranch("master");
        saveConfig();

    }

    public static void removeFile(String arg) {
        stagedPair rmFile = new stagedPair();
        rmFile.name = arg;
        rmFile.markedToRemove = true;
        if (!STAGING_AREA.contains(rmFile) && !currentMasterTracked.contains(arg)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (join(STAGINGFOLDER, arg).exists()) {
            join(STAGINGFOLDER, arg).delete();
            STAGING_AREA.remove(rmFile);
            currentMasterTracked.remove(arg);
            saveConfig();
            System.exit(0);
        }
        if (Repository.currentMasterTracked.contains(arg)) {
            Repository.currentMasterTracked.remove(arg);
            File workingFile = join(Repository.CWD, arg);
            removedFiles.add(arg);
            STAGING_AREA.add(rmFile);
            workingFile.delete();
            saveConfig();

            System.exit(0);
        }

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


            File currentMasterFile = join(Repository.GITLET_DIR,
                    Repository.currentBranchMaster.hash, arg);
            File stageFile = join(STAGINGFOLDER, arg);
            String argContent = readContentsAsString(f);

            if (currentMasterFile.exists()) {

                String content = readContentsAsString(currentMasterFile);
                String currentMasterHash = sha1(content);
                if (removedFiles.contains(arg)) {
                    removedFiles.remove(arg);
                    currentMasterTracked.add(arg);
                }
                if (currentMasterHash.equals(sha1(argContent))) {


                    saveConfig();
//                    System.exit(0);
                }
            }
            if (stageFile.exists()) {
                String stageContent = readContentsAsString(stageFile);
                if (sha1(stageContent).equals(sha1(argContent))) {
                    for (stagedPair x : STAGING_AREA) {
                        if (x.equals(newFile)) {
                            if (x.markedToRemove) {
                                x.markedToRemove = false;
                                break;
                            } else {
                                STAGING_AREA.remove(x);
                                stageFile.delete();
                                break;
                            }
                        }
                    }

                } else {
                    writeContents(stageFile, argContent);
                }
                saveConfig();
//                System.exit(0);
            }
            if (!STAGING_AREA.contains(newFile)) {
                STAGING_AREA.add(newFile);
                try {
                    stageFile.createNewFile();
                    writeContents(stageFile, argContent);
                    saveConfig();
//                    System.exit(0);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } else {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        writeObject(STAGING, STAGING_AREA);
        writeObject(TRACKING, currentMasterTracked);
        saveConfig();
    }

    public static void addCommit(Commit newCommit) {
        File f = join(GITLET_DIR, newCommit.getHashMetadata());
        File c = join(GITLET_DIR, newCommit.getHashMetadata(), "data");


        f.mkdir();

        if (newCommit.files != null) {
            for (String fs : newCommit.files) {
                stagedPair x = new stagedPair();
                x.name = fs;
                try {
                    if (!currentMasterTracked.contains(fs)) {
                        currentMasterTracked.add(fs);
                    }
                    if (STAGING_AREA.contains(x)) {
                        String createFile = readContentsAsString(join(STAGINGFOLDER, fs));
                        File newFile = join(f, fs);
                        newFile.createNewFile();
                        writeContents(newFile, createFile);
                    } else {
                        Commit perv = newCommit.getPervCommit();
                        String content = readContentsAsString(join(GITLET_DIR,
                                perv.getHashMetadata(), fs));
                        File newFile = join(f, fs);
                        newFile.createNewFile();
                        writeContents(newFile, content);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        newCommit.tracked = new ArrayList<>(currentMasterTracked);
        if (branches != null) {
            for (branchHead x : branches) {
                if (x.hash.equals(currentBranchMaster.hash)) {
                    x.hash = newCommit.getHashMetadata();
                    break;
                }
            }

        }
        currentBranchMaster.hash = newCommit.getHashMetadata();
        STAGING_AREA = new ArrayList<>();
        removedFiles = new ArrayList<>();
        for (File x : STAGINGFOLDER.listFiles()) {
            x.delete();
        }
        try {
            c.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        saveConfig();
        writeObject(c, newCommit);
        writeObject(MASTER, currentBranchMaster);
        writeObject(HEADFILE, HEAD);
        writeObject(STAGING, STAGING_AREA);
        writeObject(TRACKING, currentMasterTracked);

    }

    public static void makeCommit(String message, boolean merge, String mergingCommit) {
        for (stagedPair x : STAGING_AREA) {
            if (x.markedToRemove) {
                currentMasterTracked.remove(x.name);
            }
        }
        Commit thisCommit = new Commit(message, currentMasterTracked);
        thisCommit.pervCommit.add(currentBranchMaster.hash);
        if (merge) {
            thisCommit.pervCommit.add(mergingCommit);
            thisCommit.isMerge = true;
        }
        if (!thisCommit.checkChanged()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        addCommit(thisCommit);
    }

    public static void makeBranch(String arg) {
        branchHead newBranch = new branchHead();
        newBranch.hash = currentBranchMaster.hash;
        newBranch.branchName = arg;
        for (branchHead x : branches) {
            if (x.branchName.equals(arg)) {
                System.out.println("A branch with that name already exists.");
                System.exit(0);
            }
        }
        branches.add(newBranch);
        saveConfig();
    }

    public static void status() {
        System.out.println("=== Branches ===");
        branches.sort(branchHead.BY_BRANCH_NAME);
        STAGING_AREA.sort(stagedPair.BY_NAME);
        for (branchHead x : branches) {
            if (Objects.equals(x.branchName, currentBranchMaster.branchName)) {
                System.out.print("*");
                System.out.println(x.branchName);
            } else {
                System.out.println(x.branchName);
            }
        }
        System.out.println("\n=== Staged Files ===");
        for (stagedPair x : STAGING_AREA) {
            if (!x.markedToRemove) {
                System.out.println(x.name);
            }
        }

        System.out.println("\n=== Removed Files ===");
        if (removedFiles != null) {
            removedFiles.sort(Comparator.naturalOrder());
            for (String x : removedFiles) {
                System.out.println(x);
            }
        }

        System.out.print("\n=== Modifications Not Staged For Commit ===\n");
        ArrayList<String> filesInCurrentCommit = new ArrayList<>(
                plainFilenamesIn(join(GITLET_DIR, currentBranchMaster.hash)));
        filesInCurrentCommit.remove("data");
        ArrayList<String> filesInCWD = new ArrayList<>(plainFilenamesIn(CWD));
        ArrayList<String> filesStaged = new ArrayList<>(plainFilenamesIn(STAGINGFOLDER));
        if (filesInCurrentCommit != null && filesInCWD != null) {
            for (String x : filesInCWD) {
                if (filesInCurrentCommit.contains(x)) {
                    String fileContent = readContentsAsString(join(GITLET_DIR,
                            currentBranchMaster.hash, x));
                    String fileInCWD = readContentsAsString(join(CWD, x));
                    if (!sha1(fileInCWD).equals(sha1(fileContent))) {
                        if (!filesStaged.contains(x)) {
                            System.out.printf("%s (modified)\n",x);
                            continue;
                        }
                    }
                }
                if (filesStaged.contains(x)) {
                    if (!join(CWD, x).exists()) {
                        System.out.printf("%s (deleted)\n",x);
                        continue;
                    }
                    String fileInCWD = readContentsAsString(join(CWD, x));
                    String fileStaged = readContentsAsString(join(STAGINGFOLDER, x));
                    if (!sha1(fileInCWD).equals(sha1(fileStaged))) {
                        System.out.printf("%s (modified)\n",x);
                        continue;
                    }
                }
                if (filesInCurrentCommit.contains(x) && !join(CWD, x).exists()) {
                    if (!removedFiles.contains(x)) {
                        System.out.printf("%s (deleted)\n",x);
                    }
                }
            }
        }
        System.out.println("\n=== Untracked Files ===");
        if (filesInCWD != null) {
            for (String x : filesInCWD) {
                if (!currentMasterTracked.contains(x) && !filesStaged.contains(x)) {
                    System.out.println(x);
                }
            }
        }
        System.out.print("\n");
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

        commit = checkShortUid(commit);

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
            writeContents(resFile, content);
        } else {
            try {
                resFile.createNewFile();
                writeContents(resFile, content);
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
        if (commit.equals(currentBranchMaster.hash) && name.equals(currentBranchMaster.branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        File fileToCheck = join(GITLET_DIR, commit);

        Commit thisBranch = readObject(join(fileToCheck, "data"), Commit.class);
        List<String> temp = plainFilenamesIn(fileToCheck);
        ArrayList<String> filesInCommit = new ArrayList<>(temp);
        filesInCommit.remove("data");
        for (String x : filesInCommit) {
            if (!currentMasterTracked.contains(x) && join(CWD, x).exists()) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String x : new ArrayList<>(plainFilenamesIn(CWD))) {
            if (!filesInCommit.contains(x) && currentMasterTracked.contains(x)) {
                join(CWD, x).delete();
            }
        }
        for (String x : filesInCommit) {
            File cwdFile = join(CWD, x);
            if (!cwdFile.exists()) {
                try {
                    cwdFile.createNewFile();
                    String content = readContentsAsString(join(fileToCheck, x));
                    writeContents(cwdFile, content);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String content = readContentsAsString(join(fileToCheck, x));
                writeContents(cwdFile, content);
            }
        }
        for (String x : new ArrayList<>(plainFilenamesIn(STAGINGFOLDER))) {
            join(STAGINGFOLDER, x).delete();
        }
        currentMasterTracked = thisBranch.tracked;
        currentBranchMaster.hash = commit;
        currentBranchMaster.branchName = name;
        STAGING_AREA = new ArrayList<>();
        saveConfig();
    }

    public static void rmBranch(String name) {
        for (branchHead x : branches) {
            if (x.branchName.equals(name)) {
                if (!x.branchName.equals(currentBranchMaster.branchName)) {
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
        commitHash = checkShortUid(commitHash);
        if(!join(GITLET_DIR,commitHash).exists()){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        for(branchHead x:branches){
            if(x.branchName.equals(currentBranchMaster.branchName)){

                x.hash=commitHash;
                checkOutAllFile(currentBranchMaster.branchName);
                saveConfig();
                System.exit(0);
            }
        }
    }

    public static void log() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(
                "EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        ZoneId targetZone = ZoneId.of("Asia/Shanghai");
        Commit currentCommit = Repository.getCurrentBranchMaster();
        while (true) {
            System.out.println("===");
            String Hash = String.format("commit %s", currentCommit.getHashMetadata());
            System.out.println(Hash);
            if (currentCommit.isMerge) {
                System.out.printf("Merge: %s %s%n",
                        currentCommit.pervCommit.get(0).substring(0, 7),
                        currentCommit.pervCommit.get(1).substring(0, 7));
            }
            Instant instant = currentCommit.date.toInstant();
            ZonedDateTime zonedDate = instant.atZone(targetZone);
            String formattedDate = dateFormatter.format(zonedDate);
            System.out.print("Date: ");
            System.out.println(formattedDate);
            System.out.println(currentCommit.getMessage());
            System.out.println();
            currentCommit = currentCommit.getPervCommit();
            if (currentCommit == null) {
                System.exit(0);
            }
        }
    }

    public static void merge(String branchName) {
        if (!STAGING_AREA.isEmpty()||!removedFiles.isEmpty()) {
            System.out.print("You have uncommitted changes.");
        }
        Commit givenBranch = null;
        String currentBranchMasterName = "";
        boolean found=false;
        for (branchHead x : branches) {
            if (x.branchName.equals(branchName)) {
                givenBranch = readObject(join(GITLET_DIR, x.hash, "data"), Commit.class);
                found=true;
            }
            if (x.hash.equals(currentBranchMaster.hash)) {
                currentBranchMasterName = x.branchName;
                if (currentBranchMasterName.equals(branchName)) {
                    System.out.print("Cannot merge a branch with itself.");
                    System.exit(0);
                }
            }
        }
        if(!found){
            System.out.print("A branch with that name does not exist.");
            System.exit(0);
        }
        Commit thisBranch = getCurrentBranchMaster();

        String lcaHash = getLCA(thisBranch, givenBranch);
        Commit LCA = readObject(join(GITLET_DIR, lcaHash, "data"), Commit.class);
        if (LCA.getHashMetadata().equals(thisBranch.getHashMetadata())) {
            System.out.print("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (LCA.getHashMetadata().equals(givenBranch.getHashMetadata())) {
            checkOutAllFile(branchName);
            System.out.print("Current branch fast-forwarded.");
            System.exit(0);
        }
        HashSet<String> k = new HashSet<>(givenBranch.files);
        k.addAll(thisBranch.files);
        k.addAll(LCA.files);
        currentMasterTracked = new ArrayList<>();
        boolean conflict = false;
        for (String x : k) {
            File thisFile = join(GITLET_DIR, thisBranch.getHashMetadata(), x);
            File givenFile = join(GITLET_DIR, givenBranch.getHashMetadata(), x);
            File LCAFile = join(GITLET_DIR, LCA.getHashMetadata(), x);
            File CWDFile = join(CWD, x);
            if (!thisFile.exists() && !givenFile.exists()) {
                continue;
            } else if (givenFile.exists()&& checkFileChanged(LCA, givenBranch, x) && !checkFileChanged(LCA, thisBranch, x)) {
                if (givenFile.exists()) {
                    checkOutFile(givenBranch.getHashMetadata(), x);
                    addFile(x);
                }
            }else if(!checkFileChanged(LCA, givenBranch, x) && checkFileChanged(LCA, thisBranch, x)){
                continue;
            }else if(!checkFileChanged(thisBranch,givenBranch,x)){
                continue;
            }else if(!LCAFile.exists() && !givenFile.exists() && thisFile.exists()){
                continue;
            }else if (!LCAFile.exists() && givenFile.exists() && !thisFile.exists()) {
                checkOutFile(givenBranch.getHashMetadata(), x);
                addFile(x);
            }  else if (!checkFileChanged(LCA, thisBranch, x) && !givenFile.exists()) {
                currentMasterTracked.remove(x);
                if (CWDFile.exists()) {
                    CWDFile.delete();
                }
            } else if (checkFileChanged(LCA, thisBranch, x) && checkFileChanged(LCA, givenBranch, x) && checkFileChanged(thisBranch, givenBranch, x)) {
                String a = "";
                String b = "";
                if (thisFile.exists()) {
                    a = readContentsAsString(thisFile);
                }
                if (givenFile.exists()) {
                    b = readContentsAsString(givenFile);
                }
                String newFileContent = String.format("<<<<<<< HEAD\n%s\n=======\n%s\n>>>>>>>", a, b);
                if (!CWDFile.exists()) {
                    try {
                        CWDFile.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                writeContents(CWDFile, newFileContent);
                conflict = true;
            }
        }

        makeCommit(String.format("Merged %s into %s.", branchName, currentBranchMasterName), true, givenBranch.getHashMetadata());

        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }

    }

    public static String getLCA(Commit A, Commit B) {
        ArrayList<String> pervOfA = new ArrayList<>();
        pervOfA.add(A.getHashMetadata());
        while (A.getPervCommit() != null) {
            A = A.getPervCommit();
            pervOfA.add(A.getHashMetadata());
        }
        while (B.getPervCommit() != null) {
            if (pervOfA.contains(B.getHashMetadata())) {
                return B.getHashMetadata();
            }
            B = B.getPervCommit();
        }
        return null;
    }

    public static boolean checkFileChanged(Commit A, Commit B, String fileName) {
        File AF = join(GITLET_DIR, A.getHashMetadata(), fileName);
        File BF = join(GITLET_DIR, B.getHashMetadata(), fileName);
        if (AF.exists() && BF.exists()) {
            return !sha1(readContentsAsString(AF)).equals(sha1(readContentsAsString(BF)));
        }
        if(!AF.exists()&&!BF.exists()){
            return false;
        }
        return true;
    }

    static class stagedPair implements Serializable {
        public static final Comparator<stagedPair> BY_NAME = Comparator.comparing(b -> b.name);
        String name;
        Boolean markedToRemove;
        String hash;

        stagedPair() {

        }

        stagedPair(String x) {
            name = x;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof stagedPair) {
                stagedPair k = (stagedPair) o;
                return this.name.equals(k.name);
            }
            if (o instanceof String) {
                return o.equals(name) && !this.markedToRemove;
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
                return this.hash.equals(k.hash) && this.branchName.equals(k.branchName);
            } else if (o instanceof String) {
                return this.branchName.equals(o);
            }
            return false;
        }

    }
}
