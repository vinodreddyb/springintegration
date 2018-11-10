package com.vinod.spring.integration.integrationexample.service;


import com.vinod.spring.integration.integrationexample.Test;

public class IngetionFlowRetryService {

    public String printMessage(Test test) {

        System.out.println("Printing " + test.getId());
        return "Printing " + test.getId();
    }
}
