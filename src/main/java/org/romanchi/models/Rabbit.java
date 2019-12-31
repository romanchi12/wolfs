package org.romanchi.models;

import lombok.Data;

@Data
public class Rabbit {

    private boolean angry = false;
    private int TTL = 6 * 365;
    private int lastBreedIteration = 0;
    private int breedFrequency = 1;
    private int childAmount = 8;

    public boolean isAbleToBreed(int currentIteration) {
        return (365 < TTL && TTL < 4 * 365)
                && (currentIteration - lastBreedIteration > 365 * breedFrequency);
    }

    @Override
    public String toString() {
        return "R";
    }
}
