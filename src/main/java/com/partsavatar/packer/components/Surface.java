package com.partsavatar.packer.components;


import lombok.Getter;
import lombok.ToString;

@ToString()
public class Surface {
    @Getter
    Vector3D leftUpperBehind;
    @Getter
    Vector3D surface;

    public Surface(Vector3D leftBottomBehind, Vector3D partDim) {
        leftUpperBehind = new Vector3D(leftBottomBehind.getX(), leftBottomBehind.getY() + partDim.getY(), leftBottomBehind.getZ());
        surface = new Vector3D(partDim.getX(), 0, partDim.getZ());
    }
}
