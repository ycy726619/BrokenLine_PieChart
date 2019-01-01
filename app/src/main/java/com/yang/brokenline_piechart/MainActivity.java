package com.yang.brokenline_piechart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yang.brokenline_piechart.chatview.BrokenLinePieChartView;
import com.yang.brokenline_piechart.chatview.BrokenLinePieChartView.PieceDataHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.pc_view)
    BrokenLinePieChartView pcView;


    //饼状图数据源
    private List<PieceDataHolder> pieceDataHolders = new ArrayList<>();

    private float value1,value2,value3,value4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initPieChart();
    }

    /**
     * 初始化折线饼状图
     */
    private void initPieChart() {
        value1 =120f;
        value2 =220f;
        value3 =320f;
        value4 =420f;
        //添加数据源
        pieceDataHolders.add(new PieceDataHolder(value1, getResources().getColor(R.color.pb_01), getPercent(value1)));
        pieceDataHolders.add(new PieceDataHolder(value2, getResources().getColor(R.color.pb_02), getPercent(value2)));
        pieceDataHolders.add(new PieceDataHolder(value3, getResources().getColor(R.color.pb_03), getPercent(value3)));
        pieceDataHolders.add(new PieceDataHolder(value4, getResources().getColor(R.color.pb_04), getPercent(value4)));
        //设置数据源
        pcView.setData(pieceDataHolders);
    }

    /**
     * 格式化百分比
     * @param value
     * @return
     */
    private String getPercent(float value) {
        return new String(value/(value1+value2+value3+value4)*100+"%");
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
