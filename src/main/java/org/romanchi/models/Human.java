package org.romanchi.models;

import lombok.Data;

@Data
public class Human {
    private boolean angry = false;
    private int TTL = 70 * 365;
    private int lastBreedIteration = 0;
    private int breedFrequency = 1;
    private int childAmount = 2;

    public boolean isAbleToBreed(int currentIteration) {
        return (35 * 365 < TTL && TTL < 50 * 365)
                && (currentIteration - lastBreedIteration > 365 * breedFrequency);
    }

    @Override
    public String toString() {
        return "H";
    }
}
