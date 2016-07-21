package com.pigdogbay.weightrecorder;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.pigdogbay.weightrecorder.model.ChartAxesRanges;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ReadingsChart
{
	private static final float READINGS_LINE_WIDTH_DP = 2f;
	private static final float TREND_LINE_WIDTH_DP = 1.8f;
	private static final float LABELS_TEXT_SIZE_SP = 14f;
	private static final float AXIS_TITLE_TEXT_SIZE_SP = 20f;
	private static final int POINT_SIZE_DP = 3;
	XYMultipleSeriesRenderer _Renderer;
	XYMultipleSeriesDataset _Dataset;
	Context _Context;
	public ReadingsChart(Context context)
	{
		_Context = context;
		_Renderer = new XYMultipleSeriesRenderer();
		_Renderer.setShowLegend(false);
		_Renderer.setPanEnabled(true);
		_Renderer.setZoomEnabled(true);
		_Renderer.setBackgroundColor(Color.BLACK);
		_Renderer.setApplyBackgroundColor(true);
		_Renderer.setAxesColor(Color.LTGRAY);
		_Renderer.setLabelsColor(Color.LTGRAY);
		_Renderer.setShowGrid(true);
		_Renderer.setXLabelsAlign(Align.CENTER);
		_Renderer.setYLabelsAlign(Align.RIGHT);
		_Renderer.setZoomButtonsVisible(true);

		_Dataset = new XYMultipleSeriesDataset();
		
	}
	public void setXAxisTitle(String title)
	{
		_Renderer.setXTitle(title);
	}

	public void setYAxisTitle(String title)
	{
		_Renderer.setYTitle(title);
	}
	public void setChartAxesRanges(ChartAxesRanges chartAxesRanges)
	{
		_Renderer.setXAxisMin(chartAxesRanges.XAxisMin);
		_Renderer.setXAxisMax(chartAxesRanges.XAxisMax);
		_Renderer.setYAxisMin(chartAxesRanges.YAxisMin);
		_Renderer.setYAxisMax(chartAxesRanges.YAxisMax);
	}
	
	public void addReadings(TimeSeries series)
	{
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.GREEN);
		r.setPointStyle(PointStyle.CIRCLE);
		r.setFillPoints(true);
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, READINGS_LINE_WIDTH_DP, _Context.getResources().getDisplayMetrics());
		r.setLineWidth(size);
		_Renderer.addSeriesRenderer(r);
		_Dataset.addSeries(series);
	}
	public void addTrend(TimeSeries series)
	{
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.RED);
		r.setPointStyle(PointStyle.TRIANGLE);
		r.setFillPoints(true);
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TREND_LINE_WIDTH_DP, _Context.getResources().getDisplayMetrics());
		r.setLineWidth(size);
		_Renderer.addSeriesRenderer(r);
		_Dataset.addSeries(series);
	}
	public void addTarget(TimeSeries series)
	{
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.CYAN);
		r.setPointStyle(PointStyle.SQUARE);
		r.setFillPoints(true);
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TREND_LINE_WIDTH_DP, _Context.getResources().getDisplayMetrics());
		r.setLineWidth(size);
		_Renderer.addSeriesRenderer(r);
		_Dataset.addSeries(series);
	}
	private void fitDisplay()
	{
		DisplayMetrics metrics = _Context.getResources().getDisplayMetrics();
		int screenSize = _Context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, POINT_SIZE_DP, metrics);
		_Renderer.setPointSize(size);
		size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, AXIS_TITLE_TEXT_SIZE_SP, metrics);
		_Renderer.setAxisTitleTextSize(size);
		size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, LABELS_TEXT_SIZE_SP, metrics);
		_Renderer.setLabelsTextSize(size);
		//Default large screen values
		float marginLeft=48f,marginBottom=10f;
		_Renderer.setXLabels(10);
		_Renderer.setYLabels(10);
		if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL)
		{
			_Renderer.setXLabels(4);
			_Renderer.setYLabels(4);
			marginLeft = 30f;
			marginBottom = 16f;
		}
		else if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL)
		{
			_Renderer.setXLabels(6);
			_Renderer.setYLabels(6);
			marginLeft = 44f;
			marginBottom = 16f;
		}
		marginLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginLeft, metrics);
		marginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginBottom, metrics);
		// Top, left, bottom, right
		_Renderer.setMargins(new int[]{0,(int)marginLeft,(int)marginBottom,0});

	}
	public GraphicalView createView()
	{
		fitDisplay();
		return ChartFactory.getTimeChartView(_Context, _Dataset, _Renderer,"d MMM");

	}
}
