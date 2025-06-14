public class TrafficLightController {
    // Simulating PIR sensors for NS and EW
    private boolean[] pirSensors = new boolean[2];  // {NS, EW}

    // Green time per direction
    private int[] greenTimes = new int[2];
    private int[] vehicleCounts = new int[2];

    private int currentDirection = 0;
    private int previousDirection = -1;
    private int redBlinkCount = 0;
    private boolean redBlinkState = false;
    private long lastBlinkTime = 0;

    private int phase = 0; // 0 = Green, 1 = Yellow, 2 = Red
    private long startTime = 0;
    private final long yellowDuration = 5000;

    public static void main(String[] args) {
        TrafficLightController controller = new TrafficLightController();
        controller.setup();
        controller.loop();
    }

    public void setup() {
        // Simulate sensor input
        pirSensors[0] = true;   // NS vehicle detected
        pirSensors[1] = false;  // EW no vehicle

        getVehicleCounts();
        calculateGreenTimes();
        startGreenPhase(currentDirection);
    }

    public void loop() {
        while (true) {
            long currentTime = System.currentTimeMillis();

            if (phase == 0 && currentTime - startTime >= greenTimes[currentDirection]) {
                startYellowPhase(currentDirection);
            } else if (phase == 1 && currentTime - startTime >= yellowDuration) {
                startRedPhase(currentDirection);

                previousDirection = currentDirection;
                redBlinkCount = vehicleCounts[previousDirection];

                currentDirection = (currentDirection + 1) % 2;

                getVehicleCounts();
                calculateGreenTimes();
                startGreenPhase(currentDirection);
            }

            handleRedBlinking();
            try {
                Thread.sleep(200); // simulate delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getVehicleCounts() {
        for (int i = 0; i < 2; i++) {
            vehicleCounts[i] = pirSensors[i] ? 5 : 0;
        }
    }

    public void calculateGreenTimes() {
        for (int i = 0; i < 2; i++) {
            greenTimes[i] = vehicleCounts[i] > 0 ? 30000 : 15000;
        }
    }

    public void startGreenPhase(int dir) {
        phase = 0;
        startTime = System.currentTimeMillis();
        System.out.println("Direction " + (dir == 0 ? "NS" : "EW") + " -> GREEN");
    }

    public void startYellowPhase(int dir) {
        phase = 1;
        startTime = System.currentTimeMillis();
        System.out.println("Direction " + (dir == 0 ? "NS" : "EW") + " -> YELLOW");
    }

    public void startRedPhase(int dir) {
        phase = 2;
        System.out.println("Direction " + (dir == 0 ? "NS" : "EW") + " -> RED");
    }

    public void handleRedBlinking() {
        if (previousDirection == -1 || redBlinkCount <= 0) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBlinkTime >= 500) {
            redBlinkState = !redBlinkState;
            System.out.println("Direction " + (previousDirection == 0 ? "NS" : "EW") + " Red LED " + (redBlinkState ? "BLINK ON" : "BLINK OFF"));
            lastBlinkTime = currentTime;

            if (!redBlinkState) {
                redBlinkCount--;
            }

            if (redBlinkCount <= 0) {
                System.out.println("Direction " + (previousDirection == 0 ? "NS" : "EW") + " -> RED Steady");
                previousDirection = -1;
            }
        }
    }
}