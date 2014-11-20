import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleDatabase implements Database {
    private final Map<String, String> database;
    private final Map<String, Integer> valueFrequency;
    // name => (transactionNumber => value)
    private Map<String, Map<Integer, String>> transactions;
    private Map<String, Integer> transactionValueFrequency;
    private int openTransactionCount;

    public SimpleDatabase() {
        this.database = Maps.newHashMap();
        this.valueFrequency = Maps.newHashMap();
        this.transactions = Maps.newHashMap();
        this.transactionValueFrequency = Maps.newHashMap();
    }

    @Override
    public void set(String name, String value) {
        update(name, value);
    }

    @Override
    public String get(String name) {
        if (transactionPending() && transactions.containsKey(name)) {
            return getLast(transactions.get(name)).getValue();
        }

        return database.get(name);
    }

    @Override
    public void unset(String name) {
        update(name, null);
    }

    @Override
    public int numEqualTo(String value) {
        Integer databaseFreq = valueFrequency.containsKey(value) ? valueFrequency.get(value) : 0;
        Integer transactionFreq = transactionPending() && transactionValueFrequency.containsKey(value) ? transactionValueFrequency.get(value) : 0;
        return databaseFreq + transactionFreq;
    }

    @Override
    public void beginTransaction() {
        openTransactionCount += 1;
    }

    /* Close all open transaction blocks, returning false if there is no transaction */
    @Override
    public boolean commit() {
        if (!transactionPending()) {
            return false;
        }

        // reset
        openTransactionCount = 0;

        for (String name : transactions.keySet()) {
            Entry<Integer, String> entry = getLast(transactions.get(name));
            update(name, entry.getValue());
        }

        transactions = Maps.newHashMap();
        transactionValueFrequency = Maps.newHashMap();
        return true;
    }

    /* Roll back most recent open transaction block, returning false if there is no transaction */
    @Override
    public boolean rollBack() {
        if (!transactionPending()) {
            return false;
        }

        // remove current transaction
        for (String name : transactions.keySet()) {
            Map<Integer, String> map = transactions.get(name);
            // if name was updated in this block, remove
            if (map.containsKey(openTransactionCount)) {
                String value = map.remove(openTransactionCount);
                if (map.isEmpty()) {
                    transactions.remove(name);
                }

                // merge frequency with outer transaction block, if no transaction, merge with
                // main database
                updateFrequency(value, get(name));
            }
        }

        openTransactionCount -= 1;

        return true;
    }

    private boolean transactionPending() {
        return openTransactionCount > 0;
    }

    private void update(String name, String newValue) {
        String currentValue = get(name);
        // no change
        if (currentValue.equals(newValue)) {
            return;
        }

        if (transactionPending()) {
            Map<Integer, String> map = transactions.get(name);
            if (map == null) {
                map = Maps.newHashMap();
            }
            map.put(openTransactionCount, newValue);
            transactions.put(name, map);
        } else if (newValue == null) {
            database.remove(name);
        } else {
            database.put(name, newValue);
        }

        updateFrequency(currentValue, newValue);
    }

    /* delete: valueToIncrement is null and valueToDecrement is the deleted value
    *  update:  we reduce the count of the old value and increment the count of the new value
    *  transaction: if value not in transactionFreq, will be added;
    * */
    private void updateFrequency(String valueToDecrement, String valueToIncrement) {
        Map<String, Integer> frequencyMap = transactionPending() ? transactionValueFrequency : valueFrequency;

        if (valueToDecrement != null) {
            Integer freq = frequencyMap.get(valueToDecrement);
            if (freq == null) {
                freq = 0;
            }
            frequencyMap.put(valueToDecrement, freq - 1);
        }

        if (valueToIncrement != null) {
            Integer freq = frequencyMap.get(valueToIncrement);
            if (freq == null) {
                freq = 0;
            }
            frequencyMap.put(valueToIncrement, freq + 1);
        }
    }

    private Entry<Integer, String> getLast(Map<Integer, String> integerStringMap) {
        Iterator<Entry<Integer, String>> iter = integerStringMap.entrySet().iterator();
        Entry<Integer, String> result = null;
        while (iter.hasNext()) {
            result = iter.next();
        }

        return result;
    }
}
