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

		Vector3 smoothNormal = position.subtract(this.position).normalised();

		double theta = Math.acos(smoothNormal.y);
		double phi = Math.atan(smoothNormal.z/ smoothNormal.x);
		if(smoothNormal.z>0&& smoothNormal.x<0){ // second quadrant
			phi = Math.PI + phi;
		}
		else if(smoothNormal.z<0&& smoothNormal.x<0){ // third quadrant
			phi = Math.PI + phi;
		}
		else if(smoothNormal.z<0&& smoothNormal.x>0){ // fourth quadrant
			phi = 2*Math.PI + phi;
		}

		int u = (int) Math.round(phi*(bumpMapWidth-1)/(2*Math.PI)) % bumpMapWidth;
		int v = (int) Math.round(theta*(bumpMapHeight-1)/Math.PI) % bumpMapHeight;

		Vector3 Pu = new Vector3(-Math.cos(theta)*Math.sin(phi), -Math.sin(theta), Math.cos(theta)*Math.cos(phi) ).normalised();
		Vector3 Pv = smoothNormal.cross(Pu);

		double bu = heightMap[(v)][(u+1)]- heightMap[(v)][(u)];
		double bv = heightMap[(v+1)][(u)]- heightMap[(v)][(u)];
		Vector3 modifiedNormal = smoothNormal.add(Pu.scale(bu)).add(Pv.scale(bv)).normalised();
		//TODO: return the normal modified by the bump map
		return modifiedNormal;
	}

}
