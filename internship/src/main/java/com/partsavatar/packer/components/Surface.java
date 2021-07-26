package com.partsavatar.packer.components;


import lombok.Getter;
import lombok.ToString;

@ToString()
public class Surface {
    @Getter
    private Vector3D leftUpperBehind;
    @Getter
    private Vector3D surface;

    public Surface(final Vector3D leftBottomBehind, final Vector3D partDim) {
        leftUpperBehind = new Vector3D(leftBottomBehind.getX(), leftBottomBehind.getY() + partDim.getY(), leftBottomBehind.getZ());
        surface = new Vector3D(partDim.getX(), 0, partDim.getZ());
    }
}
