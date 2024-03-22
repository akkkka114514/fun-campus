package com.akkkka.funcampusgateway.filter;

import com.akkkka.funcampussecurityapi.api.ITokenAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

/**
 * @author akkkka
 */
@Component
@Slf4j
public class GlobalAuthFilter implements GlobalFilter {
    @DubboReference(check=false)
    private ITokenAuthService tokenAuthClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().pathWithinApplication().value();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        String token = getTokenFromRequest(request);
        log.info("请求路径:{},远程IP地址:{},token:{}", path, remoteAddress, token);
        //token不通过
        if(path.startsWith("/sso") || path.startsWith("/captcha")){
            //登录注册相关不拦截
            return chain
                    //继续调用filter
                    .filter(exchange);
                    //filter的后置处理
        }else if(token==null || !tokenAuthClient.tokenAuth(token)){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            ServerHttpResponse response = exchange.getResponse();
            return response.setComplete();
        }else{
            return chain
                    //继续调用filter
                    .filter(exchange)
                    //filter的后置处理
                    .then(Mono.fromRunnable(() -> {
                        log.info("token检查通过");
                    }));
        }

    }

    public String getTokenFromRequest(ServerHttpRequest request){
        List<String> cookies = request.getHeaders().get("Cookie");
        if(Objects.isNull(cookies)){
            return null;
        }
        for (String cookie : cookies) {
            if (cookie.contains("satoken=")) {
                return cookie.split("=")[1];
            }
        }
        return null;
    }
}
