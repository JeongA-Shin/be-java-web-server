package webserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import webserver.RequestParser;

import java.io.*;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {

    String mockHeader = "Content-Type: application/json\r\n"+ "Accept: application/json\r\n"+ "Authorization: Bearer 12345\r\n"+"\r\n";

    @Test
    void parseHeaderTest() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(mockHeader.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        //System.out.println(br.readLine());
        Map<String,String> actualMap = RequestParser.parseHeader(br);
        //assertThat(actualMap.get("Content-Type")).isEqualTo("application/json");
    }
}
