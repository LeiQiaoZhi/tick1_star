package uk.ac.cam.cl.gfxintro.ql316.tick1star;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class BumpySphere extends Sphere {

	private float BUMP_FACTOR = 5f;
	private float[][] heightMap;
	private int bumpMapHeight;
	private int bumpMapWidth;

	public BumpySphere(Vector3 position, double radius, ColorRGB colour, String bumpMapImg) {
		super(position, radius, colour);
		try {
			BufferedImage inputImg = ImageIO.read(new File(bumpMapImg));
			bumpMapHeight = inputImg.getHeight();
			bumpMapWidth = inputImg.getWidth();
			heightMap = new float[bumpMapHeight][bumpMapWidth];
			for (int row = 0; row < bumpMapHeight; row++) {
				for (int col = 0; col < bumpMapWidth; col++) {
					float height = (float) (inputImg.getRGB(col, row) & 0xFF) / 0xFF;
					heightMap[row][col] = BUMP_FACTOR * height;
				}
			}
		} catch (IOException e) {
			System.err.println("Error creating bump map");
			e.printStackTrace();
		}
	}

	// Get normal to surface at position
	@Override
	public Vector3 getNormalAt(Vector3 position) {

		Vector3 OP = position.subtract(this.position);
		double r = OP.magnitude();

		double theta = Math.acos(OP.y/OP.magnitude());
		double phi = Math.atan(OP.z/ OP.x);
		if(OP.z>0&&OP.x<0){ // second quadrant
			phi = Math.PI + phi;
		}
		if(OP.z<0&&OP.x<0){ // third quadrant
			phi = Math.PI + phi;
		}
		if(OP.z<0&&OP.x>0){ // fourth quadrant
			phi = 2*Math.PI + phi;
		}

		double u = phi*(bumpMapWidth-1)/(2*Math.PI);
		double v = theta*(bumpMapHeight-1)/Math.PI;

		Vector3 smoothNormal = OP.normalised();
		Vector3 Pu = new Vector3(-r*Math.sin(theta)*Math.sin(phi), -r*Math.sin(theta), r*Math.sin(theta)*Math.cos(phi) ).normalised();
		Vector3 Pv = smoothNormal.cross(Pu).normalised();

		if(u+1>bumpMapWidth-1){u=0;}
		if(v+1>bumpMapHeight-1){v=0;}
		double bu = heightMap[(int)Math.round(v)][(int)Math.round(u+1)]- heightMap[(int)Math.round(v)][(int)Math.round(u)];
		double bv = heightMap[(int)Math.round(v+1)][(int)Math.round(u)]- heightMap[(int)Math.round(v)][(int)Math.round(u)];
		Vector3 modifiedNormal = smoothNormal.add(Pu.scale(bu)).add(Pv.scale(bv)).normalised();
		//TODO: return the normal modified by the bump map
		return modifiedNormal;
	}

}
