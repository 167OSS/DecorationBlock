package decoblock.gui;

import java.util.EnumSet;

public interface ButtonIconProvider {

    Object[] getIconForButton(int id, EnumSet<ButtonState> state);
}
