import java.util.ArrayList;
import java.util.List;

public class Proxy implements Command{
    private Implementation power;
    private List<String> adminCmnd;
    
    public Proxy() {
        power = new Implementation();
        adminCmnd = new ArrayList<>();
        adminCmnd.add("rm -rf");
        adminCmnd.add("sudo usermod -aG sudo user");
        adminCmnd.add("sudo -l -U user");
        adminCmnd.add("sudo shutdown -r now");
    }

    @Override
    public void run(String command, String role) {
        if(adminCmnd.contains(command) && role != "admin"){
            System.out.println("Can't run command: " + command);
        }
        else{
            power.run(command,role);
        }
    }

}
