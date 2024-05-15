public class Implementation implements Command{

    @Override
    public void run(String command, String role) {
        System.out.println(role + " runs the command: " + command + " successfully");
    }

}
