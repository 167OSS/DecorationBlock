package decoblock;

public enum DecorationMode {

    CROSS("cross"),
    PLATE("plate"),
    CENTER("center"),
    NONE("");

    private String typetext;

    private DecorationMode(String typetext) {
        this.typetext = typetext;
    }

    public String typeText() {
        return typetext;
    }

    public TextureType getTextureType() {
        switch (this) {
            case CROSS:
                return TextureType.CROSS;
            case PLATE:
                return TextureType.PLATE;
            case CENTER:
                return TextureType.PLATE;
            default:
        }

        return TextureType.NONE;
    }

    public static DecorationMode fromTypeText(String typetext) {
        if (CROSS.typetext.equals(typetext)) return CROSS;
        if (PLATE.typetext.equals(typetext)) return PLATE;
        if (CENTER.typetext.equals(typetext)) return CENTER;

        return NONE;
    }

    public static final DecorationMode[] availableModes = { CROSS, PLATE, CENTER };
}
