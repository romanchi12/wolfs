package org.romanchi.services.implementations;

import org.romanchi.models.*;
import org.romanchi.models.Map;
import org.romanchi.services.ModelationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class ModelationServiceImpl implements ModelationService {

    private final static Random random = new Random();
    private final static Logger logger = Logger.getLogger(ModelationServiceImpl.class.getName());

    @Value("${wolf.amount}")
    private Integer wolfAmount;
    @Value("${human.amount}")
    private Integer humanAmount;
    @Value("${rabbit.amount}")
    private Integer rabbitAmount;
    @Value("${cabbage.amount}")
    private Integer cabbageAmount;
    @Value("${tree.amount}")
    private Integer treeAmount;
    @Value("${map.dimension}")
    private Integer mapDimension;
    @Value("${app.mode.debug}")
    private boolean debugMode;

    private Map map;

    private void postConstruct(){
        map = Map.builder()
                .dimension(mapDimension)
                .wolvesAmount(wolfAmount)
                .humansAmount(humanAmount)
                .rabbitsAmount(rabbitAmount)
                .cabbagesAmount(cabbageAmount)
                .treesAmount(treeAmount)
                .debugMode(debugMode)
                .build();
    }

    @Override
    public void run() throws InterruptedException {

        postConstruct();

        map.initialize();
        int iteration = 0;
        int deltayear = 0;
        System.out.println("YEAR: " + (deltayear + 2019) + " " + map.getMetadata());
        while (iterate(iteration)){
            iteration++;
            if(iteration % 365 == 0 ){
                deltayear += 1;
                System.out.println("YEAR: " + (deltayear + 2019) + " " +map.getMetadata());
                Thread.sleep(1000);
            }
        }
        System.out.println("END OF GAME " + (deltayear + 2019) + " " +map.getMetadata());

    }

    public boolean iterate(int iteration){
        moveAll();
        calculateInteractions(iteration);
        return map.iterate(iteration);
    }

    private void calculateInteractions(int iteration) {
        for(int x = 0; x < map.getDimension(); x++){
            for(int y = 0; y < map.getDimension(); y++){
                List<Object> objectsInCell = map.get(x, y);
                if(objectsInCell.size() > 1){
                    interact(iteration, x, y, objectsInCell.get(0), objectsInCell.get(1));
                }
            }
        }
    }


    public boolean interact(int iteration, int x, int y, Object firstObject, Object secondObject){
         switch (map.fetchType(firstObject)){
             case HUMAN:{
                 Human human = (Human) firstObject;
                 switch (map.fetchType(secondObject)){
                     case WOLF:{
                         Wolf wolf = (Wolf) secondObject;
                         if(human.isAngry()){
                             map.remove(x, y, wolf);
                             map.setWolvesAmount(map.getWolvesAmount() - 1);
                             if(debugMode){
                                 System.out.println("Human eat wolf");
                             }
                         }else{
                             map.remove(x, y, human);
                             map.awareHumans(x, y);
                             map.setHumansAmount(map.getHumansAmount() - 1);
                             if(debugMode){
                                 System.out.println("Wolf eat human");
                             }
                         }
                         break;
                     }
                     case HUMAN:{
                         Human human2 = (Human) secondObject;
                         if(human.isAbleToBreed(iteration)
                                 && human2.isAbleToBreed(iteration)){
                             for(int i = 0; i < human.getChildAmount(); i++){
                                 Map.Point newPoint = map.getRandomEmptyPoint(x, y);
                                 if(newPoint == null){
                                     return false;
                                 }
                                 if(debugMode){
                                     System.out.println("Human is borned");
                                 }
                                 map.set(newPoint.getX(), newPoint.getY(), new Human());
                                 map.setHumansAmount(map.getHumansAmount() + 1);
                             }
                             human.setLastBreedIteration(iteration);
                             human2.setLastBreedIteration(iteration);
                         }
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
                             map.setWolvesAmount(map.getWolvesAmount() - 1);
                             if(debugMode){
                                 System.out.println("Human eat wolf");
                             }
                         }else{
                             map.remove(x, y, human);
                             map.awareHumans(x, y);
                             map.setHumansAmount(map.getHumansAmount() - 1);
                             if(debugMode){
                                 System.out.println("Wolf eat human");
                             }
                         }
                         break;
                     }
                     case RABBIT:{
                         Rabbit rabbit = (Rabbit) secondObject;
                         if(rabbit.isAngry()){
                             if(random.nextBoolean()){
                                 map.remove(x, y, rabbit);
                                 map.setRabbitsAmount(map.getRabbitsAmount() - 1);
                                 if(debugMode){
                                     System.out.println("Wolf eat rabbit");
                                 }
                             }
                         }else{
                             map.remove(x, y, rabbit);
                             map.setRabbitsAmount(map.getRabbitsAmount() - 1);
                             if(debugMode){
                                 System.out.println("Wolf eat rabbit");
                             }
                         }
                         break;
                     }
                     case TREE:{
                         Map.Point newPoint = map.getRandomSurroundingPoint(x, y);
                         map.set(newPoint.getX(), newPoint.getY(), wolf);
                         break;
                     }
                     case WOLF:{
                         Wolf wolf2 = (Wolf) secondObject;
                         if(wolf.isAbleToBreed(iteration)
                                 && wolf2.isAbleToBreed(iteration)){
                             for(int i = 0; i < wolf.getChildAmount(); i++){
                                 Map.Point newPoint = map.getRandomEmptyPoint(x, y);
                                 if(newPoint == null){
                                     return false;
                                 }
                                 if(debugMode){
                                     System.out.println("Wolf is borned");
                                 }
                                 map.set(newPoint.getX(), newPoint.getY(), new Wolf());
                                 map.setWolvesAmount(map.getWolvesAmount() + 1);
                             }
                             wolf.setLastBreedIteration(iteration);
                             wolf2.setLastBreedIteration(iteration);
                         }
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
                                 map.setRabbitsAmount(map.getRabbitsAmount() - 1);
                                 if(debugMode){
                                     System.out.println("Wolf eat rabbit");
                                 }
                             }
                         }else{
                             map.remove(x, y, rabbit);
                             map.setRabbitsAmount(map.getRabbitsAmount() - 1);
                             if(debugMode){
                                 System.out.println("Wolf eat rabbit");
                             }
                         }
                         break;
                     }
                     case CABBAGE:{
                         Cabbage cabbage = (Cabbage) secondObject;
                         map.remove(x, y, cabbage);
                         map.setCabbagesAmount(map.getCabbagesAmount() - 1);
                         if(debugMode){
                             System.out.println("Rabbit eat cabbage");
                         }
                         break;
                     }
                     case RABBIT:{
                         Rabbit rabbit2 = (Rabbit) secondObject;
                         if(rabbit.isAbleToBreed(iteration)
                                 && rabbit2.isAbleToBreed(iteration)){
                             for(int i = 0; i < rabbit.getChildAmount(); i++){
                                 Map.Point newPoint = map.getRandomEmptyPoint(x, y);
                                 if(newPoint == null){
                                     return false;
                                 }
                                 if(debugMode){
                                     System.out.println("Rabbit is borned");
                                 }
                                 map.set(newPoint.getX(), newPoint.getY(), new Rabbit());
                                 map.setRabbitsAmount(map.getRabbitsAmount() + 1);
                             }
                             rabbit.setLastBreedIteration(iteration);
                             rabbit2.setLastBreedIteration(iteration);
                         }
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
                         map.setCabbagesAmount(map.getCabbagesAmount() - 1);
                         if(debugMode){
                             System.out.println("Rabbit eat cabbage");
                         }
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
                         map.remove(x, y, wolf);
                         map.set(newPoint.getX(), newPoint.getY(), wolf);
                         break;
                     }
                 }
                 break;
             }
         }
         return true;
    }

    public void moveAll(){
        for(int x = 0; x < map.getDimension(); x++){
            for(int y = 0; y < map.getDimension(); y++){
                ListIterator iterator = map.get(x, y).listIterator();
                while(iterator.hasNext()) {
                    Object object = iterator.next();
                    if (Arrays.asList(ObjectType.HUMAN, ObjectType.RABBIT, ObjectType.WOLF)
                            .contains(map.fetchType(object))) {
                        Map.Point newPoint = map.getRandomSurroundingPoint(x, y);
                        map.remove(iterator, object);
                        map.set(newPoint.getX(), newPoint.getY(), object);
                    }
                }
            }
        }

    }
}
