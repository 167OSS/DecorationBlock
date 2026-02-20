package decoblock;

public enum TextureType {

    CROSS("cross"),
    PLATE("plate"),
    NONE("");

    private String typetext;

    private TextureType(String typetext) {
        this.typetext = typetext;
    }

    public String typeText() {
        return typetext;
    }

    public static TextureType fromTypeText(String typetext) {
        if (CROSS.typetext.equals(typetext)) return CROSS;
        if (PLATE.typetext.equals(typetext)) return PLATE;

        return NONE;
    }

    public static final TextureType[] availableTypes = { CROSS, PLATE };
}
