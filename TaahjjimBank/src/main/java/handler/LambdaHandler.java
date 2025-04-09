
package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class LambdaHandler implements RequestHandler<Map<String,Object>, String> {


    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        return "Hello, lambda";
    }

}
