import java.util.ArrayList;
import java.util.List;

public class Directory implements File {
    private float size;
    private List<File> childrens;


    public Directory() {
        this.size = 0;
        childrens = new ArrayList<>();
    }

    public void addFile(File file){
        childrens.add(file);
    }

    @Override
    public void displaySize() {
        size = 0;
        for (File component : childrens) {
            size += component.fileSize();
        }
        System.out.println("Directory size: "+ size);
    }

    @Override
    public float fileSize() {
        size = 0;
        for (File component : childrens) {
            size += component.fileSize();
        }
        return size;
    }

}
