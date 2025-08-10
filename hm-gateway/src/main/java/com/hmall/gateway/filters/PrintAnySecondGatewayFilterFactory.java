package com.hmall.gateway.filters;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
// 固定结尾：GatewayFilterFactory方便配置,Object处写参数类型
// PrintAny2GatewayFilterFactory 会报错说找不到
public class PrintAnySecondGatewayFilterFactory extends AbstractGatewayFilterFactory<PrintAnySecondGatewayFilterFactory.Config> {

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter(new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String a = config.getA();
                String b = config.getB();
                String c = config.getC();
                System.out.println("print any 2 filter running, a = " + a + ", b = " + b + ", c = " + c);
                return chain.filter(exchange);
            }
        }, -2);
    }

    @Data
    public static class Config {
        private String a;
        private String b;
        private String c;
    }

    public PrintAnySecondGatewayFilterFactory() {
        super(Config.class);
    }

    /**
     * 参数顺序
     * @return
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("a", "b", "c");
    }
}
