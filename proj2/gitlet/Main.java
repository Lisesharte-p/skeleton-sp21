package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;


/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) return;
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.init("initial commit");
                break;
            case "add":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                File f = new File(args[1]);
                if (f.exists()) {
                    if(!Repository.tracked.found(args[1])){
                        Repository.tracked.addLast(args[1]);
                    }
                    String buffer=readContentsAsString(f);
                    File addFile = join(Repository.STAGING_AREA, args[1]);
                    String currentMasterHash = sha1(join(Repository.GITLET_DIR,Repository.master.getHashMetadata(),args[1]));
                    if (currentMasterHash.equals(sha1(f))) {
                        System.exit(0);
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
                break;
            case "commit":
                if(args.length==1){
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.makeCommit(args[1]);
                break;
            case "rm":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                File fileToRemove = join(Repository.STAGING_AREA, args[1]);
                if(fileToRemove.exists()){
                    fileToRemove.delete();
                }
                int index=Repository.tracked.getIndex(args[1]);
                if(index!=-1){
                    Repository.tracked.remove(index);
                    File CWDfile=join(Repository.CWD,args[1]);
                    CWDfile.delete();
                }


                break;
            case "log":
                break;
            case "global-log":
                break;
            case "find":
                break;
            case "status":
                break;
            case "checkout":
                break;
            case "branch":
                break;
            case "rm-branch":
                break;
            case "reset":
                break;
            case "merge":


            default:
                System.out.println("No command with that name exists.");
        }
    }
}
