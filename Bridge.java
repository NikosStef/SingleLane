import java.util.concurrent.Semaphore;

public class Bridge {

    private Semaphore BridgeSem;
    private Semaphore TrafficLightSem;
    private int BlueCounter;
    private int RedCounter;
    private volatile int PreviousCar;
    private long PreviousCarStartTime;
    private long GreenLightTime;
    private static final int Red = 0;
    private String Scenario;
    private volatile int AcceptableColor;

    //Constructor tis klassis.
    Bridge(String Scenario) {
        BridgeSem = new Semaphore(1);

        //Semaphore gia tin periptosh tou fanariou (scenario 5).
        //Dothike arxiki timh 3 (tosa aftokinita mporoun na pernane taftoxrona kathe fora pou einai prasino to fanari), mporei na anatethei opoiosdhpote arithmos. Epeleksa to 3 gia na ginei arketes fores enallagi twn fanariwn.
        TrafficLightSem = new Semaphore(3, true);

        //Deixnei to xrwma tou aftokinitou pou perase proigoumenws.
        PreviousCar = -1;

        //Xrisimopioithike sto scenario 5 twn fanariwn. Deixnei poio xroma aftokinitwn exei prasino fanarh.
        AcceptableColor = -1;

        //Oi dio parakatw metavlites xrisimopioithikan stin periptwsi twn fanariwn.
        //Antiprosopevei ton xrono pou egine apo kokkino se prasino to fanari kai perase to prwto aftokinito tis plevras. (Xrisimopieitai kai gia tis dio plevres).
        PreviousCarStartTime = System.currentTimeMillis();
        //O xronos pou einai prasino to fanari (15 MILLISECONDS).
        GreenLightTime = 15l;

        this.Scenario = Scenario;
    }

    //Ylopioisi tou senariou 1 (Anasfalhs dielefsi).
    //Ws parametro dexetai to xroma tou aftokinitou poy pernaei.
    //Meiwnei ton antistoixo counter o opoios deixnei posa aftokinita perimenoun apo kathe plevra.
    public void UnsafeCrossed(int Color) {
        if (Color == 0) {
            --RedCounter;
        } else {
            --BlueCounter;
        }
    }

    //Ylopioisi tou senariou 2 kai 4.
    //Dexetai to xrwma tou aftokinitou pou diasxizei tin gefira.
    //Me tin xrhsh shmatoforoy eksasfalizetai i asfalhs dielefsi kai allagh ths timis twn counters.
    public void SafeCrossed(int Color) {
        try {
            BridgeSem.acquire();
            if (Color == Red) {
                --RedCounter;
            } else
                --BlueCounter;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Ylopioisi tou senariou 3 (Asfalhs kai dikaih dielefsh).
    //Dexetai to xrwma tou aftokinitou pou diasxizei tin gefira.
    //Xrisimopiei to keyword synchronized kai tis methodou wait() gia na eksasfalisei oti den tha perasoun dia aftokinita tou idiou xromatos to ena meta to allo.
    public synchronized void SafeFairCrossed(int Color) {
        if (Color == PreviousCar) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            PreviousCar = Color;
            notify();
        }
    }

    //Ylopioish 5ou senariou opou h kikloforia rithmizete me fanaria kai ta aftokinita kathe plevras exoun sigkekrimeno xrono gia na perasoun.
    //Xrisimopiei semaphores wste na mporoun panw apo 1 aftokinita na pernoun tin gefira to ena piso apo to allo.
    public void TrafficLightCrossed() {
        try {
            TrafficLightSem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Eksodos tou aftokinitou kai apodesmefshs tous simatoforou.
    public void Exit() {
        if (Scenario.equals("5"))
            TrafficLightSem.release();
        else
            BridgeSem.release();
    }

    //Diafores methodoi Getters kai incrementation.

    public synchronized void RedArrived() {
        RedCounter++;
    }

    public synchronized void BlueArrived() {
        BlueCounter++;
    }

    public synchronized int getBlueCounter() {
        return BlueCounter;
    }

    public synchronized int getRedCounter() {
        return RedCounter;
    }

    public synchronized int getPreviousCar() {
        return PreviousCar;
    }

    //Methodos opou elexgei an to aftokinito pou thelei na perasei kai tin kalei, prolavainei xronika na perasei tin gefira.
    //Epistrefei true an prolavainei na perasei, false an oxi.
    public boolean inTime() {
        if (PreviousCar == -1) {
            return true;
        } else
            return PreviousCarStartTime + GreenLightTime > System.currentTimeMillis() + 5l;
    }

    //Methodos typou void opou o rolos tis einai na elexgei an to fanari prepei na allaksei h oxi.
    //Xrisimopioithike Synchronized gia tin isolation (ACID Criteria) twn metavliton kai opoiodipote allagwn se aftes.
    public void myTurn(int Color, long arrivalTime) throws InterruptedException {
        synchronized (this){
            if (PreviousCar == -1) {
                this.PreviousCar = Color;
                this.AcceptableColor = Color;
                this.PreviousCarStartTime = arrivalTime;
            } else if (PreviousCar == Color) {
                if (PreviousCarStartTime + GreenLightTime <= System.currentTimeMillis()) {
                    wait();
                }
            } else if (PreviousCar != Color) {
                if (PreviousCarStartTime + GreenLightTime <= System.currentTimeMillis()) {
                    this.PreviousCar = Color;
                    this.AcceptableColor = Color;
                    this.PreviousCarStartTime = arrivalTime;
                    notifyAll();
                }
            }
            notifyAll();

        }
    }

    //Methodos dexete to xroma tou aftokinitou pou tin kalei kai elexgei an einai i seira tou na perasei i oxi. Ousiastika elexgei an to fanari einai kokkino i prasino.
    //Epistrefei true an to fanari stin plevra pou vriskete to aftokinito einai prasino, false an oxi.
    public boolean canPass(int Color){
        return AcceptableColor == Color;
    }
}
