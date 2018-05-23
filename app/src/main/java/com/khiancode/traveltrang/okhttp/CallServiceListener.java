package com.khiancode.traveltrang.okhttp;

public interface CallServiceListener {
    void ResultData(String data);
    void ResultError(String data);
    void ResultNull(String data);
}