package blossom.project.rpc.core.entity;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.ExecutionException;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 23:39
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RpcPromise类
 * Promise用来异步处理
 * 1:Future代表了一个可能还没有完成的异步操作的结果。
 * 2:在Netty中，当你执行一个异步操作（如发送数据），
 * 会得到一个Future对象。这个对象可以用来在未来某个时刻获取操作的结果。
 * 3:Promise是Future的一个子接口，它不仅代表了异步操作的结果，
 * 还可以被操作的执行者显式地标记为成功或失败。
 * 4:在Netty中，Promise用于在操作完成时设置操作的结果（成功或失败）。
 * 这是一个写入结果的Future。
 *
 *
 */
@Data
@NoArgsConstructor
public class RpcPromise<T>  extends DefaultPromise<T>
{

    //private Promise<T> promise;
    //
    //public RpcPromise(Promise<T> promise) {
    //    this.promise = promise;
    //}


    /**
     * 思考一下
     * 1: 我的代码在这里是异步处理的返回结果
     * 2: 什么时候这个返回结果可以被设置值?
     * 3: 应该就是在我client接收到server的返回值的时候
     * 4: 也就是说我可以再clienthandler里面添加一个对promise的处理
     * 5: 也就是说我得有一个cache一样的东西能缓存我的promise
     * 6: 然后再client得到数据的时候去设置promise的值
     * 7: 不论成功失败都如main函数里面一样操作就行
     * 8: promise应该也要被server去使用
     * 9: cache应该是map结构
     * @param args
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1：使用Promise作为属性
        //RpcPromise<RpcResponse> promise1=new RpcPromise<>
        //        (new DefaultPromise<RpcResponse>
        //                (new DefaultEventLoop()));
        //promise1.promise.setSuccess(new RpcResponse());
        //promise1.setSuccess(new RpcResponse());
        //第二种方式 直接用原生defaultpromise
        RpcPromise promise = new RpcPromise();
        promise.setSuccess("success");
        promise.get();

    }
}
