package com.warnercloud.musicplayer.Utils;

public class CustomConsumers {
    @FunctionalInterface
    public interface TripleConsumer<T,U,V>{
        void accept(T t, U u, V v);
    }
}
