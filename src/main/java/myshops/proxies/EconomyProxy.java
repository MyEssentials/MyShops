package myshops.proxies;

import myshops.Config;
import mytown.core.utils.economy.Economy;

public class EconomyProxy {
    private static Economy economy = null;

    public static Economy economy() {
        if (economy == null) {
            economy = new Economy(Config.costItemName);
        }

        return economy;
    }
}
