//package com.dducwsjvbe.api_gateway.Filter;
//
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class JwtHeaderFilter extends AbstractGatewayFilterFactory<JwtHeaderFilter.Config> {
//    public JwtHeaderFilter() {
//        super(JwtHeaderFilter.Config.class);
//    }
//        @Override
//    public GatewayFilter apply(JwtHeaderFilter.Config config) {
//        return (exchange, chain) -> {
//            return exchange.getPrincipal().flatMap(principal -> {
//                if (principal instanceof JwtAuthenticationToken jwtAuth){
//                    var claims=jwtAuth.getToken().getClaims();
//                    var userId=claims.get("sub").toString();
//                    var userName=claims.get("preferred_username").toString();
//                    var realmAccess = (Map<String, Object>) claims.getOrDefault("realm_access", Map.of());
//                    var roles = (List<String>) realmAccess.getOrDefault("roles", List.of());
//                    var rolesHeader = String.join(",", roles);
//                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
//                            .header("X-User-Id", userId)
//                            .header("X-Username", userName)
//                            .header("X-User-Roles", rolesHeader)
//                            .build();
//                    ServerWebExchange mutableExchange = exchange.mutate().request(mutatedRequest).build();
//                    return chain.filter(mutableExchange);
//                }
//                return chain.filter(exchange);
//            });
//        };
//    }
//    static class Config{
//
//    }
//}
