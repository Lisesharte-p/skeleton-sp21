package gitlet;

import java.util.Date;


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
        Repository.readConfig();
        switch (firstArg) {
            case "init":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.initCommit("initial commit");
                break;
            case "add":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.addFile(args[1]);
                break;
            case "commit":
                if (args.length == 1) {
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
                Repository.removeFile(args[1]);


                break;
            case "log":
                Commit currentCommit = Repository.getMaster();
                while (true) {
                    System.out.println("===");
                    String Hash = String.format("Commit %s", currentCommit.getHashMetadata());
                    System.out.println(Hash);
                    String date = String.format("Date: %s", currentCommit.date);
                    System.out.println(date);
                    System.out.println(currentCommit.getMessage());
                    System.out.println();
                    if (currentCommit.pervCommit.isEmpty()) {
                        break;
                    }
                    currentCommit = currentCommit.getPervCommit();
                }
                break;
            case "global-log":
                Repository.getGlobalLog();
                break;
            case "find":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.findCommit(args[1]);
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
