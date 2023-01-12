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

    public RequestHandler(Socket connectionSocket,FrontController frontController) {
        this.connection = connectionSocket;
        this.frontController = frontController;
    }

    public void run() { //즉 이미 클라이언트와 연결이 된 상태에서 돌아가는 메서드임
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            //브라우저에서 서버로 들어오는 모든 요청은 Inputstream 안에 담겨져 있음, outputstream은 서버에서 브라우저로 보내는 응답
            HttpRequest request = RequestParser.parseInputStreamToHttpRequest(in);
            HttpResponse response = new HttpResponse();
            //TODO 프론트 컨트롤러는 의존성 주입 받도록 수정
            frontController.service(request,response);
            ResponseWriter rw = new ResponseWriter(out);
            rw.write(request,response);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
