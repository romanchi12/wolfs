package org.romanchi.models;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

@ToString
public class Map {

    private final static Random random = new Random();

    private List<Object>[][] map;

    @Getter
    private int dimension;
    @Getter @Setter
    private int wolvesAmount;
    @Getter @Setter
    private int humansAmount;
    @Getter @Setter
    private int rabbitsAmount;
    @Getter @Setter
    private int cabbagesAmount;
    @Getter @Setter
    private int treesAmount;
    private boolean debugMode;

    @Getter
    @EqualsAndHashCode
    public class Point{
        int x;
        int y;

        public boolean isInsideMap(){
            return (0 < x && x < dimension && 0 < y && y < dimension);
        }
    }


    public Map(int dimension){
        this.dimension = dimension;
        map = new List[dimension][dimension];
    }

    @Builder
    public Map(int dimension,
               int wolvesAmount,
               int humansAmount,
               int rabbitsAmount,
               int cabbagesAmount,
               int treesAmount,
               boolean debugMode){
        if(wolvesAmount + humansAmount + rabbitsAmount + cabbagesAmount + treesAmount
                >= dimension * dimension){
            throw new IllegalArgumentException("Кількість об'єктів перевищує розмір поля.");
        }
        this.dimension = dimension;
        this.map = new List[dimension][dimension];
        this.wolvesAmount = wolvesAmount;
        this.humansAmount = humansAmount;
        this.rabbitsAmount = rabbitsAmount;
        this.cabbagesAmount = cabbagesAmount;
        this.treesAmount = treesAmount;
        this.debugMode = debugMode;
    }

    public void initialize(){
        for(int x = 0; x < dimension; x++){
            for(int y = 0; y < dimension; y++){
                map[x][y] = new LinkedList<>();
            }
        }
        //trees
        for(int i = 0; i < treesAmount; i++){
            Point point = getRandomEmptyPoint();
            set(point.getX(), point.getY(), new Tree());
        }

        //wolves
        for(int i = 0; i < wolvesAmount; i++){
            Point point = getRandomEmptyPoint();
            set(point.getX(), point.getY(), new Wolf());
        }

        //humans
        for(int i = 0; i < humansAmount; i++){
            Point point = getRandomEmptyPoint();
            set(point.getX(), point.getY(), new Human());
        }

        //rabbits
        for(int i = 0; i < rabbitsAmount; i++){
            Point point = getRandomEmptyPoint();
            set(point.getX(), point.getY(), new Rabbit());
        }

        //cabbage
        for(int i = 0; i < cabbagesAmount; i++){
            Point point = getRandomEmptyPoint();
            set(point.getX(), point.getY(), new Cabbage());
        }

    }

    public void set(int x, int y, Object object){
        map[x][y].add(object);
    }

    //public void set(ListIterator iterator, object){}

    public List<Object> get(int x, int y){
        return map[x][y];
    }

    public void remove(int x, int y, Object object) {
        map[x][y].remove(object);
    }

    public void remove(ListIterator iterator,Object object) {
        iterator.remove();
    }

    public Point getRandomEmptyPoint(){
        Point point = new Point();
        int iteration = 0;
        do{
            if(iteration > dimension * dimension - 1){
                return null;
            }
            point.x = random.nextInt(dimension);
            point.y = random.nextInt(dimension);
            iteration++;
        }while(map[point.x][point.y].size() != 0);

        return point;
    }

    public Point getRandomEmptyPoint(int x, int y){
        Point point = new Point();
        int iteration = 0;
        do{
            if(iteration > dimension * dimension){
                return null;
            }
            point.x = random.nextInt(dimension);
            point.y = random.nextInt(dimension);
            iteration++;
        }while(map[point.x][point.y].size() != 0 || (point.x == x && point.y == y));

        return point;
    }

