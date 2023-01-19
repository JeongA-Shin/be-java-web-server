package controller;

import model.User;
import service.SignUpService;
import view.Model;
import view.ViewHandler;
import webserver.HttpMethod;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class FrontController {
    private static final Map<String,Controller> controllers;
    private static final String SIGN_UP_PATH_URL = "/user/create";
    private static final String LOGIN_PATH_URL ="/user/login";
    private static final String USER_LIST_PATH_URL ="/user/list";

    //객체 캐싱
    static{
        SignUpService signUpService = new SignUpService();
        SignUpController signUpController = new SignUpController(signUpService);
        LoginController loginInController = new LoginController();
        UserListController userListController = new UserListController();
        controllers = Map.of(
                SIGN_UP_PATH_URL,signUpController,
                LOGIN_PATH_URL,loginInController,
                USER_LIST_PATH_URL,userListController
        );
    }

    public Controller getControllerByUrl(String pathUrl){
        return controllers.get(pathUrl);
    }

    /**
     * 프론트 컨트롤러 : 요청 url에 따라 해당되는 컨트롤러를 찾고 해당 컨트롤러의 service를 실행
     * @param request
     * @param response
     */
    public void handle(HttpRequest request, HttpResponse response) {
        try{
            String url = request.getUrl();
            Model model = new Model();

            String dynamicUrl ="";
            //get이면 파일 요청
            if(request.getMethod().equals(HttpMethod.GET)){
                if(!(url.contains("."))){
                    dynamicUrl = url+".html";
                    request.setUrl(dynamicUrl);
                }
            }

            Controller controller = new NotFoundExceptionHandler();
            //일반 정적 파일 요청시
            if(url.contains(".")&&(dynamicUrl.isEmpty())) {
                System.out.println("static controller");
                controller = new ReturnFileController();
            }
            //동적 파일 요청이라면 - 현재는 유저 리스트 요청만
            if(!(dynamicUrl.isEmpty())) {
                System.out.println("dynamic controller");
                controller = getControllerByUrl(url);
                String path = controller.service(request,response,model);
                ViewHandler.handle(path,request,response,model);
                return;
            }
            //만약 파일 요청이 아니라 그 외 요청이라면 (post등)
            if(!url.contains(".") && (dynamicUrl.isEmpty())) {
                System.out.println("etc controller");
                controller = getControllerByUrl(url);
//                String path = controller.service(request,response,model);
//                ViewHandler.handle(path,request,response,model);
//                return;
            }
            controller.service(request,response,model);
        }catch(NullPointerException e){
            NotFoundExceptionHandler.showErrorPage(response);
             e.printStackTrace();
            System.out.println("해당 url에 대한 응답이 없습니다");
        } catch (FileNotFoundException e) {
            NotFoundExceptionHandler.showErrorPage(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
