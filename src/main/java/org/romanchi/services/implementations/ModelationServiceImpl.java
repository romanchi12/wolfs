package org.romanchi.services.implementations;

import org.romanchi.models.*;
import org.romanchi.services.ModelationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.romanchi.models.ObjectType.*;

@Service
public class ModelationServiceImpl implements ModelationService {

    private final static Random random = new Random();
    private final static Logger logger = Logger.getLogger(ModelationServiceImpl.class.getName());

    private final Map map = Map.builder()
                                .dimension(10)
                                .wolvesAmount(10)
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
        while (iterate(iteration)){
            iteration++;
            if(iteration % 365 == 0 ){
                deltayear += 1;
                System.out.println("YEAR: " + (deltayear + 2019));
                System.out.println(map.getMetadata());
                Thread.sleep(1000);
            }
        }
    }

    public boolean iterate(int iteration){
        for(int x = 0; x < map.getDimension(); x++){
            for(int y = 0; y < map.getDimension(); y++){
                processCell(x, y);
                if(!map.iterate(iteration)){
                    System.out.println("END OF GAME");
                }
            }
        }
        return true;
    }

    public void processCell(int x, int y){
        switch (map.fetchType(map.get(x, y))){
            case WOLF:{
                List<Map.Point> surroundingPoints = map.getSurroundingPoints(x, y);
                for(Map.Point point : surroundingPoints){
                    ObjectType objectType = map.fetchType(map.get(point.getX(), point.getY()));
                    if(objectType == ObjectType.HUMAN){
                        Human human = (Human) map.get(point.getX(), point.getY());
                        if(human.isAngry()){
                            map.set(x, y, null);
                            map.setWolvesAmount(map.getWolvesAmount() - 1);
                            System.out.println("Human eat wolf");
                        }else{
                            map.set(point.getX(), point.getY(), null);
                            map.setHumansAmount(map.getHumansAmount() - 1);
                            System.out.println("Wolf eat human");
                            for(Map.Point pnt : surroundingPoints){
                                if(pnt.equals(point)){
                                    continue;
                                }
                                if(map.fetchType(map.get(pnt.getX(), pnt.getY()))
                                        .equals(ObjectType.HUMAN)){
                                    Human witness = (Human) map.get(pnt.getX(), pnt.getY());
                                    witness.setAngry(true);
                                }
                            }
                        }
                    }else if(objectType == ObjectType.RABBIT){
                        Rabbit rabbit = (Rabbit) map.get(point.getX(), point.getY());
                        map.set(point.getX(), point.getY(), null);
                        map.setRabbitsAmount(map.getRabbitsAmount() - 1);

                        System.out.println("Wolf eat rabbit");
                    }
                }
                break;
            }
            case HUMAN:{
                Human human = (Human) map.get(x, y);
                List<Map.Point> surroundingPoints = map.getSurroundingPoints(x, y);
                for(Map.Point point : surroundingPoints){
                    ObjectType objectType = map.fetchType(map.get(point.getX(), point.getY()));
                    if(objectType == ObjectType.WOLF) {
                        if(human.isAngry()){
                            map.set(point.getX(), point.getY(), null);
                            map.setWolvesAmount(map.getWolvesAmount() - 1);
                            System.out.println("Human eat wolf");
                        }else{
                            map.set(x, y, null);
                            map.setHumansAmount(map.getHumansAmount() - 1);
                            System.out.println("Wolf eat human");

                            for(Map.Point pnt : surroundingPoints){
                                if(pnt.equals(point)){
                                    continue;
                                }
                                if(map.fetchType(map.get(pnt.getX(), pnt.getY()))
                                        .equals(ObjectType.HUMAN)){
                                    Human witness = (Human) map.get(pnt.getX(), pnt.getY());
                                    witness.setAngry(true);
                                }
                            }

                        }
                    }
                }
                break;
            }
            case RABBIT:{
                List<Map.Point> surroundingPoints = map.getSurroundingPoints(x, y);
                for(Map.Point point : surroundingPoints){
                    ObjectType objectType = map.fetchType(map.get(point.getX(), point.getY()));
                    if(objectType == ObjectType.WOLF) {
                        if(random.nextBoolean()){
                            map.set(x, y, null);
                            map.setRabbitsAmount(map.getRabbitsAmount() - 1);
                            System.out.println("Wolf eat rabbit");
                        }
                    }
                }
                break;
            }
        }
    }
}
