package com.yasin.measuredemo.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by leo on 17/5/11.
 */

public class MeasureView extends View {
    //弧形开始的角度
    private static final int startAngle = 180;
    //弧面所跨的弧度
    private static final int sweepAngle = 180;
    //里面数字的单位
    private static final String unint = "mmhg";
    //每隔多少画一个刻度
    private static final int angPre = 2;
    //总刻度
    private static final int totalDial = 90;
    //进度条的底色
    private static final int PROGRESS_COLOR = 0x55000000;
    //画普通的线用的笔
    private Paint linePaint;
    //画文字用的笔
    private Paint textPaint;
    //画进度条用的笔
    private Paint progressPaint;
    //里面半圆的半径
    private int innerRadius = dp2px(100);
    //最内层的padding
    private int innerPadding = dp2px(6);
    //外两层的padding
    private int outerPadding = dp2px(10);
    //进度条的宽度
    private int progressLineW = dp2px(8);
    //最里面跟最外面的线的宽度
    private int innerLineWidth = dp2px(1);
    //刻度线的宽度
    private int outerLineWidth = dp2px(2);
    //刻度线的高度
    private int outerLineHeight = dp2px(10);
    //文字的size
    private float textSize = sp2px(18);
    //单位文字的size
    private float textSizeUnit = sp2px(13);

    //当前进度
    private float currProgress = 0.5f;
    //起始值
    private float start = 0;
    //最终值
    private float end = 150;

    public MeasureView(Context context) {
        this(context, null);
    }

