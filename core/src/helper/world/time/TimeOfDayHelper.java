package helper.world.time;

public class TimeOfDayHelper {
    public float[] returnTime(String time) {
        switch (time.toLowerCase()) {
            case "dusk":
                return new float[]{0.4f, 0.5f, 0.8f, 1f};
            case "night":
                return new float[]{0.2f, 0.25f, 0.6f, 1f};
            case "day":
                return new float[]{1f, 1f, 1f, 1f};
            default:
                // Fallback: maybe day if unrecognized
                return new float[]{1f, 1f, 1f, 1f};
        }
    }
}
