package net.minecraft.server;

import me.aki.linkstone.annotations.Classfile;
import me.aki.linkstone.annotations.Method;
import me.aki.linkstone.annotations.*;

import static me.aki.linkstone.annotations.Version.*;

@Classfile(version = V1_11_R1)
@Classfile(version = V1_12_R1, name = "aqz")
public class Entity {

    @Method(version = V1_11_R1, name = "xxx")
    private void v11() {}

    @Method(version = V1_12_R1, name = "yyy")
    private void v12() {}

//    private final Logger logger;
//
//    public Entity(Logger logger) {
//        this.logger = logger;
//    }
//
//    @Field(version = V1_11_R1)
//    @Field(version = V1_12_R1, name = "asd")
//    private int age;
//
//    @Getter(version = V1_11_R1)
//    private int getter() {
//        return 10;
//    }
//    @Getter(version = V1_12_R1)
//    private int getter2() {
//        return 20;
//    }
//
//    @Setter(version = { V1_12_R1, V1_11_R1 })
//    private void setter(int value) {
//
//    }
//
//    @Method(version = V1_12_R1)
//    @Method(version = V1_11_R1, name = "hgt")
//    private int getHeight(net.minecraft.server.EnumConnectionState state) {
////        System.out.println("Hello World");
//        System.out.println(EnumConnectionState.HANDSHAKE);
//        return 20;
//    }

}
