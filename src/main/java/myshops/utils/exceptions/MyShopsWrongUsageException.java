package myshops.utils.exceptions;

import myshops.proxies.LocalizationProxy;
import net.minecraft.command.WrongUsageException;

/**
 * @author Joe Goett
 */
public class MyShopsWrongUsageException extends WrongUsageException {
    public MyShopsWrongUsageException(String key, Object... args) {
        super(LocalizationProxy.getLocalization().getLocalization(key, args));
    }
}
