package blossom.project.netty.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import java.util.Arrays;

public class SimpleTestMain {

    public static void main(String[] args) {

        //初始化数据
        DemoProto.Demo.Builder demo = DemoProto.Demo.newBuilder();
        demo.setId(1).setCode("001").setName("张三").build();

        //序列化
        DemoProto.Demo build = demo.build();
        //转换成字节数组
        byte[] s = build.toByteArray();
        System.out.println("protobuf数据bytes[]:" + Arrays.toString(s));
        System.out.println("protobuf序列化大小: " + s.length);


        DemoProto.Demo demo1 = null;
        String jsonObject = null;
        try {
            //反序列化
            demo1 = DemoProto.Demo.parseFrom(s);
            //转 json
            jsonObject = JsonFormat.printer().print(demo1);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        System.out.println("Json格式化结果:\n" + jsonObject);
        System.out.println("Json格式化数据大小: " + jsonObject.getBytes().length);
    }
}