package com.example.studygo.ui.signUpLogIn;

public interface ConnectionCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}