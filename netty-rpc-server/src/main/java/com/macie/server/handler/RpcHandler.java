package com.macie.server.handler;

import com.macie.protocol.RpcRequest;
import com.macie.protocol.RpcResponse;
import com.macie.server.RpcServer;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author macie
 * @date 2022/10/10
 */
@Slf4j
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
        log.debug("Receive request " + msg.getRequestId());
        RpcServer.submit(() -> {
            RpcResponse response = new RpcResponse();
            response.setRequestId(msg.getRequestId());
            try {
                Object result = handle(msg);
                response.setResult(result);
                log.debug("rpc result : {}", result.toString());
            } catch (InvocationTargetException e) {
                log.error("invoke method error : ", e);
                response.setError(e.toString());
            }
            ctx.writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE)
                    .addListener((ChannelFutureListener) (channelFuture) -> log.debug("Send response for request " + msg.getRequestId()));
        });
    }

    private Object handle(RpcRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod method = serviceFastClass.getMethod(methodName, parameterTypes);
        return method.invoke(serviceBean, parameters);
    }
}
