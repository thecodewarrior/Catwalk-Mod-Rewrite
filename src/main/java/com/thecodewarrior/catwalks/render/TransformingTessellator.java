package com.thecodewarrior.catwalks.render;

import java.util.Stack;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;

import com.thecodewarrior.codechicken.lib.vec.Matrix4;
import com.thecodewarrior.codechicken.lib.vec.Vector3;

public class TransformingTessellator extends Tessellator {
	Matrix4 matrix = new Matrix4();
	Stack<Matrix4> matrixStack = new Stack<Matrix4>();
	Tessellator t;
	
	public static TransformingTessellator instance = new TransformingTessellator(Tessellator.instance);
	
	public TransformingTessellator(Tessellator toWrap) {
		t = toWrap;
	}
	
	public void pushMatrix() {
		matrixStack.push(matrix);
		matrix = matrix.copy();
	}
	
	public void popMatrix() {
		if(matrixStack.isEmpty())
			matrix = new Matrix4();
		else
			matrix = matrixStack.pop();
	}
	
	public void translate(double x, double y, double z) {
		matrix.translate(new Vector3(x,y,z));
	}
	public void scale(double x, double y, double z) {
		matrix.scale(new Vector3(x,y,z));
	}
	public void rotate(double angle, double x, double y, double z) {
		angle = (angle/180D)*Math.PI;
		matrix.rotate(angle, new Vector3(x,y,z));
	}
	
	
	// transforming methods
	
    public TesselatorVertexState getVertexState(float x, float y, float z) {
    	Vector3 vec = get(x,y,z);
    	return t.getVertexState((float)vec.x, (float)vec.y, (float)vec.z);
    }
    
    public void addVertexWithUV(double x, double y, double z, double u, double v) {
    	t.setTextureUV(u, v);
    	this.addVertex(x, y, z);
//    	Vector3 vec = get(x,y,z);
//    	t.addVertexWithUV(vec.x, vec.y, vec.z, u, v);
    }
    
    public void addVertex(double x, double y, double z) {
    	Vector3 vec = get(x,y,z);
    	t.addVertex(vec.x, vec.y, vec.z);
    }
    
    public void setNormal(float x, float y, float z) {
    	Vector3 vec = getN(x,y,z);
    	t.setNormal((float)vec.x, (float)vec.y, (float)vec.z);
    }

    // utility methods
    
    Vector3 get(double x, double y, double z) {
    	Vector3 vec = new Vector3(x,y,z);
    	apply(vec);
    	return vec;
    }
    Vector3 getN(double x, double y, double z) {
    	Vector3 vec = new Vector3(x,y,z);
    	applyN(vec);
    	return vec;
    }
    public void apply(Vector3 vec) {
    	matrix.apply(vec);
    }
    public void applyN(Vector3 vec) {
    	matrix.applyN(vec);
    }
	
    // plain pass-it-on methods
    
    public int draw()
    { return t.draw(); }
    
    public void setVertexState(TesselatorVertexState p_147565_1_)
    { t.setVertexState(p_147565_1_); }
    
    public void startDrawingQuads()
    { t.startDrawingQuads(); }
    
    public void startDrawing(int p_78371_1_)
    { t.startDrawing(p_78371_1_); }
    
    public void setTextureUV(double p_78385_1_, double p_78385_3_)
    { t.setTextureUV(p_78385_1_, p_78385_3_); }
    
    public void setBrightness(int p_78380_1_)
    { t.setBrightness(p_78380_1_); }
    
    public void setColorOpaque_F(float p_78386_1_, float p_78386_2_, float p_78386_3_)
    { t.setColorOpaque_F(p_78386_1_, p_78386_2_, p_78386_3_); }
    
    public void setColorRGBA_F(float p_78369_1_, float p_78369_2_, float p_78369_3_, float p_78369_4_)
    { t.setColorRGBA_F(p_78369_1_, p_78369_2_, p_78369_3_, p_78369_4_); }
    
    public void setColorOpaque(int p_78376_1_, int p_78376_2_, int p_78376_3_)
    { t.setColorOpaque(p_78376_1_, p_78376_2_, p_78376_3_); }
    
    public void setColorRGBA(int p_78370_1_, int p_78370_2_, int p_78370_3_, int p_78370_4_)
    { t.setColorRGBA(p_78370_1_, p_78370_2_, p_78370_3_, p_78370_4_); }
    
    public void func_154352_a(byte p_154352_1_, byte p_154352_2_, byte p_154352_3_)
    { t.func_154352_a(p_154352_1_, p_154352_2_, p_154352_3_); }
    
    public void setColorOpaque_I(int p_78378_1_)
    { t.setColorOpaque_I(p_78378_1_); }
    
    public void setColorRGBA_I(int p_78384_1_, int p_78384_2_)
    { t.setColorRGBA_I(p_78384_1_, p_78384_2_); }
    
    public void disableColor()
    { t.disableColor(); }
    
    public void setTranslation(double p_78373_1_, double p_78373_3_, double p_78373_5_)
    { t.setTranslation(p_78373_1_, p_78373_3_, p_78373_5_); }
    
    public void addTranslation(float p_78372_1_, float p_78372_2_, float p_78372_3_)
    { t.addTranslation(p_78372_1_, p_78372_2_, p_78372_3_); }

}
