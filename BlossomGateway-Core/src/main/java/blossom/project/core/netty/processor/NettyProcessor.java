package blossom.project.core.netty.processor;


import blossom.project.core.context.HttpRequestWrapper;

public interface NettyProcessor {

    void process(HttpRequestWrapper wrapper);
}
