package com.example.springsecurityjwt.util;

import com.example.springsecurityjwt.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class JwtTokenizerTest {

    @Autowired
    JwtTokenizer jwtTokenizer;

    @Value("${jwt.secretKey}") // application.yml파일의 jwt: secretKey: 값
    String accessSecret; // "12346789012346789012346789012"

    public final long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 60 * 1000 * 30 // 30 분을 변수에다 넣어준것이다.

    @Test
    public void createToken() throws Exception{ // JWT 토큰을 생성하는 코드.
        String email = "dlalsgud12@naver.com";
        List<String> roles = List.of("ROLE_USER"); // ["ROLE_USER"]
        Long id = 1L;
        Claims claims = Jwts.claims().setSubject(email); // JWT 토큰의 payload에 들어갈 내용(claims)을 설정.
        // claims -- sub -- email
        // 위에는 이미 있는거고 없는 것들은
        //        -- roles -- ["ROLE_USER"]
        //        -- userId -- 1L
        claims.put("roles", roles);
        claims.put("userId", id);

        // application.yml파일의 jwt: secretKey: 값을 Byte 배열로 바꿔준것.
        byte[] accessSecret = this.accessSecret.getBytes(StandardCharsets.UTF_8);

        // JWT를 생성하는 부분.
        String JwtToken = Jwts.builder() // builder는 JwtBuilder를 반환. Builder패턴.
                .setClaims(claims) // claims가 추가된 JwtBuilder를 그대로 리턴.
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + this.ACCESS_TOKEN_EXPIRE_COUNT)) // 현재 시간으로부터 30분뒤에 만료.
                .signWith(Keys.hmacShaKeyFor(accessSecret)) // 결과에 서명까지 포함시킨 JwtBuilder를 리턴.
                .compact();

        System.out.println(JwtToken);
    }

    @Test
    public void parseToken() throws Exception{
        byte[] accessSecret = this.accessSecret.getBytes(StandardCharsets.UTF_8);
        // 만약 아래 값을 바꾼다면 조작된것이므로 실행을 해보면 에러가 뜬다.
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkbGFsc2d1ZDEyQG5hdmVyLmNvbSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJ1c2VySWQiOjEsImlhdCI6MTY5NTM4NTcxNCwiZXhwIjoxNjk1Mzg3NTE0fQ.kNya-9-QYvKpEyTyNqe_YE_zpbDuIw8SFt5axHeaedg";

        Claims claims = Jwts.parserBuilder() // jwtParserBuilder를 반환. - jwt를 분석하기위한 공장
                .setSigningKey(Keys.hmacShaKeyFor(accessSecret))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
        System.out.println(claims.getSubject());
        System.out.println(claims.get("roles"));
        System.out.println(claims.get("userId"));
        System.out.println(claims.getIssuedAt());
        System.out.println(claims.getExpiration()); // 여기서 찍힌 시간이 지나면 만료가되서 출력부분이 실행되지 않는다.
    }

}