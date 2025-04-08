package com.zupbank.TaahjjimBank.zup.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LambdaHandler implements RequestHandler<Map<String,Object>, String> {


    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        return "Hello, lambda";
    }

}
