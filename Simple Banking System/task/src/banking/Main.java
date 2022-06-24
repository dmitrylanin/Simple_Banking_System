package banking;

public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu(args[1]);
        menu.menuEngine();
    }
}