package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import controller.Controller;
import controller.Controller;
import controller.FrontController;
import controller.SignUpController;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpRequestUtils;

import javax.xml.crypto.dom.DOMCryptoContext;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private FrontController frontController;
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.frontController = new FrontController();
    }

    public void run() { //즉 이미 클라이언트와 연결이 된 상태에서 돌아가는 메서드임
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            //브라우저에서 서버로 들어오는 모든 요청은 Inputstream 안에 담겨져 있음, outputstream은 서버에서 브라우저로 보내는 응답
            HttpRequest request = RequestParser.parseInputStreamToHttpRequest(in);
            String url = request.getUrl();
            HttpResponse response = new HttpResponse();
            //단순 html 파일 반환이 요청이 아닐 때
            if(!url.endsWith("html")) {
                try{
                    Controller controller = this.frontController.getControllerByUrl(url);
                    response = controller.service(request);
                }catch (NullPointerException e){
                    logger.debug("해당되는 컨트롤러가 없습니다");
                }
            }
            // 즉 그냥 단순 html 파일 반환이 요청일 때
            if(response.getStatus()==null) RequestDispatcher.handle(request,response);
            ResponseWriter rw = new ResponseWriter(out);
            rw.write(request,response);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
