public class Main {
    public static void main(String[] args) {

        Proxy proxyShell = new Proxy();
        proxyShell.run("sudo shutdown -r now", "user");
        proxyShell.run("rm -rf", "user");
        proxyShell.run("touch text.c++", "user");
        proxyShell.run("rm -rf", "admin");
        
    }
}
