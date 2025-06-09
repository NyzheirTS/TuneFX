package com.warnercloud.musicplayer.Utils;

public class ViewControllerWrapper<T> {
    private final javafx.scene.Parent view;
    private final T controller;

    public ViewControllerWrapper(javafx.scene.Parent view, T controller) {
        this.view = view;
        this.controller = controller;
    }

    public javafx.scene.Parent getView() {
        return view;
    }
    public T getController() {
        return controller;
    }
}
