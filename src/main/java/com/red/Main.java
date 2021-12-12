package com.red;

import java.lang.reflect.Array;
import java.util.*;

public class Main {


    private static int fin,counter,cities;

    private static boolean check_zero(ArrayList<Integer> journey,int[][] distance){ //new part
        for(int i=0;i<journey.size()-1;i++){
            if(distance[journey.get(i)][journey.get(i+1)]==0)
                return true;
        }
        return false;
    }

    private static int[][] init(int cities,ArrayList<Integer> towns){
        int[][] arr = new int[cities][cities];
        for (int[] line: arr) Arrays.fill(line, 0);
        for(int i=0;i<towns.size();i+=3){
            arr[towns.get(i)][towns.get(i+1)] = towns.get(i+2);
            arr[towns.get(i+1)][towns.get(i)] = arr[towns.get(i)][towns.get(i+1)];
        }
        return arr;
    }

    private static int[][] rand_initialize(int cities,int max_dist,boolean mode,ArrayList<Integer> towns){
        Random random = new Random();
        int[][] arr = new int[cities][cities];
        if(mode) {
            for (int i = 0; i < cities; i++) {
                for (int j = i; j < cities; j++) {
                    if (i == j) arr[i][j] = 0;
                    else {
                        arr[i][j] = random.nextInt(max_dist) + 1;
                        arr[j][i] = arr[i][j];
                    }
                }
            }
        }else{ //new part
            for (int[] line: arr) Arrays.fill(line, 0);
            for(int i=0;i<towns.size();i+=2){
                arr[towns.get(i)][towns.get(i+1)] = random.nextInt(max_dist) + 1;
                arr[towns.get(i+1)][towns.get(i)] = arr[towns.get(i)][towns.get(i+1)];
            }
        }///
        return arr;
    }


    private static int fitness(ArrayList<Integer> journey,int[][] distance){
        int all = 0;
 //       int fin = 0;
        for(int i=0;i<journey.size();i++)
            for(int j=i;j<journey.size();j++)
                fin+=distance[i][j];

        for (int i=0;i<journey.size()-1;i++) all+=distance[journey.get(i)][journey.get(i+1)];

        return fin-all;
    }

    private static int[] selection(int[] fitness){
        int total=0;
        int[] result = new int[fitness.length];
        for(int i=0;i<fitness.length;i++) total+=fitness[i];
        Random random = new Random();
        for (int i = 0; i< fitness.length;i++){
            int rand = random.nextInt(total+1);
            int temp = 0;
            for(int j=0;j<fitness.length;j++){
                temp+= fitness[j];
                if(rand<=temp) {
                    result[i] = j;
                    break;
                }
            }
        }
        return result;
    }

//    private static ArrayList<Integer>[] crossover(ArrayList<Integer>[] journeys) {
//        Random random = new Random();
//        int count = 0;
//        ArrayList<Integer>[] child_journey = new ArrayList[journeys.length/2];
//        for(int i=0;i<journeys.length/2;i++) child_journey[i] = new ArrayList<>();
//        for (int j = 0; j < journeys.length-1; j+=2) {
//            int r1 = random.nextInt(journeys[j].size()), r2 = random.nextInt(journeys[j].size());
//            if (r1 > r2) {
//                int tmp = r2;
//                r2 = r1;
//                r1 = tmp;
//            }
//            for(int i =0;i<journeys[j].size();i++){
//                if(i>=r1&&i<=r2) child_journey[j-count].add(journeys[j].get(i));
//                else child_journey[j-count].add(journeys[j+1].get(i));
//            }
//            count++;
//        }
//        return child_journey;
//    }