    public MeasureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeasureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.WHITE);
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(Color.WHITE);
        progressPaint.setStrokeWidth(progressLineW);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //最里面圆的半径计算
        //半径＝控件的宽度／2-最里层的padding－刻度线的高度－里第二层的padding－进度条的宽度－第二层padding－最外层线宽度
        innerRadius = getMeasuredWidth() / 2 - innerPadding - outerLineHeight -
                outerPadding - progressLineW - outerPadding - innerLineWidth;
        //高度＝刻度线的高度＋padding＋最里面一层的半径＋padding＋进度条宽度＋padding＋最外层线宽度
        int height = (outerLineHeight + innerPadding + innerRadius + outerPadding + progressLineW + outerPadding + innerLineWidth);
        //重新生成高度
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画出最里面的弧线
        drawInnerLine(canvas);
        drawDial(startAngle, sweepAngle, totalDial, angPre, outerLineHeight, outerLineHeight / 2, innerRadius + innerPadding + outerLineHeight, canvas);
        drawText(canvas);
        drawOuterStaticLine(canvas);
        drawProgress(canvas);
    }

    private void drawOuterStaticLine(Canvas canvas) {
        //最外层的弧线
        RectF rectF1 = new RectF();
        int width = getWidth();
        rectF1.set(innerLineWidth, innerLineWidth, width - innerLineWidth, getHeight() * 2 - innerLineWidth);
        linePaint.setStrokeWidth(innerLineWidth);
        canvas.drawArc(rectF1, startAngle, sweepAngle, false, linePaint);
        //静态的进度条
        progressPaint.setColor(PROGRESS_COLOR);
        RectF rectF2 = new RectF();
        rectF2.set(innerLineWidth + outerPadding + outerPadding / 2, innerLineWidth + outerPadding + outerPadding / 2,
                width - (innerLineWidth + outerPadding + outerPadding / 2), getHeight() * 2 - ((innerLineWidth + outerPadding + outerPadding / 2)));
        canvas.drawArc(rectF2, startAngle, sweepAngle, false, progressPaint);

    }

    /**
     * 画进度条
     *
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        int width = getWidth();
        //设置画笔颜色为白色
        progressPaint.setColor(Color.WHITE);
        //确定矩形范围
        RectF rectF2 = new RectF();
        rectF2.set(innerLineWidth + outerPadding + outerPadding / 2, innerLineWidth + outerPadding + outerPadding / 2,
                width - (innerLineWidth + outerPadding + outerPadding / 2), getHeight() * 2 - ((innerLineWidth + outerPadding + outerPadding / 2)));
        //画出弧度 弧线扫过的角度（startangle＋（endangle－startangle）＊progress）
        canvas.drawArc(rectF2, startAngle, sweepAngle * currProgress, false, progressPaint);
    }

    /**
     * 画文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        //当前文字对应的值为（0+（150-0）＊当前进度）
        String currText = String.valueOf((int) (start + (end - start) * currProgress));
        //因为数字字体大而单位数字小
        textPaint.setTextSize(textSize);
        //测量数字文字对应的长度
        float numWidth = textPaint.measureText("" + currText);
        //重新设置笔的size
        textPaint.setTextSize(textSizeUnit);
        //为了获取单位文字的高度
        Rect rect = new Rect();
        //获取单位文字的最小矩形范围
        textPaint.getTextBounds(unint, 0, unint.length(), rect);
        //单位文字的宽度
        float unitWidth = textPaint.measureText(unint);
        //从新设置笔的大小
        textPaint.setTextSize(textSize);
        //文字的basex为（控件的宽度／2-（数字文字的长度＋单位文字的长度）／2）
        float baseX = getWidth() / 2 - (numWidth + unitWidth) / 2;
        //文字的centery为（最外层线的宽度＋padding＋进度条宽度＋padding＋padding＋最里面半圆半径的一半）
        float centerY = innerLineWidth + outerPadding + progressLineW + outerPadding + outerLineHeight + innerPadding + innerRadius / 2;
        //（主要解决文字在半圆的中心文字）根据centery算出文字的basey
        float baseY = centerY - (textPaint.ascent() + textPaint.descent()) / 2;
        //设置数字文字为粗体
        textPaint.setFakeBoldText(true);
        //画出数字文字
        canvas.drawText(currText + "", baseX, baseY, textPaint);
        //重新设置画笔
        textPaint.setTextSize(textSizeUnit);
        textPaint.setFakeBoldText(false);
        //画出单位文字（跟数字文字底部有一个偏移量所以basey－了一个（单位文字的高度的1/6））
        canvas.drawText(unint, baseX + numWidth + dp2px(1), baseY - rect.height() / 6, textPaint);
    }

    /**
     * 画刻度盘
     */
    private void drawDial(int startAngle, int allAngle, int dialCount, int per, int longLength, int shortLength, int radius, Canvas canvas) {
        linePaint.setStrokeWidth(outerLineWidth);
        int length;
        int angle;
        //根据需要显示的刻度总个数遍历
        for (int i = 0; i <= dialCount; i++) {
            //每一个刻度对应的起始角度为180度＋（总度数／个数）＊对应刻度的位置
            angle = (int) ((allAngle) / (dialCount * 1f) * i) + startAngle;
            //线条的起始点位置
            int[] startP;
            //线条的end点的位置
            int[] endP;
            //当i％per＝＝0，每一个需要显示短刻度的时候（因为设计稿第一个为短的刻度条）
            if (i % per == 0) {
                //短刻度条的长度为长刻度条的一半
                length = shortLength;
                //获取刻度条起始点位置
                startP = getPointFromAngleAndRadius(angle, radius - length);
                endP = getPointFromAngleAndRadius(angle, radius - length * 2);
            } else {
                length = longLength;
                startP = getPointFromAngleAndRadius(angle, radius);
                endP = getPointFromAngleAndRadius(angle, radius - length);
            }
            //画出对应的刻度条
            canvas.drawLine(startP[0], startP[1], endP[0], endP[1], linePaint);
        }
    }

    /**
     * 根据刻度条相应的角度算出点位置
     * @param angle
     * @param radius
     * @return
     */
    private int[] getPointFromAngleAndRadius(int angle, int radius) {
        //根据三角函数公式可以知道，横坐标值为（刻度条＋innnerradius）也就是刻度条对应圆的半径
        //乘以一个cos（angle），因为我们是以（getWidth() / 2，控件的高度）位置建的坐标系
        //而真正的坐标系的位置为控件左上角，所以算出的值后需要＋getWidth() / 2或者getHeight()
        double x = radius * Math.cos(angle * Math.PI / 180) + getWidth() / 2;
        double y = radius * Math.sin(angle * Math.PI / 180) + getHeight();
        return new int[]{(int) x, (int) y};
    }

    private void drawInnerLine(Canvas canvas) {
        //定义一个矩形区域
        RectF rectF = new RectF();
        int width = getWidth();
        //矩形的top为刻度线的高度＋padding＋最外层弧线线宽度＋padding＋padding＋进度条宽度
        //left为控件的宽度／2－半圆半径
        int top = outerLineHeight + innerPadding + innerLineWidth + outerPadding * 2 + progressLineW;
        rectF.set(width / 2 - innerRadius, top, width / 2 + innerRadius, top + innerRadius * 2);
        linePaint.setStrokeWidth(innerLineWidth);
        //弧线的起始位置为180度的位置，扫过的弧度为180
        canvas.drawArc(rectF, startAngle, sweepAngle, false, linePaint);
    }

    public void setProgress(float progress) {
        if (currProgress == progress) {
            return;
        }
        this.currProgress = progress;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public float sp2px(float value) {
        Context c = getContext();
        Resources r;
        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, value, r.getDisplayMetrics());
    }
}
