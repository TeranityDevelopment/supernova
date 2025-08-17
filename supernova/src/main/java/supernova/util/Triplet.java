package supernova.util;

import java.util.Collection;
import java.util.Set;

/**
 * A class that represent as collection list that holds two pairs of
 * key and one value.
 *
 * @param <P> the type of the Primary Key
 * @param <S> the type of the Secondary Key
 * @param <V> the type of the Value
 */
public interface Triplet<P, S, V> {

    int size();

    boolean isEmpty();

    void clear();

    V put(P primaryKey, S secondaryKey, V value);

    void remove(Object primaryOrSecondaryKey);

    void remove(Object primaryKey, Object secondaryKey, Object value);

    boolean containsPrimary(Object primaryKey);

    boolean containsSecondary(Object secondaryKey);

    boolean containsValue(Object value);

    V get(Object primaryKey);

    V get(Object primaryKey, Object secondaryKey);

    Set<P> primaryKeySet();

    Set<S> secondaryKeySet();

    Collection<V> values();

    Collection<ImmutableTripletObject<P, S, V>> tripletObjectSet();

    String toString();
}