    private static ArrayList<Integer>[] crossover(ArrayList<Integer>[] journeys) {
        Random random = new Random();
        boolean add = true;
        ArrayList<Integer>[] child_journey = new ArrayList[journeys.length];
        for(int i=0;i<journeys.length;i++) child_journey[i] = new ArrayList<>();
        for (int j = 0; j < journeys.length-1; j+=2) {
            ArrayList<Integer>[] journeys_f = new ArrayList[2];
            for (int i = 0; i < journeys_f.length; i++) journeys_f[i] = new ArrayList<>(journeys[j+i]);
            if (!journeys[j].equals(journeys[j + 1])){
                int r1 = random.nextInt(journeys[j].size()), r2 = random.nextInt(journeys[j].size());
            if (r1 > r2) {
                int tmp = r2;
                r2 = r1;
                r1 = tmp;
            }
            ArrayList<Integer>[] mut_tmp = new ArrayList[2];
            for (int i = 0; i < mut_tmp.length; i++) mut_tmp[i] = new ArrayList<>();
            ArrayList<Integer>[] ad_mut_tmp = new ArrayList[2];
            for (int i = 0; i < ad_mut_tmp.length; i++) ad_mut_tmp[i] = new ArrayList<>();
            for (int i = r1; i <= r2; i++) {
                mut_tmp[0].add(journeys_f[0].get(i));
                mut_tmp[1].add(journeys_f[1].get(i));
            }
            //i'm not proud of this
            for(int i=0;i<mut_tmp[0].size();i++){
                for(int k=0;k<mut_tmp[1].size();k++){
                    if(mut_tmp[0].get(i).equals(mut_tmp[1].get(k))) add = false;
                }
                if(add) ad_mut_tmp[0].add(mut_tmp[0].get(i));
                add = true;
            }
            for(int i=0;i<mut_tmp[1].size();i++){
                for(int k=0;k<mut_tmp[0].size();k++){
                    if(mut_tmp[1].get(i).equals(mut_tmp[0].get(k))) add = false;
                }
                if(add) ad_mut_tmp[1].add(mut_tmp[1].get(i));
                add = true;
            }
            //
            for (int i = 0; i < journeys_f[0].size(); i++) {
                for (int k = 0; k < mut_tmp[0].size(); k++) {
                    if (journeys_f[0].get(i)==mut_tmp[1].get(k)) journeys_f[0].set(i, -1);
                    if (journeys_f[1].get(i)==mut_tmp[0].get(k)) journeys_f[1].set(i, -1);
                }
            }
            for (int i = 0; i < journeys_f[0].size(); i++) {
                if (i == r1 - 1||(i == r1&&r1 == 0)) {
                    for (int k = 0; k < ad_mut_tmp[0].size(); k++) {
                        child_journey[j].add(ad_mut_tmp[0].get(k));
                        child_journey[j + 1].add(ad_mut_tmp[1].get(k));
                    }
                }
                if (i >= r1 && i <= r2) {
                    child_journey[j].add(mut_tmp[1].get(i - r1));
                    child_journey[j + 1].add(mut_tmp[0].get(i - r1));
                } else {
                    if (journeys_f[0].get(i)!=-1) child_journey[j + 1].add(journeys_f[0].get(i));
                    if (journeys_f[1].get(i)!=-1) child_journey[j].add(journeys_f[1].get(i));
                }
            }
        }
            else {
                child_journey[j] = journeys_f[0];
                child_journey[j+1] = journeys_f[1];
            }
        }
        return child_journey;

    }


    private static boolean check_rep(ArrayList<Integer> journey){
        Set<Integer> set = new HashSet<>(journey);
        if(journey.size()==set.size()) return false;
        else return true;
    }

    private static ArrayList<Integer>[] mutation(ArrayList<Integer>[] journeys,int[][] distance){
        Random random = new Random();
        ArrayList<Integer> tmp = journeys[0];;
        double mutation_rate = 0.744;
        int i = 0;
            while(i<journeys.length) {
                double mutation = random.nextDouble();
                if (mutation < mutation_rate) {
                    Collections.swap(journeys[i], random.nextInt(journeys[i].size()), random.nextInt(journeys[i].size()));
                }
              //  if (!check_zero(journeys[i], distance)){
                    i++;
            //    }

        }

        return journeys;
    }

    private static ArrayList<Integer>[] init_col(int gen,int cities,int[][] distance) {
        ArrayList<Integer> journey = new ArrayList<>();
        ArrayList<Integer>[] journey_col = new ArrayList[gen];
        for(int i = 0;i<journey_col.length;i++) journey_col[i] = new ArrayList<>();
        for (int i = 0; i < cities; i++) journey.add(i);
        for (int j = 0; j < gen; j++) {
            boolean check = true;
            while (check) {
                check = false;
                Collections.shuffle(journey);
                if (check_zero(journey, distance)) check = true;
            }
            for(int i = 0;i<journey.size();i++) journey_col[j].add(journey.get(i));
        }
        return journey_col;
    }

    private static ArrayList<Integer>[] gen_simple(int gen,int cities,int[][] distance,ArrayList<Integer>[] journey_col) {
            ArrayList<Integer>[] next_journey_col = new ArrayList[gen];
            int[] fitness = new int[gen];
            for (int i = 0; i < gen; i++) fitness[i] = fitness(journey_col[i], distance);
            int[] fitnessed = selection(fitness);
            for (int i = 0; i < gen; i++) next_journey_col[i] = journey_col[fitnessed[i]];

            return mutation(crossover(next_journey_col),distance);
    }

