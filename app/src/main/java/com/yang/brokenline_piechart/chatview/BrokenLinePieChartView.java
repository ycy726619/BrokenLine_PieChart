package com.yang.brokenline_piechart.chatview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.yang.brokenline_piechart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 折线饼状图
 * email: isNaNFor_MrYang@163.com
 */
public class BrokenLinePieChartView extends View {

    //绘制文字对象
    private TextPaint mTextPaint;
    //绘制文字高度
    private float mTextHeight;
    //饼图半径
    private float pieChartCircleRadius = 100;
    //绘制文字中最低字符高度和基线之间距离
    private float textBottom;
    //记录文字大小
    private float mTextSize;
    //饼图所占矩形区域（不包括文字）
    private RectF pieChartCircleRectF = new RectF();
    //饼状图信息列表
    private List<PieceDataHolder> pieceDataHolders = new ArrayList<>();
    //斜直线长度
    private final float brkenR = 35f;


    public BrokenLinePieChartView(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * 初始化
     *
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {
        // 获取本地attr.xml文件列表
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BrokenLinePieChartView, defStyle, 0);

        //设置饼图半径绘制样式
        pieChartCircleRadius = a.getDimension(
                R.styleable.BrokenLinePieChartView_circleRadius,
                pieChartCircleRadius);
        //设置绘制文字样式  默认12sp
        mTextSize = a.getDimension(R.styleable.BrokenLinePieChartView_textSize, 12);

        //初始化并回收TypedArray对象
        a.recycle();

        // 初始化文字绘制对象
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        invalidateTextPaintAndMeasurements();
    }

