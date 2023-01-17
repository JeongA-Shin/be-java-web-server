package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.RequestDispatcher;

public class ReturnFileController implements Controller{

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        RequestDispatcher.handle(request,response);
    }
}
