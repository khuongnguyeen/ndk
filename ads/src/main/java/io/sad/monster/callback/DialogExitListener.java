package io.sad.monster.callback;

public interface DialogExitListener {
    void onExit(boolean exit);
    void onCancel(boolean exit);
}