    /**
     * 初始化绘制文字画笔
     */
    private void invalidateTextPaintAndMeasurements() {

        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, getContext().getResources().getDisplayMetrics()));

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.descent - fontMetrics.ascent;
        textBottom = fontMetrics.bottom;
    }

    public BrokenLinePieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BrokenLinePieChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * 设置饼状图的半径
     *
     * @param pieChartCircleRadius 饼状图的半径（px）
     */
    public void setPieChartCircleRadius(int pieChartCircleRadius) {

        this.pieChartCircleRadius = pieChartCircleRadius;

        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initPieChartCircleRectF();

        drawAllSectors(canvas);

    }

    /**
     * 初始化绘制饼状图扇形弧度
     */
    private void initPieChartCircleRectF() {
        pieChartCircleRectF.left = getWidth() / 2 - pieChartCircleRadius;
        pieChartCircleRectF.top = getHeight() / 2 - pieChartCircleRadius;
        pieChartCircleRectF.right = pieChartCircleRectF.left + pieChartCircleRadius * 2;
        pieChartCircleRectF.bottom = pieChartCircleRectF.top + pieChartCircleRadius * 2;
    }

    /**
     * 开始绘制
     *
     * @param canvas 画布
     */
    private void drawAllSectors(Canvas canvas) {
        //计算数据总和
        float sum = 0f;
        for (PieceDataHolder pieceDataHolder : pieceDataHolders) {
            sum += pieceDataHolder.value; //记录传入扇形的值的大小
        }
        //每块扇形所绘制的角度
        float sum2 = 0f;
        for (PieceDataHolder pieceDataHolder : pieceDataHolders) {
            float startAngel = sum2 / sum * 360;
            sum2 += pieceDataHolder.value;
            float sweepAngel = pieceDataHolder.value / sum * 360; //绘制结束角度
            //绘制扇形
            drawSector(canvas, pieceDataHolder.color, startAngel, sweepAngel);
            //绘制标记线
            drawMarkerLineAndText(canvas, pieceDataHolder.color, startAngel + sweepAngel / 2, pieceDataHolder.marker);
        }
    }

    /**
     * 绘制扇形
     *
     * @param canvas     画布
     * @param color      要绘制扇形的颜色
     * @param startAngle 起始角度
     * @param sweepAngle 结束角度
     */
    protected void drawSector(Canvas canvas, int color, float startAngle, float sweepAngle) {
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawArc(pieChartCircleRectF, startAngle, sweepAngle, true, paint);
    }

    /**
     * 绘制标注线和标记文字
     *
     * @param canvas      画布
     * @param color       标记的颜色
     * @param rotateAngel 每块扇形弧边中心点角度
     */
    protected void drawMarkerLineAndText(Canvas canvas, int color, float rotateAngel, String text) {
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK); //绘制线和文字颜色

        //初始化绘制路径对象
        Path path = new Path();
        //绘制起点横坐标
        final float x = (float) (getWidth() / 2 + pieChartCircleRadius * Math.cos(Math.toRadians(rotateAngel)));
        //绘制起点纵坐标
        final float y = (float) (getHeight() / 2 + pieChartCircleRadius * Math.sin(Math.toRadians(rotateAngel)));
        //折线绘制横坐标
        final float brokenX = (float) (brkenR * Math.cos(45));
        //折线绘制纵坐标
        final float brokenY = (float) (brkenR * Math.cos(45));
        Log.d("drawMarkerLineAndText", "brokenX: " + brokenX + "  brokenY:" + brokenY);
        //设置绘制线条对象起点坐标
        path.moveTo(x, y);
        //获取绘制文字宽度
        float textWidth = mTextPaint.measureText(text);
        switch (brokenLineDeflectionAngle(rotateAngel)) {
            case 1://向右水平绘制(0°|360°)
                path.lineTo(getWidth(), y);
                canvas.drawText(text, getWidth() - textWidth, y + mTextHeight, mTextPaint);
                break;
            case 2://向左水平绘制(180°)
                path.lineTo(0, y);
                canvas.drawText(text, 0, y + mTextHeight, mTextPaint);
                break;
            case 3://先向右下方45°绘制,再向右水平绘制
                //绘制向右下方45°直线
                path.lineTo(brokenX + x, y + brokenY);
                //重置坐标为折线末尾
                path.moveTo(brokenX + x, y + brokenY);
                //绘制向右水平直线
                path.lineTo(getWidth(), y + brokenY);
                canvas.drawText(text, getWidth() - textWidth, y + brokenY + mTextHeight, mTextPaint);
                break;
            case 4://先向左上方45°绘制，再向左水平绘制
                //绘制向左上方45°直线
                path.lineTo(x - brokenX, y - brokenY);
                //重置坐标为折线末尾
                path.moveTo(x - brokenX, y - brokenY);
                //绘制向右水平直线
                path.lineTo(0, y - brokenY);
                canvas.drawText(text, 0, y - brokenY + mTextHeight, mTextPaint);
                break;
            case 5://先向左下方45°绘制，再向左水平绘制
                //绘制向左下方45°直线
                path.lineTo(x - brokenX, y + brokenY);
                //重置坐标为折线末尾
                path.moveTo(x - brokenX, y + brokenY);
                //绘制向右水平直线
                path.lineTo(0, y + brokenY);
                canvas.drawText(text, 0, y + brokenY + mTextHeight, mTextPaint);
                break;
            case 6://先向右上方45°绘制，再向右水平绘制
                //绘制向右上方45°直线
                path.lineTo(brokenX + x, y - brokenY);
                //重置坐标为折线末尾
                path.moveTo(brokenX + x, y - brokenY);
                //绘制向右水平直线
                path.lineTo(getWidth(), y - brokenY);
                canvas.drawText(text, getWidth() - textWidth, y - brokenY + mTextHeight, mTextPaint);
                break;
            default:
                break;
        }
        canvas.drawPath(path, paint);

    }

    /**
     * 计算折线偏向方向
     *
     * @param rotateAngel 绘制角度
     * @return
     */
    private int brokenLineDeflectionAngle(float rotateAngel) {
        if (rotateAngel == 0f || rotateAngel == 360f) {
            return 1;
        } else if (rotateAngel == 180f) {
            return 2;
        } else if (0f < rotateAngel && rotateAngel <= 90f) {
            return 3;
        } else if (180f < rotateAngel && rotateAngel <= 270f) {
            return 4;
        } else if (90f < rotateAngel && rotateAngel < 180f) {
            return 5;
        } else if (270f < rotateAngel && rotateAngel < 360f) {
            return 6;
        }

        return 0;
    }


    public float getTextSize() {
        return mTextSize;
    }


    public void setTextSize(float textSize) {
        mTextSize = textSize;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * 设置饼状图要显示的数据
     *
     * @param data 列表数据
     */
    public void setData(List<PieceDataHolder> data) {
        if (data != null) {
            pieceDataHolders.clear();
            pieceDataHolders.addAll(data);
        }
        invalidate();
    }

    /**
     * 饼状图每块的信息持有者
     */
    public static final class PieceDataHolder {
        //每块扇形的值的大小
        private float value;
        //扇形的颜色
        private int color;
        //每块的标记
        private String marker;
        public PieceDataHolder(float value, int color, String marker) {
            this.value = value;
            this.color = color;
            this.marker = marker;
        }
    }

}

