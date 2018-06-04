package net.minecraft.server;

import me.aki.linkstone.annotations.*;
import static me.aki.linkstone.annotations.Version.*;

@Classfile(version = { V1_11_R1, V1_12_R1}, name = "XYZ")
public class EditedClass {
    @Field(version = { V1_11_R1, V1_12_R1}, name = "asd")
    private int x = 20;

    @Getter(version = { V1_11_R1, V1_12_R1 })
    public int getter_x() {
        return x;
    }

    @Setter(version = { V1_12_R1 })
    public void setter_x_v12(int x) {
        this.x = x;
    }

    @Setter(version = V1_11_R1)
    public void setter_x_v1(int x) {
        this.x = x * 2;
    }


    @Method(version = V1_12_R1, name = "hlo")
    public void printHello() {
        System.out.println("Hello World");
    }

    @Method(version = V1_11_R1)
    private void deprected() {
        System.out.println("Deprecated");
    }

    public EditedClass(int x) {
        this.x = x;
    }
}
