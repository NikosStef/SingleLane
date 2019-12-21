import java.util.Scanner;

public class Main {

    //Plithos Aftokinitwn apo kathe plevra.
    private static int maxCars = 4;

    public static void main(String[] args){

        //Epilogi senariou me tin xrisi tis vivliothikis scanner.
        System.out.println("Choose scenario from 1 to 5.\n");
        Scanner scanner = new Scanner(System.in);
        String scenario = scanner.nextLine();

        //Se periptosi pou epilexthei to senario 5 (Leitourgia Fanariou), o arithmos twn aftokinitwn kathe plevras ginete isos me 12.
        if(scenario.equals("5"))
            maxCars = 12;

        //Dimiourgia kai arxikopioish.
        Bridge singleBridge = new Bridge(scenario);
        Thread[] red = new Thread[maxCars];
        Thread[] blue = new Thread[maxCars];

        for(int i=0; i<maxCars; i++){
            red[i] = new Thread(new RedCar(singleBridge, i, scenario));
            blue[i] = new Thread(new BlueCar(singleBridge, i, scenario));
        }

        //Ekkinisi twn thread.
        for(int i=0; i<maxCars; i++){
            red[i].start();
            blue[i].start();

        }

    }
}
