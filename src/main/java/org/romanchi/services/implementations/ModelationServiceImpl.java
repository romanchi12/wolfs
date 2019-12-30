package org.romanchi.services.implementations;

import org.romanchi.models.*;
import org.romanchi.models.Map;
import org.romanchi.services.ModelationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;
import org.romanchi.models.ObjectType.*;

@Service
public class ModelationServiceImpl implements ModelationService {

    private final static Random random = new Random();
    private final static Logger logger = Logger.getLogger(ModelationServiceImpl.class.getName());

    private final Map map = Map.builder()
                                .dimension(100)
                                .wolvesAmount(3)
                                .humansAmount(10)
                                .rabbitsAmount(5)
                                .cabbagesAmount(5)
                                .treesAmount(5)
                                .build();

    @Override
    public void run() throws InterruptedException {
        map.initialize();
        int iteration = 0;
        int deltayear = 0;
        System.out.println("YEAR: " + (deltayear + 2019));
        while (iterate(iteration)){
            iteration++;
            if(iteration % 365 == 0 ){
                deltayear += 1;
                System.out.println("YEAR: " + (deltayear + 2019));
                System.out.println(map.getMetadata());
                Thread.sleep(1000);
            }
        }
        System.out.println("END OF GAME");

    }

    public boolean iterate(int iteration){
        moveAll();
        calculateInterractions();
        return map.iterate(iteration);
    }

    private void calculateInterractions() {
        for(int x = 0; x < map.getDimension(); x++){
            for(int y = 0; y < map.getDimension(); y++){
                List<Object> objectsInCell = map.get(x, y);
                if(objectsInCell.size() > 1){
                    interract(x, y, objectsInCell.get(0), objectsInCell.get(1));
                }
            }
        }
    }


    public void interract(int x, int y, Object firstObject, Object secondObject){
         switch (map.fetchType(firstObject)){
             case HUMAN:{
                 Human human = (Human) firstObject;
                 switch (map.fetchType(secondObject)){
                     case WOLF:{
                         Wolf wolf = (Wolf) secondObject;
                         if(human.isAngry()){
                             map.remove(x, y, wolf);
                             System.out.println("Human eat wolf");
                         }else{
                             map.remove(x, y, human);
                             map.awareHumans(x, y);
                             System.out.println("Wolf eat human");
                         }
                         break;
                     }
                     default:{
                         break;
                     }
                 }
                 break;
             }
             case WOLF:{
                 Wolf wolf = (Wolf) firstObject;
                 switch (map.fetchType(secondObject)){
                     case HUMAN:{
                         Human human = (Human) secondObject;
                         if(human.isAngry()){
                             map.remove(x, y, wolf);
                             System.out.println("Human eat wolf");
                         }else{
                             map.remove(x, y, human);
                             map.awareHumans(x, y);
                             System.out.println("Wolf eat human");
                         }
                         break;
                     }
                     case RABBIT:{
                         Rabbit rabbit = (Rabbit) secondObject;
                         if(rabbit.isAngry()){
                             if(random.nextBoolean()){
                                 map.remove(x, y, rabbit);
                                 System.out.println("Wolf eat rabbit");
                             }
                         }else{
                             map.remove(x, y, rabbit);
                             System.out.println("Wolf eat rabbit");
                         }
                         break;
                     }
                     case TREE:{
                         Map.Point newPoint = map.getRandomSurroundingPoint(x, y);
                         map.set(newPoint.getX(), newPoint.getY(), wolf);
                         break;
                     }
                 }
                 break;
             }
             case RABBIT:{
                 Rabbit rabbit = (Rabbit) firstObject;
                 switch (map.fetchType(secondObject)){
                     case WOLF:{
                         Wolf wolf = (Wolf) secondObject;
                         if(rabbit.isAngry()){
                             if(random.nextBoolean()){
                                 map.remove(x, y, rabbit);
                                 System.out.println("Wolf eat rabbit");
                             }
                         }else{
                             map.remove(x, y, rabbit);
                             System.out.println("Wolf eat rabbit");
                         }
                         break;
                     }
                     case CABBAGE:{
                         Cabbage cabbage = (Cabbage) secondObject;
                         map.remove(x, y, cabbage);
                         System.out.println("Rabbit eat cabbage");
                         break;
                     }
                 }
                 break;
             }
             case CABBAGE:{
                 Cabbage cabbage = (Cabbage) firstObject;
                 switch (map.fetchType(secondObject)){
                     case RABBIT:{
                         Rabbit rabbit = (Rabbit) secondObject;
                         map.remove(x, y, cabbage);
                         System.out.println("Rabbit eat cabbage");
                         break;
                     }
                 }
                 break;
             }
             case TREE:{
                 Tree tree = (Tree) firstObject;
                 switch (map.fetchType(secondObject)){
                     case WOLF:{
                         Wolf wolf = (Wolf) secondObject;
                         Map.Point newPoint = map.getRandomSurroundingPoint(x, y);
                         map.set(newPoint.getX(), newPoint.getY(), wolf);
                         break;
                     }
                 }
                 break;
             }
         }
    }

    public void moveAll(){
        for(int x = 0; x < map.getDimension(); x++){
            for(int y = 0; y < map.getDimension(); y++){
                for (Object object : map.get(x, y)) {
                    if (Arrays.asList(ObjectType.HUMAN, ObjectType.RABBIT, ObjectType.WOLF)
                            .contains(map.fetchType(object))) {
                        Map.Point newPoint = map.getRandomSurroundingPoint(x, y);
                        map.set(newPoint.getX(), newPoint.getY(), object);
                    }
                }
            }
        }

    }


    public void processCell(int x, int y){
        switch (map.fetchType(map.get(x, y))){
            case WOLF:{

                break;
            }
            case HUMAN:{

                break;
            }
            case RABBIT:{
                break;
            }
        }
    }
}
