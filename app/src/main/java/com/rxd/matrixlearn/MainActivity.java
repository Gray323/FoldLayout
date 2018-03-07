package com.rxd.matrixlearn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fold_slide);
    }

    class PolyToPolyView extends View{

        private static final int NUM_OF_POINT = 8;
        private int mTranslateDis;//图片折叠后的总宽度
        private float mFactor = 0.8f;//折叠后的总宽度与原图宽度的比例
        private int mNumOfFloads = 24;//折叠块的个数
        private Matrix[] mMatrices = new Matrix[mNumOfFloads];
        private Bitmap mBitmap;
        private Paint mSolidPaint;//绘制黑色透明区域
        private Paint mShadowPaint;//绘制阴影
        private Matrix mShadowGradientMatrix;
        private LinearGradient mShadowGradientShader;
        private int mFloadWidth;//原图每块的宽度
        private int mTranslateDisPerFlod;//折叠时，每块的宽度

        public PolyToPolyView(Context context) {
            super(context);
            mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.wenzi);

            //折叠后的总宽度
            mTranslateDis = (int) (mBitmap.getWidth() * mFactor);
            //原图每块的宽度
            mFloadWidth = mBitmap.getWidth() / mNumOfFloads;
            //折叠后每块原图的宽度
            mTranslateDisPerFlod = mTranslateDis / mNumOfFloads;

            //初始化matrix
            for (int i = 0; i < mNumOfFloads; i++){
                mMatrices[i] = new Matrix();
            }

            mSolidPaint = new Paint();
            int alpha = (int) (255 * mFactor * 0.8f);
            mSolidPaint.setColor(Color.argb((int) (alpha * 0.8), 0, 0, 0));

            mShadowPaint = new Paint();
            mShadowPaint.setStyle(Paint.Style.FILL);
            mShadowGradientShader = new LinearGradient(0, 0, 0.5f, 0,
                    Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mShadowGradientShader);
            mShadowGradientMatrix = new Matrix();
            mShadowGradientMatrix.setScale(mFloadWidth, 1);
            mShadowGradientShader.setLocalMatrix(mShadowGradientMatrix);
            mShadowPaint.setAlpha(alpha);

            int depth = (int) (Math.sqrt(mFloadWidth * mFloadWidth - mTranslateDisPerFlod * mTranslateDisPerFlod)) / 2;
            float[] src = new float[NUM_OF_POINT];
            float[] dst = new float[NUM_OF_POINT];

            for (int i = 0; i < mNumOfFloads; i++){
                src[0] = i * mFloadWidth;
                src[1] = 0;
                src[2] = src[0] + mFloadWidth;
                src[3] = 0;
                src[4] = src[2];
                src[5] = mBitmap.getHeight();
                src[6] = src[0];
                src[7] = src[5];

                boolean isEven = i % 2 == 0;

                dst[0] = i * mTranslateDisPerFlod;
                dst[1] = isEven ? 0 : depth;
                dst[2] = dst[0] + mTranslateDisPerFlod;
                dst[3] = isEven ? depth : 0;
                dst[4] = dst[2];
                dst[5] = isEven ? mBitmap.getHeight() - depth : mBitmap.getHeight();
                dst[6] = dst[0];
                dst[7] = isEven ? mBitmap.getHeight() : mBitmap.getHeight() - depth;

                mMatrices[i].setPolyToPoly(src, 0, dst, 0, src.length >> 1);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 0; i < mNumOfFloads; i++){
                canvas.save();
                canvas.concat(mMatrices[i]);
                canvas.clipRect(mFloadWidth * i, 0, mFloadWidth * i + mFloadWidth, mBitmap.getHeight());
                canvas.drawBitmap(mBitmap, 0, 0, null);
                canvas.translate(mFloadWidth * i, 0);
                if (i % 2 == 0){
                    //绘制黑色遮盖
                    canvas.drawRect(0, 0, mFloadWidth, mBitmap.getHeight(), mSolidPaint);
                }else{
                    //绘制阴影
                    canvas.drawRect(0, 0, mFloadWidth, mBitmap.getHeight(), mShadowPaint);
                }
                canvas.restore();
            }
        }
    }

}
