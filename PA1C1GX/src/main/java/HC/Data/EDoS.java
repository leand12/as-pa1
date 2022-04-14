package HC.Data;

/**
 * Degree of severity given to Patients
 */
public enum EDoS {
    RED("R"),
    YELLOW("Y"),
    BLUE("B"),
    NONE("");

    private String custom;

    EDoS(String custom) {
        this.custom = custom;
    }

    @Override
    public String toString() {
        return custom;
    }
}
