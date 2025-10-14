package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/**
 * A repository for Capers
 *
 * @author TODO
 * The structure of a Capers Repository is as follows:
 * <p>
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 * - dogs/ -- folder containing all of the persistent data for dogs
 * - story -- file containing the current story
 * <p>
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /**
     * Current Working Directory.
     */
    static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * Main metadata folder.
     */
    static final File CAPERS_FOLDER = Utils.join(System.getProperty("user.dir"), ".capers"); // TODO Hint: look at the `join`
    //      function in Utils
    static final File story = join(System.getProperty("user.dir"), ".capers", "story");
    static final File dogs = join(System.getProperty("user.dir"), ".capers", "dogs");

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     * <p>
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     * - dogs/ -- folder containing all of the persistent data for dogs
     * - story -- file containing the current story
     */
    public static void setupPersistence() {
        //done

        CAPERS_FOLDER.mkdir();

        dogs.mkdir();
        try {
            story.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     *
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        String thisFile = readContentsAsString(join(System.getProperty("user.dir"), ".capers", "story"));

        File f = join(System.getProperty("user.dir"), ".capers", "story");
        if (!thisFile.isEmpty()) {
            thisFile += "\n";
        }

        thisFile += text;
        writeContents(f, thisFile);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        Dog newDog = new Dog(name, breed, age);
        newDog.saveDog();
        System.out.println(newDog);
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     *
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        Dog thisDog = Dog.fromFile(name);
        thisDog.haveBirthday();
        thisDog.saveDog();
    }
}
