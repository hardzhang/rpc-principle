package com.xinc.zhang.netty.rpc.client;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import com.xinc.zhang.netty.rpc.codec.RpcRequest;
import com.xinc.zhang.netty.rpc.codec.RpcResponse;

public class RpcProxy {
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequest request = new RpcRequest(); // 鍒涘缓骞跺垵濮嬪�?RPC 璇锋�?                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        if (serviceDiscovery != null) {
                            serverAddress = serviceDiscovery.discover(); // 鍙戠幇鏈嶅姟
                        }
                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);

                        RpcClient client = new RpcClient(host, port); // 鍒濆鍖�RPC 瀹㈡埛绔�?                       
                        RpcResponse response = client.send(request); // 閫氳�?RPC瀹㈡埛绔彂閫丷PC璇锋眰骞惰幏鍙朢PC鍝嶅�?                        if (response.isError()) {
                        if(response.isError()){
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }
                    }
                });
    }
}
