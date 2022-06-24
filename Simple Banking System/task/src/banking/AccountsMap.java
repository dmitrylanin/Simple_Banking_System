package banking;

import java.util.HashMap;

public class AccountsMap {
    private static HashMap<Long, Account> accounts = new HashMap<>();

    public static HashMap<Long, Account> getAccounts() {
        return accounts;
    }

    public static void putNewAccount(Long key, Account value) {
        accounts.put(key, value);
    }

    public static Account getAccount(Long key){
        return accounts.get(key);
    }

    public static boolean isAccountNumberUnique(Long key){
        return accounts.containsKey(key);
    }
}