    private static ArrayList<Integer> gen_solution(int gen,int cities,int[][] distance,ArrayList<Integer>[] journey_col,int init,ArrayList<Integer> local_best) {
        //  ArrayList<Integer> journey = new ArrayList<>();
        while (true) {
            ArrayList<Integer>[] next_journey_col = new ArrayList[gen];
            int[] fitness = new int[gen];
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < gen; i++) fitness[i] = fitness(journey_col[i], distance);
            int[] fitnessed = selection(fitness);
            for (int i = 0; i < gen; i++) next_journey_col[i] = journey_col[fitnessed[i]];

            if(next_journey_col.length>1) journey_col = mutation(crossover(next_journey_col), distance);
            else journey_col = next_journey_col;
            if(journey_col.length<=1) gen = 1;
            else gen /=2;
            if (counter < 4) {
            for (int i = 0; i < gen; i++) {
                if (min > Math.abs(fitness(next_journey_col[i], distance) - fin)) {
                    min = Math.abs(fitness(next_journey_col[i], distance) - fin);
                    local_best = next_journey_col[i];
                }
            }
            if(min<=init) {
                init=min;
                counter--;
            }else if(min>=init) counter++;
                } else return local_best;

                return gen_solution(gen, cities, distance, journey_col, init,local_best);
        }
    }

    private static ArrayList<Integer> min_sim(int gen, int cities, int[][] distance){
        int min = Integer.MAX_VALUE,iter = 0;
        ArrayList<Integer> fittest = new ArrayList<>();
        ArrayList<Integer>[] tmp = init_col(gen,cities,distance);
        while (counter<cities*10) {
            iter++;
            int tmp_min = min;
            ArrayList<Integer>[] journey_col = gen_simple(gen, cities, distance, tmp);
            for (int i = 0; i < journey_col.length; i++) {
                if (Math.abs(fitness(journey_col[i], distance) - fin) < min && !check_rep(journey_col[i])&& !check_zero(journey_col[i],distance)) {
                    min = Math.abs(fitness(journey_col[i], distance) - fin);
                //      System.out.println("MIN - " + min);
                    fittest = journey_col[i];
                    counter=0;
                }

            }
            if(tmp_min==min) counter++;
            tmp = journey_col;
        }
        System.out.println("Iterations - " + iter);
        return fittest;
    }

    private static ArrayList<Integer> init_2d_space(){
        ArrayList<Integer> towns = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of cities - ");
        cities = scanner.nextInt();
        int count = 0 ;
        double[] coord = new double[cities*2];
        for(int i=0;i<coord.length;i+=2){
            System.out.println("Enter the coordinates of the city № " + count);
            System.out.println("X - ");
            coord[i] = scanner.nextDouble();
            System.out.println("Y - ");
            coord[i+1] = scanner.nextDouble();
            count++;
        }
      for(int i=0;i<cities;i++){
          while (true){
              int tmp = 0,dist = 0;
              System.out.println("Enter the number of the city you want to connect with the city number " + i + " \n(if you don't want to connect anything with this city enter -1) " );
              tmp = scanner.nextInt();
              if(tmp==-1) break;
              towns.add(i);
              towns.add(tmp);
              dist = (int)Math.sqrt(Math.pow(coord[tmp*2]-coord[i*2],2)+Math.pow(coord[tmp*2+1]-coord[i*2+1],2));
              System.out.println(dist);
              towns.add(dist);
          }
      }
      return towns;
    }

    public static void main(String[] args) {
        cities = 6;
        counter=0;
       // ArrayList<Integer> towns_manual = init_2d_space();
        ArrayList<Integer> towns_with_distance = new ArrayList<>(Arrays.asList(0,1,48,0,2,27,1,2,41,1,3,34,1,4,12,2,3,55,3,4,22,4,5,16,0,1,48));///
        ArrayList<Integer> towns = new ArrayList<>(Arrays.asList(1,2,5,4,5,0,2,3,0,1));///
       int[][] distance = init(cities,towns_with_distance);
       // int[][] distance = init(cities,towns_manual);
      //  int[][] distance = rand_initialize(cities,100,true,towns);
        for(int i=0;i<cities;i++) {
            for (int j = 0; j < cities; j++) {
                System.out.print(distance[i][j] + " ");
            }
            System.out.println();
        }
       // ArrayList<Integer> shortest = gen_solution(10,cities,distance,init_col(10,cities,distance),Integer.MAX_VALUE,init_best);
        ArrayList<Integer> shortest = min_sim(60,cities,distance); //for init exact
        System.out.println("Path length: " + Math.abs(fitness(shortest,distance)-fin) + "\nPath: " + shortest);

    }
}
