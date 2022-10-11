package com.macie.client;

import com.macie.client.handler.RpcClientHandler;
import com.macie.protocol.RpcRequest;
import com.macie.protocol.RpcResponse;
import com.macie.client.discovery.ServerDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * RPC 客户端
 * @author macie
 * @date 2022/10/11
 */
@Component
public class RpcClient {
    @Value("${server.address}")
    private String serverAddress;
    
    @Autowired
    private ServerDiscovery serverDiscovery;
    
    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                (proxy, method, args) -> {
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameters(args);
                    request.setParameterTypes(method.getParameterTypes());
                    
                    if (serverDiscovery != null) {
                        serverAddress = serverDiscovery.discover();
                    }
                    
                    if (serverAddress != null) {
                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        RpcClientHandler client = new RpcClientHandler(host, port);
                        RpcResponse response = client.send(request);
                        if (response.isError()) {
                            throw new RuntimeException("Response error : ", new Throwable(response.getError()));
                        } else {
                            return response.getResult();
                        }
                    } else {
                        throw new RuntimeException("No Server address found!");
                    }
                });
    }
}
