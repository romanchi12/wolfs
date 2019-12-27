package org.romanchi.models;

import lombok.Data;

@Data
public class Tree {

    private int TTL = 100 * 365;
    private int childAmount = 5;
    private int lastBreedIteration = 0;
    private int breedFrequency = 1;

    public boolean isAbleToBreed(int currentIteration) {
        return (0 < TTL && TTL < 95 * 365)
                && (currentIteration - lastBreedIteration > 365 * breedFrequency) ;
    }


    @Override
    public String toString() {
        return "T";
    }
}
