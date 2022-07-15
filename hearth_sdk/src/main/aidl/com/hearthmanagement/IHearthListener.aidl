package com.hearthmanagement;

interface IHearthListener {
    void onSuccess(String functionName, String success);
    void onFailure(String functionName, String message);
}