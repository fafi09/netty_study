package com.thesevensky.netty.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class NettyBuf {
    public static void main(String[] args) {
        //two();
        //three();
        //four();
        five();
    }

    public static void one() {
        ByteBuf buf = Unpooled.buffer(10);
        for(int i = 0; i < 10; i++) {
            buf.writeByte(i);
        }
        /*for(int i = 0; i < 10; i++) {
            System.out.println(buf.getByte(i));
        }*/
        System.out.println(buf.readerIndex());
        //read 会改变readIndex，get不会
        System.out.println(buf.readByte());
        System.out.println(buf.readByte());
        System.out.println(buf.readerIndex());
    }

    public static void two() {
        ByteBuf buf = Unpooled.copiedBuffer("张hello", Charset.forName("Utf-8"));
        if(buf.hasArray()) {
            byte[] content = buf.array();
            String str = new String(content, Charset.forName("Utf-8"));
            System.out.println(str);
            System.out.println(buf);
            System.out.println(buf.arrayOffset());
            System.out.println(buf.readerIndex());
            System.out.println(buf.writerIndex());
            System.out.println(buf.capacity());

            int readbs = buf.readableBytes();
            System.out.println(readbs);

            for(int i = 0; i < buf.readableBytes(); i++) {
                System.out.println((char)buf.getByte(i));
            }
            System.out.println(buf.getCharSequence(0,4,Charset.forName("Utf-8")));
        }

    }

    public static void three() {
        //heapbuf directbuf CompositeByteBuf
        CompositeByteBuf compositebuf =Unpooled.compositeBuffer();
        ByteBuf directbuf = Unpooled.directBuffer();
        ByteBuf buf = Unpooled.buffer(10);
        compositebuf.addComponents(directbuf, buf);
        //compositebuf.removeComponent(0);
        Iterator<ByteBuf> iter = compositebuf.iterator();
        while(iter.hasNext()) {
            System.out.println(iter.next());
        }
        compositebuf.forEach(System.out::println);
    }

    public static void four() {
        Person p = new Person();
        for(int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(p.age++);
                }
            }).start();
        }
    }

    public static void five() {
        Person person = new Person();
        AtomicIntegerFieldUpdater<Person> p = AtomicIntegerFieldUpdater.newUpdater(Person.class,"age");
        for(int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(p.getAndIncrement(person));
                }
            }).start();
        }
    }
}
