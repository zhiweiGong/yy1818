package com.android.tools.Misc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class UtilQRCode {
	private final String TAG = "UtilQRCode";
	
	private static UtilQRCode mUtilQRCode = null;

	public static synchronized UtilQRCode getInstance(){
		if(mUtilQRCode == null){
			mUtilQRCode = new UtilQRCode();
		}
		return mUtilQRCode;
	}
	
	public UtilQRCode(){
		Log.d(TAG,"init...");
	}

    public String readQRCode(Bitmap scanBitmap) {
        String content = null;
        Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

        int[] pixels = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];
        scanBitmap.getPixels(pixels, 0, scanBitmap.getWidth(), 0, 0, scanBitmap.getWidth(),scanBitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap.getWidth(),scanBitmap.getHeight(),pixels);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap, hints);
            content = result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    public Bitmap createQRCode(String str,int widthAndHeight) throws WriterException {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
		BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}else{
					pixels[y * width + x] = 0xffffffff;
				}
			}
		}
		
		Bitmap bitmap_src = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
		bitmap_src.setPixels(pixels, 0, width, 0, 0, width, height);

		return bitmap_src;
	}
	    
	 /**
     * 在二维码中间添加文字
     */
	public Bitmap addText(Bitmap src, String top,String bot) {
        if (src == null) {
            return null;
        }
  
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
         
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
  
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.TRANSPARENT);
            Paint p = new Paint();
            Typeface font = Typeface.create("sans", Typeface.NORMAL);
            p.setColor(Color.RED);
            p.setTypeface(font);
            p.setTextSize(16);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.drawText(top, srcWidth/10, srcHeight/10, p);
            canvas.drawText(bot, srcWidth/10, srcHeight-srcHeight/20, p);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
 
        return bitmap;
    }
    

	 /**
    * 在二维码中间添加Logo图案
    */
	public Bitmap addLogo(Bitmap src, Bitmap logo) {
       if (src == null) {
           return null;
       }

       if (logo == null) {
           return src;
       }

       //获取图片的宽高
       int srcWidth = src.getWidth();
       int srcHeight = src.getHeight();
       int logoWidth = logo.getWidth();
       int logoHeight = logo.getHeight();

       if (srcWidth == 0 || srcHeight == 0) {
           return null;
       }

       if (logoWidth == 0 || logoHeight == 0) {
           return src;
       }

       //logo大小为二维码整体大小的1/5
       float scaleFactor = srcWidth * 1.0f / 6 / logoWidth;
       Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
       try {
           Canvas canvas = new Canvas(bitmap);
           canvas.drawBitmap(src, 0, 0, null);
           canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
           canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

           canvas.save(Canvas.ALL_SAVE_FLAG);
           canvas.restore();
       } catch (Exception e) {
           bitmap = null;
           e.getStackTrace();
       }
               
       return bitmap;
   }
	
	public Bitmap CreateBmp(int widthAndHeight,String top,String bot) {
	    Bitmap bitmap = Bitmap.createBitmap(widthAndHeight, widthAndHeight, Bitmap.Config.ARGB_8888);
	   
	    try {
	        Canvas canvas = new Canvas(bitmap);
	        canvas.drawColor(Color.WHITE);
	        Paint p = new Paint();
	        Typeface font = Typeface.create("sans", Typeface.NORMAL);
	        p.setColor(Color.RED);
	        p.setTypeface(font);
	        p.setTextSize(16);
	        canvas.drawBitmap(bitmap, 0, 0, null);
	        canvas.drawText(top, widthAndHeight/10, widthAndHeight/10, p);
	        canvas.drawText(bot, widthAndHeight/10, widthAndHeight-widthAndHeight/20, p);
	        canvas.save(Canvas.ALL_SAVE_FLAG);
	        canvas.restore();
	    } catch (Exception e) {
	        bitmap = null;
	        e.getStackTrace();
	    }
	           
	   return bitmap;
    }
}
