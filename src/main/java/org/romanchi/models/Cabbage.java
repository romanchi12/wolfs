package org.romanchi.models;

import lombok.Data;

@Data
public class Cabbage {

    private int TTL = 2 * 365;
    private int childAmount = 20;
    private int lastBreedIteration = 0;
    private int breedFrequency = 1;

    public boolean isAbleToBreed(int currentIteration) {
        return (0 < TTL && TTL < 365)
                && (currentIteration - lastBreedIteration > 365 * breedFrequency) ;
    }

    @Override
    public String toString() {
        return "C";
    }
}
