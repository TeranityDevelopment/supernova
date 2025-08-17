package supernova.util.concurrent;

import supernova.util.ImmutableTripletObject;
import supernova.util.Triplet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentTriplet<P, S, V> implements Triplet<P, S, V> {

    private final Map<P, ImmutableTripletObject<P, S, V>> primaryMap = new ConcurrentHashMap<>();
    private final Map<S, ImmutableTripletObject<P, S, V>> secondaryMap = new ConcurrentHashMap<>();

    @Override
    public int size() {
        return primaryMap.size();
    }

    @Override
    public boolean isEmpty() {
        return primaryMap.isEmpty();
    }

    @Override
    public void clear() {
        primaryMap.clear();
        secondaryMap.clear();
    }

    @Override
    public V put(P primaryKey, S secondaryKey, V value) {
        final ImmutableTripletObject<P, S, V> immutableTripletObject = new ImmutableTripletObject<>(primaryKey, secondaryKey, value);
        primaryMap.put(primaryKey, immutableTripletObject);
        secondaryMap.put(secondaryKey, immutableTripletObject);
        return value;
    }

    @Override
    public void remove(Object primaryOrSecondaryKey) {
        ImmutableTripletObject<P, S, V> obj = null;
        if (primaryMap.containsKey(primaryOrSecondaryKey)) {
            obj = primaryMap.remove(primaryOrSecondaryKey);
            if (obj != null) secondaryMap.remove(obj.secondaryKey());
        } else if (secondaryMap.containsKey(primaryOrSecondaryKey)) {
            obj = secondaryMap.remove(primaryOrSecondaryKey);
            if (obj != null) primaryMap.remove(obj.primaryKey());
        }
    }


    @Override
    public void remove(Object primaryKey, Object secondaryKey, Object value) {
        if (primaryMap.containsKey(primaryKey) && secondaryMap.containsKey(secondaryKey)) {
            final ImmutableTripletObject<P, S, V> immutableTripletObject = primaryMap.get(primaryKey);
            if (immutableTripletObject.value().equals(value)) {
                primaryMap.remove(primaryKey, immutableTripletObject);
                secondaryMap.remove(secondaryKey, immutableTripletObject);
            }
        }
    }

    @Override
    public boolean containsPrimary(Object primaryKey) {
        return primaryMap.containsKey(primaryKey);
    }

    @Override
    public boolean containsSecondary(Object secondaryKey) {
        return secondaryMap.containsKey(secondaryKey);
    }

    @Override
    public boolean containsValue(Object value) {
        return primaryMap.values().stream()
                .anyMatch(obj -> Objects.equals(obj.value(), value));
    }

    @Override
    public V get(Object primaryKey) {
        ImmutableTripletObject<P, S, V> obj = primaryMap.get(primaryKey);
        return obj != null ? obj.value() : null;
    }

    @Override
    public V get(Object primaryKey, Object secondaryKey) {
        final ImmutableTripletObject<P, S, V> immutableTripletObject = primaryMap.get(primaryKey);
        if (immutableTripletObject.secondaryKey().equals(secondaryKey)) {
            return immutableTripletObject.value();
        } return null;
    }

    @Override
    public Set<P> primaryKeySet() {
        return primaryMap.keySet();
    }

    @Override
    public Set<S> secondaryKeySet() {
        return secondaryMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return primaryMap.values().stream()
                .map(ImmutableTripletObject::value)
                .toList();
    }

    @Override
    public Collection<ImmutableTripletObject<P, S, V>> tripletObjectSet() {
        return primaryMap.values();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (ImmutableTripletObject<P, S, V> obj : primaryMap.values()) {
            sb.append("{")
                    .append(obj.primaryKey()).append(", ")
                    .append(obj.secondaryKey()).append("}=")
                    .append(obj.value())
                    .append(", ");
        }
        if (!primaryMap.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }
}