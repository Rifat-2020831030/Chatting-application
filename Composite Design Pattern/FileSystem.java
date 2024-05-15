public class FileSystem implements File{

    private float size;

    public FileSystem(float size) {
        this.size = size;
    }

    public float fileSize(){
        return size;
    }
    @Override
    public void displaySize() {
        System.out.println("Size of the file: "+size);
    }

}
