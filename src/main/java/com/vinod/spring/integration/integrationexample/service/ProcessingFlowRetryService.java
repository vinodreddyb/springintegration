package com.vinod.spring.integration.integrationexample.service;

import com.vinod.spring.integration.integrationexample.Test;

public class ProcessingFlowRetryService {

    public void printMessage(Test test) {
        System.out.println("Printing " + test.getId());
    }
}
