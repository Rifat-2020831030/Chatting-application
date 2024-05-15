public class Client {
    public static void main(String[] args) {
        File file1 = new FileSystem(100);
        File file2 = new FileSystem(250);
        File file3 = new FileSystem(193);

        Directory dir1 = new Directory();
        Directory dir2 = new Directory();
        Directory root = new Directory();

        dir1.addFile(file1);
        dir1.addFile(file2);
        dir2.addFile(file3);
        root.addFile(dir1);
        root.addFile(dir2);

        dir1.displaySize();
        dir2.displaySize();
        root.displaySize();

    }
}
