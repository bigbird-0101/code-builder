package com.fpp.code.org;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/6/30 9:57
 */
public class A {
    public static <jsonObject> void main(String[] args) {
//        MessageQueue testQueue=new MessageQueue();
//        List<QueueObserver> list=new ArrayList<>(5);
//        for (int i = 0; i <100; i++) {
//            list.add(new Consumer());
//        }
//        testQueue.subscribe(list);
//        new Thread(()->{
//            for (int i = 0; i <10000000 ; i++) {
//                String a="第"+i+"个数据";
//                testQueue.add(a);
//            }
//        }).start();

//        JSONArray jsonObject= (JSONArray) JSONObject.parse("[{a:1},{a:\"c\"}]");
//        jsonObject jsonObject= (jsonObject) JSONObject.parse("[{a:1},{a:\"c\"}]");

//        Map<String,Object> maps=new IdentityHashMap<>();
//        maps.put("a","b");
//        maps.put("a","d");
//        JSONObject json = new JSONObject(maps);
//        System.out.println(json.toJSONString());
//        short a=3;
//        int b=a;

//        char a='a';
//        new String(a).toString();

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream buffer= new DataOutputStream(bout);
//        buffer.writeChar('a'.);
    }
}
