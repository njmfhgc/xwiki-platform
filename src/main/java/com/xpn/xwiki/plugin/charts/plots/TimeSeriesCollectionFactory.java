package com.xpn.xwiki.plugin.charts.plots;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.NotImplementedException;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.xpn.xwiki.plugin.charts.exceptions.DataSourceException;
import com.xpn.xwiki.plugin.charts.exceptions.GenerateException;
import com.xpn.xwiki.plugin.charts.params.ChartParams;
import com.xpn.xwiki.plugin.charts.source.DataSource;

public class TimeSeriesCollectionFactory {
	private static TimeSeriesCollectionFactory uniqueInstance = new TimeSeriesCollectionFactory();
	
	private TimeSeriesCollectionFactory() {
		// empty
	}

	public static TimeSeriesCollectionFactory getInstance() {
		return uniqueInstance;
	}

	public XYDataset create(DataSource dataSource, ChartParams params)
			throws GenerateException, DataSourceException {
		String dataSeries = params.getString(ChartParams.SERIES);
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		Class timePeriodClass = params.getClass(ChartParams.TIME_PERIOD_CLASS);
		if (timePeriodClass == null) {
			timePeriodClass = Day.class;
		}
		DateFormat format = params.getDateFormat(ChartParams.DATE_FORMAT);
		if (format == null) {
			format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		}
		
		if (dataSeries.equals("columns")) {
			
			throw new NotImplementedException(); // TODO!!!
			
		} else if (dataSeries.equals("rows")) {
			if (!dataSource.hasHeaderRow()) {
				throw new GenerateException("Header row required");
			}
			for (int row = 0; row<dataSource.getRowCount(); row++) {
				String seriesName;
				if (dataSource.hasHeaderColumn()) {
					seriesName = dataSource.getHeaderColumnValue(row);
				} else {
					seriesName = "";
				}

				TimeSeries series = new TimeSeries(seriesName, timePeriodClass);
				
				for (int column = 0; column<dataSource.getColumnCount(); column++) {
					RegularTimePeriod period;
					try {
						Date date = format.parse(dataSource.getHeaderRowValue(column));
						Constructor ctor = timePeriodClass.getConstructor(new Class[] {Date.class});
						period = (RegularTimePeriod)ctor.newInstance(new Object[] {date});
					} catch (Exception e) {
						throw new GenerateException(e);
					}
					series.add(period, dataSource.getCell(row, column));
				}
				dataset.addSeries(series);
			}
		} else {
			throw new GenerateException("Invalid series parameter:"+dataSeries);
		}
		return dataset;
	}
}
