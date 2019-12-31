package org.romanchi.models;

import lombok.Data;

@Data
public class Wolf {

    private int TTL = 10 * 365;
    private int lastBreedIteration = 0;
    private int breedFrequency = 1;
    private int childAmount = 6;

    public boolean isAbleToBreed(int currentIteration) {
        return (0 < TTL && TTL < 8 * 365)
                && (currentIteration - lastBreedIteration > 365 * breedFrequency);
    }

    @Override
    public String toString() {
        return "W";
    }
}
