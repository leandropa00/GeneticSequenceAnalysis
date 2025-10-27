package master.ucaldas;

import master.ucaldas.facade.MenuFacade;

public class Main {
    public static void main(String[] args) {
        MenuFacade menuFacade = new MenuFacade();
        menuFacade.start();
    }
}