    public boolean iterate(int iterationIndex){
        for(int x = 0; x < dimension; x++){
            for(int y = 0; y < dimension; y++){
                ListIterator iterator = map[x][y].listIterator();
                while(iterator.hasNext()){
                    Object object = iterator.next();
                    switch (fetchType(object)){
                        case HUMAN:{
                            Human human = (Human) object;
                            if(human.getTTL()  < 1){
                                remove(iterator, human);
                                humansAmount -= 1;
                                if(debugMode){
                                    System.out.println("Human died");
                                }
                            }else{
                                human.setTTL(human.getTTL() - 1);
                            }
                            break;
                        }
                        case WOLF:{
                            Wolf wolf = (Wolf) object;
                            if(wolf.getTTL()  < 1){
                                remove(iterator, wolf);
                                wolvesAmount -= 1;
                                if(debugMode){
                                    System.out.println("Wolf died");
                                }
                            }else{
                                wolf.setTTL(wolf.getTTL() - 1);
                            }
                            break;
                        }
                        case RABBIT:{
                            Rabbit rabbit = (Rabbit) object;

                            if(rabbit.getTTL()  < 1){
                                remove(iterator, rabbit);
                                rabbitsAmount -= 1;
                                if(debugMode){
                                    System.out.println("Rabbit died");
                                }
                            }else{
                                rabbit.setTTL(rabbit.getTTL() - 1);
                            }
                            break;
                        }
                        case CABBAGE:{
                            Cabbage cabbage = (Cabbage) object;
                            if(cabbage.isAbleToBreed(iterationIndex)){
                                for(int i = 0; i < cabbage.getChildAmount(); i++){
                                    Point point = getRandomEmptyPoint(x, y);
                                    if(point == null){
                                        return false;
                                    }
                                    set(point.x, point.y, new Cabbage());
                                    cabbagesAmount += 1;
                                }
                                cabbage.setLastBreedIteration(iterationIndex);
                            }

                            if(cabbage.getTTL()  < 1){
                                remove(iterator, cabbage);
                                cabbagesAmount -= 1;
                                if(debugMode){
                                    System.out.println("Cabbage died");
                                }
                            }else{
                                cabbage.setTTL(cabbage.getTTL() - 1);
                            }
                            break;
                        }
                        case TREE:{
                            Tree tree = (Tree) object;
                            if(tree.isAbleToBreed(iterationIndex)){
                                for(int i = 0; i < tree.getChildAmount(); i++){
                                    Point point = getRandomEmptyPoint(x, y);
                                    if(point == null){
                                        return false;
                                    }
                                    set(point.x, point.y, new Tree());
                                    treesAmount += 1;
                                }
                                tree.setLastBreedIteration(iterationIndex);
                            }

                            if(tree.getTTL()  < 1){
                                remove(iterator, tree);
                                treesAmount -= 1;
                                if(debugMode){
                                    System.out.println("Tree died");
                                }
                            }else{
                                tree.setTTL(tree.getTTL() - 1);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }

    public List<Point> getSurroundingPoints(int x, int y){
        List<Point> points = new LinkedList<>();

        for(int x_ = x - 1; x_ < x + 2; x_++){
            for(int y_ = y - 1; y_ < y + 2; y_++){
                if(x_ == x && y_ == y){
                    continue;
                }
                Point p = new Point();
                p.x = x_;
                p.y = y_;
                if(p.isInsideMap()){
                    points.add(p);
                }
            }
        }

        return points;
    }

    public List<Point> getZone(int x, int y){
        List<Point> points = new LinkedList<>();

        for(int x_ = x - 2; x_ < x + 3; x_++){
            for(int y_ = y - 2; y_ < y + 3; y_++){
                if(x_ == x && y_ == y){
                    continue;
                }
                Point p = new Point();
                p.x = x_;
                p.y = y_;
                if(p.isInsideMap()){
                    points.add(p);
                }
            }
        }

        return points;
    }

    public Point getRandomSurroundingPoint(int x, int y){
        List<Point> surroundingPoints = getSurroundingPoints(x, y);

        return surroundingPoints.get(random.nextInt(surroundingPoints.size()));
    }

    public ObjectType fetchType(Object object){
        if(object == null){
            return ObjectType.NULL;
        }

        Class objectClass = object.getClass();

        if(objectClass.equals(Wolf.class)){
            return ObjectType.WOLF;
        }else if(objectClass.equals(Human.class)){
            return ObjectType.HUMAN;
        }else if(objectClass.equals(Rabbit.class)){
            return ObjectType.RABBIT;
        }else if(objectClass.equals(Cabbage.class)){
            return ObjectType.CABBAGE;
        }else if(objectClass.equals(Tree.class)){
            return ObjectType.TREE;
        }

        return null;
    }


    public void awareHumans(int x, int y) {
        List<Point> zone = getZone(x, y);
        for(Point point : zone){
            if(map[point.x][point.y].size() > 0){
                map[point.x][point.y].forEach(object -> {
                    if(fetchType(object) == ObjectType.HUMAN){
                        ((Human)object).setAngry(true);
                    }
                });
            }
        }
    }

    public void awareRabbits(int x, int y){
        List<Point> zone = getSurroundingPoints(x, y);
        for(Point point : zone){
            if(map[point.x][point.y].size() > 0){
                map[point.x][point.y].forEach(object -> {
                    if(fetchType(object) == ObjectType.RABBIT){
                        ((Rabbit)object).setAngry(true);
                    }
                });
            }
        }
    }

    public String getMetadata(){
        return "HUMANS=" + humansAmount + " WOLVES=" + wolvesAmount + " RABBITS=" + rabbitsAmount +
                " CABBAGES=" + cabbagesAmount + " TREES=" + treesAmount;
    }

}
