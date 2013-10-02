package com.abir.photolotto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.TypedValue;

public class Utils {

	private static Bitmap inkwellMap = null;
	private static Bitmap nashvilleMap = null;
	
	public final static int REQUEST_ACTIVITY_TWITTER = 5000;
	public final static int REQUEST_ACTIVITY_INSTAGRAM = 5001;
	public final static int REQUEST_ACTIVITY_BEFORE_HTML = 5002;
	public final static int REQUEST_ACTIVITY_AFTER_HTML = 5003;
	public final static int REQUEST_ACTIVITY_EMAIL = 5004;
	public final static int	REQUEST_ACTIVITY_DONE = 5005;
	public final static int	REQUEST_ACTIVITY_SHARE = 5006;
	
	
	public static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String generateUniqueName() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(Calendar.getInstance().getTime());
		return "A_" + timeStamp;
	}

	public static File getFileFromBitmap(Bitmap bitmap) {
		File fileImage = null;
		try {
			File storagePath = new File(
					Environment.getExternalStorageDirectory(), "PhotoLotto");
			storagePath.mkdirs();
			String sFileName = Utils.generateUniqueName() + ".jpg";
			fileImage = new File(storagePath, sFileName);
			FileOutputStream out = new FileOutputStream(fileImage);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
			out.flush();
			out.close();
			// bitmap.recycle();
			// bitmap = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileImage;
	}

	public static Bitmap rotateBitmap(Bitmap src, float degree) {
		// create new matrix
		Matrix matrix = new Matrix();
		// setup rotation degree
		matrix.postRotate(degree);
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
				matrix, true);
	}

	public static Bitmap convertToMutable(Bitmap imgIn) {
		try {
			// this is the file going to use temporally to save the bytes.
			// This file will not be a image, it will store the raw image data.
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "temp.tmp");

			// Open an RandomAccessFile
			// Make sure you have added uses-permission
			// android:name="android.permission.WRITE_EXTERNAL_STORAGE"
			// into AndroidManifest.xml file
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

			// get the width and height of the source bitmap.
			int width = imgIn.getWidth();
			int height = imgIn.getHeight();
			Config type = imgIn.getConfig();

			// Copy the byte to the file
			// Assume source bitmap loaded using options.inPreferredConfig =
			// Config.ARGB_8888;
			FileChannel channel = randomAccessFile.getChannel();
			MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0,
					imgIn.getRowBytes() * height);
			imgIn.copyPixelsToBuffer(map);
			// recycle the source bitmap, this will be no longer used.
			imgIn.recycle();
			System.gc();// try to force the bytes from the imgIn to be released

			// Create a new bitmap to load the bitmap again. Probably the memory
			// will be available.
			imgIn = Bitmap.createBitmap(width, height, type);
			map.position(0);
			// load it back from temporary
			imgIn.copyPixelsFromBuffer(map);
			// close the temporary file and channel , then delete that also
			channel.close();
			randomAccessFile.close();

			// delete the temp file
			file.delete();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return imgIn;
	}

	public static Bitmap applyShadingFilter(Bitmap source, int shadingColor) {
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);

		int index = 0;
		// iteration through pixels
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;
				// AND
				pixels[index] &= shadingColor;
			}
		}
		// output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;
	}

	public static Bitmap applySaturationFilter(Bitmap source, int level) {
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		float[] HSV = new float[3];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);

		int index = 0;
		// iteration through pixels
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;
				// convert to HSV
				Color.colorToHSV(pixels[index], HSV);
				// increase Saturation level
				HSV[1] *= level;
				HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
				// take color back
				pixels[index] |= Color.HSVToColor(HSV);
			}
		}
		// output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;
	}

	public static Bitmap applyFleaEffect(Bitmap source) {
		final int COLOR_MIN = 0x00;
		final int COLOR_MAX = 0xFF;

		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);
		// a random object
		Random random = new Random();

		int index = 0;
		// iteration through pixels
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;
				// get random color
				int randColor = Color.rgb(random.nextInt(COLOR_MAX),
						random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
				// OR
				pixels[index] |= randColor;
			}
		}
		// output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;
	}

	public static Bitmap doInvert(Bitmap src) {
		// create new bitmap with the same settings as source bitmap
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		// color info
		int A, R, G, B;
		int pixelColor;
		// image size
		int height = src.getHeight();
		int width = src.getWidth();

		// scan through every pixel
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// get one pixel
				pixelColor = src.getPixel(x, y);
				// saving alpha channel
				A = Color.alpha(pixelColor);
				// inverting byte for each R/G/B channel
				R = 255 - Color.red(pixelColor);
				G = 255 - Color.green(pixelColor);
				B = 255 - Color.blue(pixelColor);
				// set newly-inverted pixel to output image
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final bitmap
		return bmOut;
	}

	public static Bitmap doGreyscale(Bitmap src) {
		// constant factors
		final double GS_RED = 0.299;
		final double GS_GREEN = 0.587;
		final double GS_BLUE = 0.114;

		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		// pixel information
		int A, R, G, B;
		int pixel;

		// get image size
		int width = src.getWidth();
		int height = src.getHeight();

		// scan through every single pixel
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get one pixel color
				pixel = src.getPixel(x, y);
				// retrieve color of all channels
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				// take conversion up to one single value
				R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	// level 50
	public static Bitmap tintImage(Bitmap src, int degree) {

		final double PI = 3.14159d;
		final double FULL_CIRCLE_DEGREE = 360d;
		final double HALF_CIRCLE_DEGREE = 180d;
		final double RANGE = 256d;

		int width = src.getWidth();
		int height = src.getHeight();

		int[] pix = new int[width * height];
		src.getPixels(pix, 0, width, 0, 0, width, height);

		int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
		double angle = (PI * (double) degree) / HALF_CIRCLE_DEGREE;

		int S = (int) (RANGE * Math.sin(angle));
		int C = (int) (RANGE * Math.cos(angle));

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				int index = y * width + x;
				int r = (pix[index] >> 16) & 0xff;
				int g = (pix[index] >> 8) & 0xff;
				int b = pix[index] & 0xff;
				RY = (70 * r - 59 * g - 11 * b) / 100;
				GY = (-30 * r + 41 * g - 11 * b) / 100;
				BY = (-30 * r - 59 * g + 89 * b) / 100;
				Y = (30 * r + 59 * g + 11 * b) / 100;
				RYY = (S * BY + C * RY) / 256;
				BYY = (C * BY - S * RY) / 256;
				GYY = (-51 * RYY - 19 * BYY) / 100;
				R = Y + RYY;
				R = (R < 0) ? 0 : ((R > 255) ? 255 : R);
				G = Y + GYY;
				G = (G < 0) ? 0 : ((G > 255) ? 255 : G);
				B = Y + BYY;
				B = (B < 0) ? 0 : ((B > 255) ? 255 : B);
				pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
			}

		Bitmap outBitmap = Bitmap.createBitmap(width, height, src.getConfig());
		outBitmap.setPixels(pix, 0, width, 0, 0, width, height);

		pix = null;

		return outBitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	// for hefe filter
	public static Bitmap hefeImage(Bitmap src) {
		Bitmap boostBitmap = doGamma(src, 1.3, 0.9, 0.7);
		return boostBitmap;

	}

	// for earlybird filter
	public static Bitmap earlybirdImage(Context context, Bitmap src) {
		Bitmap boostBitmapTwo = doGamma(src, 1.6, 1.50, 0.9);
		Bitmap brightBitmap = doBrightness(boostBitmapTwo, 3);
		Bitmap newBitmap = getRoundedCornerImage(brightBitmap, 0, 40, context);
		// Bitmap roundedBitmap = getRoundedCornerBitmap(newBitmap);
		return newBitmap;
	}
	
	// for earlybird filter
	public static Bitmap xProImage(Context context, Bitmap src) {
		Bitmap boostBitmapTwo = doGamma(src, 0.84, 0.78, 0.68);
		Bitmap brightBitmap = doBrightness(boostBitmapTwo, 5);
		Bitmap newBitmap = getRoundedCornerImage(brightBitmap, 0, 40, context);
		// Bitmap roundedBitmap = getRoundedCornerBitmap(newBitmap);
		return newBitmap;
	}

	public static Bitmap doBrightness(Bitmap src, int value) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				// increase/decrease each channel
				R += value;
				if (R > 255) {
					R = 255;
				} else if (R < 0) {
					R = 0;
				}

				G += value;
				if (G > 255) {
					G = 255;
				} else if (G < 0) {
					G = 0;
				}

				B += value;
				if (B > 255) {
					B = 255;
				} else if (B < 0) {
					B = 0;
				}

				// apply new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	public static Bitmap decreaseColorDepth(Bitmap src, int bitOffset) {
		// get image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				// round-off color offset
				R = ((R + (bitOffset / 2))
						- ((R + (bitOffset / 2)) % bitOffset) - 1);
				if (R < 0) {
					R = 0;
				}
				G = ((G + (bitOffset / 2))
						- ((G + (bitOffset / 2)) % bitOffset) - 1);
				if (G < 0) {
					G = 0;
				}
				B = ((B + (bitOffset / 2))
						- ((B + (bitOffset / 2)) % bitOffset) - 1);
				if (B < 0) {
					B = 0;
				}

				// set pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		// final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 45;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		// paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap getRoundedCornerImage(Bitmap bitmap, int cornerDips,
			int borderDips, Context context) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int borderSizePx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) borderDips, context
						.getResources().getDisplayMetrics());
		final int cornerSizePx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips, context
						.getResources().getDisplayMetrics());
		final Paint paint = new Paint();
		final Rect rect = new Rect(-130, -130, bitmap.getWidth() + 90,
				bitmap.getHeight() + 130);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		// paint.setColor(0xFFFFFFFF);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawARGB(0, 0, 0, 0);
		// paint.setMaskFilter(new BlurMaskFilter(90, Blur.NORMAL));
		canvas.drawRoundRect(rectF, bitmap.getWidth() / 2,
				bitmap.getHeight() / 2, paint);

		// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setMaskFilter(new BlurMaskFilter(90, Blur.NORMAL));
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		paint.setStrokeWidth((float) borderSizePx);
		canvas.drawRoundRect(rectF, bitmap.getWidth(), bitmap.getHeight(),
				paint);

		return output;
	}

	public static Bitmap doGamma(Bitmap src, double red, double green,
			double blue) {
		// create output image
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
				src.getConfig());
		// get image size
		int width = src.getWidth();
		int height = src.getHeight();
		// color information
		int A, R, G, B;
		int pixel;
		// constant value curve
		final int MAX_SIZE = 256;
		final double MAX_VALUE_DBL = 255.0;
		final int MAX_VALUE_INT = 255;
		final double REVERSE = 1.0;

		// gamma arrays
		int[] gammaR = new int[MAX_SIZE];
		int[] gammaG = new int[MAX_SIZE];
		int[] gammaB = new int[MAX_SIZE];

		// setting values for every gamma channels
		for (int i = 0; i < MAX_SIZE; ++i) {
			gammaR[i] = (int) Math.min(
					MAX_VALUE_INT,
					(int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE
							/ red)) + 0.5));
			gammaG[i] = (int) Math.min(
					MAX_VALUE_INT,
					(int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE
							/ green)) + 0.5));
			gammaB[i] = (int) Math.min(
					MAX_VALUE_INT,
					(int) ((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE
							/ blue)) + 0.5));
		}

		// apply gamma table
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				// look up gamma
				R = gammaR[Color.red(pixel)];
				G = gammaG[Color.green(pixel)];
				B = gammaB[Color.blue(pixel)];
				// set new color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}
	
	//Map PNG Loader
	public static void loadMaps(Context context) {
		try {
			if (inkwellMap == null)
			{
				InputStream inInkwellMap = context.getAssets().open("inkwellmap.png");
				BufferedInputStream buf1 = new BufferedInputStream(inInkwellMap);
				inkwellMap = BitmapFactory.decodeStream(buf1);
			}
			
			if (nashvilleMap == null)
			{
				InputStream inNashvilleMap = context.getAssets().open("nashvillemap.png");
				BufferedInputStream buf2 = new BufferedInputStream(inNashvilleMap);
				nashvilleMap = BitmapFactory.decodeStream(buf2);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static int myRound(double dValue) {
		boolean isNagative = false;
		if (dValue < 0) {
			dValue = -dValue;
			isNagative = true;
		}
		
		int ret = (int)dValue;
		double d = dValue - (double)ret;
		if (d >= 0.5)
			ret = ret + 1;
		
		if (isNagative)
			ret = -ret;
		
		return ret;
	}
	
	//NashvilleShader
	public static Bitmap nashvilleImage(Context context, Bitmap src) {
		Utils.loadMaps(context);
		
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int RR, GG, BB;
		int newRR, newGG, newBB;
		
		int pixel;
		int refPixel;
		
		int mapWidth = Utils.nashvilleMap.getWidth();
		int xx;
		
		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				RR = Color.red(pixel);
				GG = Color.green(pixel);
				BB = Color.blue(pixel);

				xx = myRound((double)(mapWidth * RR) / 255.0);
				if (xx >= mapWidth - 1)					xx = mapWidth - 1;
				refPixel = Utils.nashvilleMap.getPixel(xx, 0/*mapHeight / 6*/);
				newRR = Color.red(refPixel);
				
				xx = myRound((double)(mapWidth * GG) / 255.0);
				if (xx >= mapWidth - 1)					xx = mapWidth - 1;
				refPixel = Utils.nashvilleMap.getPixel(xx, 1/*mapHeight / 2*/);
				newGG = Color.green(refPixel);
				
				xx = myRound((double)(mapWidth * BB) / 255.0);
				if (xx >= mapWidth - 1)					xx = mapWidth - 1;
				refPixel = Utils.nashvilleMap.getPixel(xx, 2/*mapHeight * 5 / 6*/);
				newBB = Color.blue(refPixel);
				
				// apply new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(255, newRR, newGG, newBB));
			}
		}

		// return final image
		return bmOut;
	}	
	
	//NashvilleShader
	public static Bitmap inkwellImage(Context context, Bitmap src) {
		Utils.loadMaps(context);
		
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int RR, GG, BB;
		int newRR;
		
		int pixel;
		int refPixel;
		
		int mapWidth = Utils.inkwellMap.getWidth();
		int xx;
		
		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				RR = Color.red(pixel);
				GG = Color.green(pixel);
				BB = Color.blue(pixel);

				xx = myRound(RR * 0.3 + GG * 0.6 + BB * 0.1);
				if (xx >= mapWidth - 1)					xx = mapWidth - 1;
				
				refPixel = Utils.inkwellMap.getPixel(xx, 0/*mapHeight / 6*/);
				newRR = Color.red(refPixel);
				
				// apply new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(255, newRR, newRR, newRR));
			}
		}

		// return final image
		return bmOut;
	}
}
