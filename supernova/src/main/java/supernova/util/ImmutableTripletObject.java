package supernova.util;

public record ImmutableTripletObject<P, S, V>(P primaryKey, S secondaryKey, V value) {
}
