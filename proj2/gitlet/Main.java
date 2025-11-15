package gitlet;

import java.util.Objects;

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
        if (args.length == 0) {
            System.out.print("Please enter a command.\n");

            return;
        }

        if (!Repository.GITLET_DIR.exists()&& !Objects.equals(args[0], "init")) {
            System.out.print("Not in an initialized Gitlet directory.");
            return;
        }else{
            Repository.readConfig();
        }
        String firstArg = args[0];

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
                if (args.length == 1 || Objects.equals(args[1], "")) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.makeCommit(args[1], false, null);
                break;
            case "rm":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.removeFile(args[1]);


                break;
            case "log":
                Repository.log();
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
                Repository.status();
                break;
            case "checkout":

                if (Objects.equals(args[1], "--")) {
                    if (args.length == 3) {
                        Repository.checkOutFile(Repository.currentBranchMaster.hash, args[2]);
                    } else {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                } else if (args.length == 4 && args[2].equals("--")) {
                    Repository.checkOutFile(args[1], args[3]);
                } else if (args.length == 2) {
                    Repository.checkOutAllFile(args[1]);
                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.makeBranch(args[1]);
                break;
            case "rm-branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                String arg = Repository.checkShortUid(args[1]);
                Repository.reset(arg);
                break;
            case "merge":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                Repository.merge(args[1]);
                break;

            default:
                System.out.println("No command with that name exists.");
        }
    }
}
