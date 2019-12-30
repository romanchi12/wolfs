package org.romanchi.models;

import lombok.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@ToString
public class Map {

    private final static Random random = new Random();

    private Object[][] map;

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
        map = new Object[dimension][dimension];
    }

    @Builder
    public Map(int dimension,
               int wolvesAmount,
               int humansAmount,
               int rabbitsAmount,
               int cabbagesAmount,
               int treesAmount){
        if(wolvesAmount + humansAmount + rabbitsAmount + cabbagesAmount + treesAmount
                >= dimension * dimension){
            throw new IllegalArgumentException("Кількість об'єктів перевищує розмір поля.");
        }
        this.dimension = dimension;
        this.map = new Object[dimension][dimension];
        this.wolvesAmount = wolvesAmount;
        this.humansAmount = humansAmount;
        this.rabbitsAmount = rabbitsAmount;
        this.cabbagesAmount = cabbagesAmount;
        this.treesAmount = treesAmount;
    }

    public void initialize(){

        //trees
        for(int i = 0; i < treesAmount; i++){
            Point point = getRandomEmptyPoint();
            map[point.x][point.y] = new Tree();
        }

        //wolves
        for(int i = 0; i < wolvesAmount; i++){
            Point point = getRandomEmptyPoint();
            map[point.x][point.y] = new Wolf();
        }

        //humans
        for(int i = 0; i < humansAmount; i++){
            Point point = getRandomEmptyPoint();
            map[point.x][point.y] = new Human();
        }

        //rabbits
        for(int i = 0; i < rabbitsAmount; i++){
            Point point = getRandomEmptyPoint();
            map[point.x][point.y] = new Rabbit();
        }

        //cabbage
        for(int i = 0; i < cabbagesAmount; i++){
            Point point = getRandomEmptyPoint();
            map[point.x][point.y] = new Cabbage();
        }

    }

    public void set(int x, int y, Object object){
        map[x][y] = object;
    }

    public Object get(int x, int y){
        return map[x][y];
    }

    private Point getRandomEmptyPoint(){
        Point point = new Point();
        int iteration = 0;
        do{
            if(iteration > dimension * dimension - 1){
                return null;
            }
            point.x = random.nextInt(dimension);
            point.y = random.nextInt(dimension);
            iteration++;
        }while(map[point.x][point.y] != null);

        return point;
    }

    public boolean iterate(int iterationIndex){
        for(int x = 0; x < dimension; x++){
            for(int y = 0; y < dimension; y++){
                ObjectType type = fetchType(map[x][y]);
                switch (type){
                    case HUMAN:{
                        Human human = (Human) map[x][y];
                        if(human.isAbleToBreed(iterationIndex)){
                            for(int i = 0; i < human.getChildAmount(); i++){
                                Point point = getRandomEmptyPoint();
                                if(point == null){
                                    return false;
                                }
                                map[point.x][point.y] = new Human();
                                humansAmount += 1;
                            }
                            human.setLastBreedIteration(iterationIndex);
                        }
                        if(human.getTTL() < 1){
                            map[x][y] = null;
                            humansAmount -= 1;
                            System.out.println("HUMAN DIED");
                        }else{
                            human.setTTL(human.getTTL() - 1);
                        }

                        break;
                    }
                    case TREE:{
                        Tree tree = (Tree) map[x][y];
                        if(tree.isAbleToBreed(iterationIndex)){
                            for(int i = 0; i < tree.getChildAmount(); i++){
                                Point point = getRandomEmptyPoint();
                                if(point == null){
                                    return false;
                                }
                                map[point.x][point.y] = new Tree();
                                treesAmount += 1;
                            }
                            tree.setLastBreedIteration(iterationIndex);
                        }
                        if(tree.getTTL() < 1){
                            map[x][y] = null;
                            treesAmount -= 1;
                            System.out.println("TREE DIED");
                        }else{
                            tree.setTTL(tree.getTTL() - 1);
                        }
                        break;
                    }
                    case RABBIT:{
                        Rabbit rabbit = (Rabbit) map[x][y];
                        if (rabbit.isAbleToBreed(iterationIndex)) {
                            for(int i = 0; i < rabbit.getChildAmount(); i++){
                                Point point = getRandomEmptyPoint();
                                if(point == null){
                                    return false;
                                }

                                map[point.x][point.y] = new Rabbit();
                                rabbitsAmount += 1;
                            }
                            rabbit.setLastBreedIteration(iterationIndex);
                        }
                        if(rabbit.getTTL() < 1){
                            map[x][y] = null;
                            rabbitsAmount -= 1;
                            System.out.println("RABBIT DIED");
                        }else{
                            rabbit.setTTL(rabbit.getTTL() - 1);
                        }
                        break;
                    }
                    case CABBAGE:{
                        Cabbage cabbage = (Cabbage) map[x][y];
                        if (cabbage.isAbleToBreed(iterationIndex)) {
                            for(int i = 0; i < cabbage.getChildAmount(); i++){
                                Point point = getRandomEmptyPoint();
                                if(point == null){
                                    return false;
                                }

                                map[point.x][point.y] = new Cabbage();
                                cabbagesAmount += 1;
                            }
                            cabbage.setLastBreedIteration(iterationIndex);
                        }
                        if(cabbage.getTTL() < 1){
                            map[x][y] = null;
                            cabbagesAmount -= 1;
                            System.out.println("CABBAGE DIED");
                        }else{
                            cabbage.setTTL(cabbage.getTTL() - 1);
                        }
                        break;
                    }
                    case WOLF:{
                        Wolf wolf = (Wolf) map[x][y];
                        if (wolf.isAbleToBreed(iterationIndex)) {
                            for(int i = 0; i < wolf.getChildAmount(); i++){
                                Point point = getRandomEmptyPoint();
                                if(point == null){
                                    return false;
                                }

                                map[point.x][point.y] = new Wolf();
                                wolvesAmount += 1;
                            }
                            wolf.setLastBreedIteration(iterationIndex);
                        }
                        if(wolf.getTTL() < 1){
                            map[x][y] = null;
                            wolvesAmount -= 1;
                            System.out.println("WOLF DIED");
                        }else{
                            wolf.setTTL(wolf.getTTL() - 1);
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    public List<Point> getSurroundingPoints(int x, int y){
        List<Point> points = new LinkedList<>();

        Point p1 = new Point();
        p1.x = x - 1;
        p1.y = y;

        Point p2 = new Point();
        p2.x = x + 1;
        p2.y = y;

        Point p3 = new Point();
        p3.x = x;
        p3.y = y - 1;

        Point p4 = new Point();
        p4.x = x;
        p4.y = y + 1;

        if(p1.isInsideMap()){
            points.add(p1);
        }
        if(p2.isInsideMap()){
            points.add(p2);
        }
        if(p3.isInsideMap()){
            points.add(p3);
        }
        if(p4.isInsideMap()){
            points.add(p4);
        }

        return points;
    }

    public Point getRandomFreeSurroundingPoint(int x, int y){
        List<Point> candidates = new LinkedList<>();

        for(Point point : getSurroundingPoints(x, y)){
            if(map[point.x][point.y] == null){
                candidates.add(point);
            }
        }

        return candidates.size() != 0 ? candidates.get(random.nextInt(candidates.size())) : null;
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


    public String getMetadata(){
        return "HUMANS=" + humansAmount + " WOLVES=" + wolvesAmount + " RABBITS=" + rabbitsAmount +
                " CABBAGES=" + cabbagesAmount + " TREES=" + treesAmount;
    }

}
