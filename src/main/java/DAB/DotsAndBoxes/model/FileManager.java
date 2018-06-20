package DAB.DotsAndBoxes.model;

import java.io.*;

/**
 * This class is used to save all current data for the game, it is called from Game class
 */
public class FileManager {

    /**
     * Searchs the path for the resources directory
     *
     * @return Directory path
     */
    private static String getResourcesDirectory() {
        File resourcesDirectory = new File(System.getProperty("user.dir") + "/target");
        return resourcesDirectory.getAbsoluteFile().toString();
    }

    /**
     * Saves an object on a file
     *
     * @param object   Object to save
     * @param fileName File name
     * @throws IOException
     */
    static void writeToFile(Object object, String fileName) throws IOException {
        String resourceDirectory = getResourcesDirectory();
        ObjectOutputStream outStream = null;
        outStream = new ObjectOutputStream(new FileOutputStream(resourceDirectory + "/" + fileName + ".game"));
        outStream.writeObject(object);
        if (outStream != null)
            outStream.close();
    }

    /**
     * Searchs an object on a file
     *
     * @param fileName File name
     * @return Object on the file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    static Object readFromFile(String fileName) throws IOException, ClassNotFoundException {
        String resourceDirectory = getResourcesDirectory();
        String filePath = resourceDirectory + "/" + fileName + ".game";
        Object obj = null;
        if (new File(filePath).length() != 0) {
            ObjectInputStream inputStream;
            inputStream = new ObjectInputStream(new FileInputStream(filePath));
            obj = inputStream.readObject();
            inputStream.close();
        }
        return obj;
    }
}