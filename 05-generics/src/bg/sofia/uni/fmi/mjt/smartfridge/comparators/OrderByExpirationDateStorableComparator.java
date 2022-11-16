package bg.sofia.uni.fmi.mjt.smartfridge.comparators;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;

import java.util.Comparator;

public class OrderByExpirationDateStorableComparator implements Comparator<Storable> {

    @Override
    public int compare(Storable o1, Storable o2) {
        return o1.getExpiration().compareTo(o2.getExpiration());
    }
}
