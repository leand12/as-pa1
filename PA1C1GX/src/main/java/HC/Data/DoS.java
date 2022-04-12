package HC.Data;

/**
 * Degree of severity given to Patients
 */
public enum DoS {
    RED("R"),
    YELLOW("Y"),
    BLUE("B"),
    NONE("");

    private String custom;

    DoS(String custom) {
        this.custom = custom;
    }

    @Override
    public String toString() {
        return custom;
    }
}
